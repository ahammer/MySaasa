package com.mysaasa.core.blog.services;

import com.mysaasa.api.model.ApiSuccess;
import com.mysaasa.core.blog.model.BlogPost;
import com.mysaasa.core.categories.model.Category;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.interfaces.annotations.ApiCall;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.api.model.ApiError;
import com.mysaasa.api.ApiNotAuthorized;
import com.mysaasa.api.model.ApiResult;
import com.mysaasa.core.blog.model.BlogComment;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.core.security.services.SessionService;
import com.mysaasa.core.security.services.session.SecurityContext;
import com.mysaasa.interfaces.IApiService;
import org.apache.wicket.Session;
import org.apache.wicket.request.cycle.RequestCycle;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mysaasa.MySaasa.getService;

/**
 * Created by administrator on 2014-06-30.
 */
@SimpleService
public class BlogApiService implements IApiService {

	@ApiCall
	public ApiResult postToBlog(String title, String subtitle, String summary, String body, String category) {
		try {

			Website w = getService(HostingService.class).findWebsite(RequestCycle.get().getRequest().getClientUrl());
			SessionService service = SessionService.get();
			Session session = Session.get();
			checkNotNull(service);
			checkNotNull(session);
			SecurityContext context = service.getSecurityContext(session);
			if (context == null) {
				return new ApiNotAuthorized();
			}

			checkNotNull(context);
			User u = context.getUser();
			if (u == null) {
				return new ApiNotAuthorized();
			}
			BlogPost blogPost = new BlogPost(u, w.getOrganization());
			blogPost.setTitle(title);
			blogPost.setBody(body);
			blogPost.setSummary(summary);
			blogPost.setSubtitle(subtitle);
			blogPost.addCategory(category);
			BlogPost post = BlogService.get().saveBlogPost(blogPost);

			return new ApiSuccess(post);
		} catch (Exception e) {
			return new ApiError(e);
		}
	}

	@ApiCall
	public ApiResult updateBlogPost(long id, String title, String subtitle, String summary, String body) {
		try {
			User u = SecurityContext.get().getUser();
			BlogPost blogPost = BlogService.get().getBlogPostById(id);
			if (u == null || blogPost == null || blogPost.author.id != u.id)
				return new ApiNotAuthorized();
			if (blogPost.author.getId() != u.getId())
				return new ApiNotAuthorized();
			blogPost.setTitle(title);
			blogPost.setBody(body);
			blogPost.setSummary(summary);
			blogPost.setSubtitle(subtitle);
			BlogPost post = BlogService.get().saveBlogPost(blogPost);
			return new ApiSuccess(post);
		} catch (Exception e) {
			return new ApiError(e);
		}
	}

	@ApiCall
	public ApiResult getBlogPosts(String category, int page, int take, String order, String direction) {
		try {
			Category c = Category.fromString(category, BlogPost.class, Website.getCurrent().getOrganization());
			List<Category> l = new ArrayList<>();
			l.add(c);
			List<BlogPost> blogPosts;
			ApiSuccess result = new ApiSuccess(blogPosts = BlogService.get().getBlogPostsByCategory(Website.getCurrent().getOrganization(), l, page, take, order, direction));
			return result;
		} catch (Exception e) {
			return new ApiError(e);
		}
	}

	@ApiCall
	public ApiResult getBlogPostById(long id) {
		try {
			return new ApiSuccess(BlogService.get().getBlogPostById(id));
		} catch (Exception e) {
			return new ApiError(e);
		}
	}

	@ApiCall
	public ApiResult getBlogComments(long post_id, int count) {
		try {
			return new ApiSuccess(BlogService.get().getBlogComments(post_id, count));
		} catch (Exception e) {
			e.printStackTrace();
			return new ApiError(e);
		}
	}

	@ApiCall
	public ApiResult getTopLevelBlogComments(long id, int count) {
		try {
			return new ApiSuccess(BlogService.get().getTopLevelBlogComments(id, count));
		} catch (Exception e) {
			return new ApiError(e);
		}
	}

	@ApiCall
	public ApiResult postComment(long post_id, String comment) {
		try {
			BlogComment bc = new BlogComment();
			SecurityContext sc = SecurityContext.get();

			bc.setAuthor(sc.getUser());
			bc.setPost(BlogService.get().getBlogPostById(post_id));
			bc.setContent(comment);
			bc = BlogService.get().saveBlogComment(bc);
			return new ApiSuccess(bc);
		} catch (Exception e) {
			e.printStackTrace();
			return new ApiError(e);
		}

	}

	@ApiCall
	public ApiResult updateComment(long comment_id, String comment) {
		try {
			BlogComment bc = BlogService.get().getBlogCommentById(comment_id);
			if (bc != null && bc.author != null) {
				if (bc.getAuthor().getId() != SecurityContext.get().getUser().id)
					return new ApiNotAuthorized();
				bc.setContent(comment);
				bc = BlogService.get().saveBlogComment(bc);
				return new ApiSuccess(bc);
			} else {
				return new ApiError(new RuntimeException("This comment can not be found"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ApiError(e);
		}
	}

	@ApiCall
	public ApiResult postReply(long parent_comment_id, String comment) {
		try {
			BlogComment bc = new BlogComment();
			if (SecurityContext.get() == null || SecurityContext.get().getUser() == null)
				return new ApiNotAuthorized();
			bc.setAuthor(SecurityContext.get().getUser());
			BlogComment parent = BlogService.get().getBlogCommentById(parent_comment_id);
			bc.setPost(parent.getPost());
			bc.setContent(comment);
			bc.setParent(BlogService.get().getBlogCommentById(parent_comment_id));
			bc = BlogService.get().saveBlogComment(bc);
			return new ApiSuccess(bc);
		} catch (Exception e) {
			e.printStackTrace();
			return new ApiError(e);
		}
	}

	/**
	 * Comments without children will be deleted
	 *
	 * Comment with children will be marked [DELETED]
	 * 
	 * @param comment_id
	 *            the id of the comment we want to delete
	 * @return the result of the delete
	 */
	@ApiCall
	public ApiResult deleteComment(long comment_id) {
		try {
			if (SecurityContext.get() == null)
				return new ApiNotAuthorized();
			BlogComment comment = BlogService.get().getBlogCommentById(comment_id);
			if (comment.retrieveChildComments().size() > 0) {
				comment.setContent("[DELETED]");
				comment.setAuthor(null);
				BlogService.get().saveBlogComment(comment);
			} else {
				BlogService.get().deleteComment(comment);
			}

			return new ApiSuccess(true);
		} catch (Exception e) {
			return new ApiError(e);
		}
	}

	@ApiCall
	public ApiResult deleteBlogPost(long post_id) {
		try {
			if (SecurityContext.get() == null)
				return new ApiNotAuthorized();
			User u = SecurityContext.get().getUser();
			BlogPost blogPost = BlogService.get().getBlogPostById(post_id);
			BlogService.get().deleteBlogPost(blogPost);
			return new ApiSuccess(true);
		} catch (Exception e) {
			return new ApiError(e);
		}
	}
}
