package com.mysaasa.core.blog.services;

import com.mysaasa.core.blog.model.BlogPost;
import com.mysaasa.core.categories.model.Category;
import com.mysaasa.core.users.model.User;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.Simple;
import com.mysaasa.core.blog.model.BlogComment;
import com.mysaasa.core.messaging.services.MessagingService;
import com.mysaasa.core.organization.model.Organization;
import com.mysaasa.core.security.services.session.AdminSession;
import com.mysaasa.core.messaging.model.Message;
import org.apache.commons.collections.ListUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * service to manage BlogTemplateService Data, to keep it all DA consistent in the module.
 */
@SimpleService
public class BlogService {

	public BlogService() {}

	public static BlogService get() {
		return Simple.get().getInjector().getProvider(BlogService.class).get();
	}

	public List<BlogPost> getBlogPostsByCategory(Organization organization, Category c, String order, String direction) {
		List<Category> l = new ArrayList();
		l.add(c);
		return getBlogPostsByCategory(organization, l, 0, 10, order, direction);
	}

	public List<BlogPost> getBlogPostsByCategory(Organization organization, Category c, String order, String direction, int page, int take) {
		List<Category> l = new ArrayList();
		l.add(c);
		return getBlogPostsByCategory(organization, l, page, take, order, direction);
	}

	public List<BlogPost> getBlogPostsByCategory(Organization organization, Category c) {
		List<Category> l = new ArrayList();
		l.add(c);
		return getBlogPostsByCategory(organization, l, 0, 10, "dateCreated", "DESC");
	}

	/**
	 * Untested, but it should get a bunch of blog posts where they belong to multiple categories.
	 *
	 * @param organization
	 * @param cats
	 * @return
	 */
	public List<BlogPost> getBlogPostsByCategory(Organization organization, List<Category> cats, int page, int page_size, String order, String direction) {

		checkNotNull(cats);
		if (cats.size() == 0)
			return getBlogPosts(organization, page, page_size, order, direction);
		List<BlogPost> results = null;
		EntityManager em = Simple.getEm();
		String whereClause = "";

		try {
			if (order == null)
				order = "dateCreated";
			if (direction == null)
				direction = "DESC";
			String sql = "select bp from BlogPost bp WHERE $whereClause bp.organization=:organization ORDER BY " + order + " " + direction;
			for (int i = 0; i < cats.size(); i++) {
				whereClause += " :category" + i + " member of bp.categories AND ";
			}
			sql = sql.replace("$whereClause", whereClause);
			Query query = em.createQuery(sql).setParameter("organization", organization);
			for (int i = 0; i < cats.size(); i++) {
				query.setParameter("category" + i, cats.get(i));
			}
			query.setFirstResult(page_size * page);
			query.setMaxResults(page_size);
			results = query.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		}
		em.close();
		return results;
	}

	/**
	 * Get's a blog post
	 *
	 * @param id
	 * @return The Blogpost, null if 0, Exception otherwise (0 is used by system for editing/new records)
	 */
	public BlogPost getBlogPostById(long id) {
		if (id == 0)
			return null;
		EntityManager em = Simple.getEm();
		List<BlogPost> results = ListUtils.unmodifiableList(em.createQuery("SELECT B FROM BlogPost B WHERE B.id=:id").setParameter("id", id).getResultList());
		em.close();
		if (results.size() == 0) {
			throw new IllegalStateException("Could not find this blog post '" + id + "'");
		}
		return results.get(0);
	}

	public List<BlogPost> getBlogPosts(Organization organization, int page, int page_size, String order, String direction) {
		checkNotNull(organization);
		EntityManager em = Simple.getEm();
		Query query = em.createQuery("SELECT B FROM BlogPost B WHERE B.organization=:organization ORDER BY " + order + " " + direction).setParameter("organization", organization);
		query.setFirstResult(page * page_size);
		query.setMaxResults(page_size);
		List<BlogPost> results = ListUtils.unmodifiableList(query.getResultList());

		for (BlogPost bp : results)
			bp.getCategories();
		em.close();
		return results;
	}

	/**
	 * Get's a list of blog comments for a blog post
	 *
	 * @param post_id
	 * @param count
	 * @return
	 */
	public List<BlogComment> getTopLevelBlogComments(long post_id, int count) {

		EntityManager em = Simple.getEm();
		BlogPost post = BlogService.get().getBlogPostById(post_id);
		List<BlogComment> results = em.createQuery("SELECT B FROM BlogComment B WHERE B.post=:post AND B.parent is null").setParameter("post", post).getResultList();
		Collections.reverse(results);
		em.close();

		if (count != 0 && results.size() >= count)
			return results.subList(0, count);
		else
			return results;
	}

	public List<BlogComment> getBlogComments(long post_id, int count) {

		EntityManager em = Simple.getEm();
		BlogPost post = BlogService.get().getBlogPostById(post_id);
		List<BlogComment> results = em.createQuery("SELECT B FROM BlogComment B WHERE B.post=:post").setParameter("post", post).getResultList();
		Collections.reverse(results);
		em.close();

		if (count != 0 && results.size() >= count)
			return results.subList(0, count);
		else
			return results;
	}

