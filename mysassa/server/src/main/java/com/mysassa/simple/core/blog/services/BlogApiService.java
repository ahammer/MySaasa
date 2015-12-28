package com.mysassa.simple.core.blog.services;

import com.mysassa.simple.api.ApiError;
import com.mysassa.simple.api.ApiNotAuthorized;
import com.mysassa.simple.api.ApiResult;
import com.mysassa.simple.api.ApiSuccess;
import com.mysassa.simple.core.blog.model.BlogComment;
import com.mysassa.simple.core.blog.model.BlogPost;
import com.mysassa.simple.core.categories.model.Category;
import com.mysassa.simple.core.hosting.service.HostingService;
import com.mysassa.simple.core.security.services.SessionService;
import com.mysassa.simple.core.security.services.session.SecurityContext;
import com.mysassa.simple.core.users.model.User;
import com.mysassa.simple.core.website.model.Website;
import com.mysassa.simple.interfaces.IApiService;
import com.mysassa.simple.interfaces.annotations.ApiCall;
import com.mysassa.simple.interfaces.annotations.SimpleService;
import org.apache.wicket.Session;
import org.apache.wicket.request.cycle.RequestCycle;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by administrator on 2014-06-30.
 */
@SimpleService
public class BlogApiService implements IApiService {

	@ApiCall
	public ApiResult postToBlog(String title, String subtitle, String summary, String body, String category) {
		try {

			Website w = HostingService.get().findWebsite(RequestCycle.get().getRequest().getClientUrl());
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
			return new ApiSuccess(BlogService.get().getBlogPostsByCategory(Website.getCurrent().getOrganization(), l, page, take, order, direction));
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
	public ApiResult getBlogComments(long id, int count) {
		try {
			return new ApiSuccess(BlogService.get().getBlogComments(id, count));
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
			bc.setAuthor(SecurityContext.get().getUser());
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
				return new ApiError("Not found/Deleted");
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
	 * @return
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