	public void deleteBlogPost(BlogPost blogPost) {
		EntityManager em = Simple.getEm();
		em.getTransaction().begin();
		BlogPost tracked = em.merge(blogPost);
		for (BlogComment bc : getTopLevelBlogComments(tracked.getId(), 0)) {
			deleteBlogComment(bc);

		}
		em.remove(tracked);
		em.flush();
		em.getTransaction().commit();
		em.close();
	}

	private void deleteBlogComment(BlogComment comment) {
		EntityManager em = Simple.getEm();
		BlogComment tracked = em.merge(comment);
		em.getTransaction().begin();
		for (BlogComment bc : getBlogComments(tracked)) {
			deleteBlogComment(bc);
		}

		em.remove(tracked);
		em.flush();
		em.getTransaction().commit();
		em.close();
	}

	public BlogPost saveBlogPost(BlogPost blogPost) {
		if (blogPost == null)
			throw new IllegalArgumentException("postToBlog");
		if (AdminSession.get() != null && AdminSession.get().getContext() != null && AdminSession.get().getContext().getUser() != null) {
			if (blogPost.getAuthor() == null)
				blogPost.setAuthor(AdminSession.get().getContext().getUser());
		}
		EntityManager em = Simple.getEm();
		em.getTransaction().begin();
		BlogPost tracked = em.merge(blogPost);
		em.flush();
		em.getTransaction().commit();
		em.close();
		return tracked;
	}

	public BlogComment saveBlogComment(BlogComment blogComment) {
		checkNotNull(blogComment);
		EntityManager em = Simple.getEm();
		em.getTransaction().begin();
		BlogComment tracked = em.merge(blogComment);
		em.flush();
		em.getTransaction().commit();
		em.close();

		//This a reply, let's create a Reply message
		if (blogComment.parent != null) {
			Message message = new Message();
			//message.setBody("You have received a reply to your message \""+blogComment.parent.getContent()+"\" comment from "+blogComment.author.getIdentifier());
			message.setBody(blogComment.author.identifier + " says \"" + blogComment.content + "\" in response to your comment \"" + blogComment.parent.getContent() + "\"");
			message.setData("{type:\"Reply\",comment_id:\"" + tracked.getId() + "\",blogpost_id:" + blogComment.getPost().getId() + "}");
			message.setRecipient(blogComment.parent.author);
			message.setTitle("You have received a reply to your comment");
			MessagingService.get().saveMessage(message, true);
		}

		return tracked;
	}

	public BlogComment getBlogCommentById(long id) {
		EntityManager em = Simple.getEm();
		List<BlogComment> results = ListUtils.unmodifiableList(em.createQuery("SELECT B FROM BlogComment B WHERE B.id=:id").setParameter("id", id).getResultList());
		em.close();
		if (results.size() == 0) {
			throw new IllegalStateException("Could not find this blog comment '" + id + "'");
		}
		return results.get(0);
	}

	public List<BlogComment> getBlogComments(BlogComment blogComment) {
		EntityManager em = Simple.getEm();
		List<BlogComment> results = ListUtils.unmodifiableList(em.createQuery("SELECT B FROM BlogComment B WHERE B.parent=:parent").setParameter("parent", blogComment).getResultList());
		em.close();
		return results;
	}

	public List<Category> getBlogCategories(Organization o) {

		EntityManager em = Simple.getEm();
		List<Category> list;
		list = ListUtils.unmodifiableList(em.createQuery("SELECT C FROM Category C WHERE  C.organization=:org AND C.type=:type ORDER BY C.id DESC")
				.setParameter("org", o)
				.setParameter("type", BlogPost.class.getSimpleName())
				.getResultList());
		em.close();
		return list;
	}

	public List<BlogPost> getBlogPosts(Organization organization) {
		EntityManager em = Simple.getEm();
		List<BlogPost> results = ListUtils.unmodifiableList(em.createQuery("SELECT B FROM BlogPost B WHERE B.organization=:id").setParameter("id", organization).getResultList());
		em.close();
		return results;
	}

	public void deleteComment(BlogComment comment) {
		try {
			EntityManager em = Simple.getEm();
			em.getTransaction().begin();
			BlogComment tracked = em.merge(comment);
			em.remove(tracked);
			em.flush();
			em.getTransaction().commit();
			em.close();
		} catch (Exception e) {
			comment.setContent(null);

			saveBlogComment(comment);

		}
	}

	public List<BlogPost> getBlogPosts(User u) {

		EntityManager em = Simple.getEm();
		List<BlogPost> results = ListUtils.unmodifiableList(em.createQuery("SELECT B FROM BlogPost B WHERE B.author=:id").setParameter("id", u).getResultList());
		em.close();
		return results;
	}
}
