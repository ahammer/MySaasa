package com.mysassa.simple.interfaces.templating;

import com.google.common.collect.Lists;
import com.mysassa.simple.SimpleImpl;
import com.mysassa.simple.core.event_log.model.Event;
import com.mysassa.simple.core.event_log.service.EventQueueService;
import com.mysassa.simple.core.categories.model.Category;
import com.mysassa.simple.core.blog.model.BlogComment;
import com.mysassa.simple.core.blog.services.BlogService;
import com.mysassa.simple.core.security.services.SessionService;
import com.mysassa.simple.core.security.services.session.AdminSession;
import com.mysassa.simple.core.users.model.User;
import com.mysassa.simple.core.website.model.ContentBinding;
import com.mysassa.simple.core.website.model.Website;
import com.mysassa.simple.core.website.services.WebsiteService;
import com.mysassa.simple.interfaces.ITemplateService;
import com.mysassa.simple.core.blog.model.BlogPost;
import com.mysassa.simple.core.website.templating.TemplateHelperService;
import com.mysassa.simple.interfaces.annotations.SimpleService;
import org.apache.wicket.Session;
import org.eclipse.jetty.util.security.Credential;

import java.util.Collections;
import java.util.List;

/**
 *
 * Blog Related Functions for the Tmplating system
 *
 * Created by Adam on 3/15/14.
 */
@SimpleService
public class BlogTemplateService implements ITemplateService {
	public static final String TEMPLATE_SHORT_NAME = "Blog";

	public BlogTemplateService() {};

	/**
	 * This is javascript to make any content in the dom ContentEditable
	 * It's use by interactive portions of the website
	 *
	 * We inject this code so that it's not generally editable, but only on request.
	 *
	 * It is only used in the inline editor in the admin for blogs and website content
	 *
	 * It's executed in the client
	 */
	public final static String MAKE_WYSIWYG_CODE = " function makeWYSIWYG(editor) {\n" + "if (editor.attr('contenteditable')) return ;\n" + "       CKEDITOR.inline(document.getElementById(editor.attr('id')));\n" + "        editor.attr('contenteditable', true);\n" + "}\n" +

	"function EditContent(contentName) {\n" + "   makeWYSIWYG($('#'+contentName));\n" + " }" + "" + "function getValueOrNull(element) {" + "if (element == null) return null;\n" + "return element.html();\n" + "}\n";

	/**
	 * We inject some CSS as well, but perhaps this can be documented and moved to client css.
	 */
	public final static String MAKE_WYSIWYG_CSS = ".EditableContent {\n" +

	"		display:inline-block;\n" + "		cursor:pointer;\n" + "border:1px dotted grey;" + "}\n" + ".EditableContent:hover {\n" + "       border:1px dotted black;\n" +

	"}\n" + "" + ".simple_blog_entry_new {" + "   display:inline-block;" + "   border:1px dotted green;\n" + "   position:relative;" + "}\n" + "" + ".simple_blog_entry_new label {" + "background:green;\n" + "color:white;\n" + "margin:0px;\n" + "padding:0px;\n" + "text-size:0.4em;\n" + "padding-right:0.5em;\n" + "position:absolute;\n" + "display:inline-block;" + "left:0px;" + "top:0px;" + "" + "}";

	@Override
	public String getTemplateInterfaceName() {
		return TEMPLATE_SHORT_NAME;
	}

	/**
	 * We want to update these blog posts to have proper Edit references
	 * @param posts
	 * @param category
	 */
	protected void processBlogPostsForInlineEditing(List<BlogPost> posts, String category) {

		if (TemplateHelperService.get().getRequestProperties().debugMode) {
			for (BlogPost post : posts) {
				if (post.getId() == 0) {
					Category cat = Category.fromString(category, BlogPost.class, Website.getCurrent().getOrganization());
					post.getCategories().add(cat);
				}

				String unique = Credential.MD5.MD5.digest(System.currentTimeMillis() + " " + category).substring(5) + "_ID_" + post.getId();
				String categoryJson = post.CategoriesToJson();
				String editor = "$('#BlogPostBody_" + unique + "')";
				String title = "$('#BlogPostTitle_" + unique + "')";
				String summary = "$('#BlogPostSummary_" + unique + "')";
				String subtitle = "$('#BlogPostSubtitle_" + unique + "')";

				String rawSaveScript = "window.parent.postMessage(" + "{" + "   title:getValueOrNull(" + title + ")," + "   subtitle:getValueOrNull(" + subtitle + ")," + "   categories:" + categoryJson + "," + "   method:'save'," + "   content:getValueOrNull(" + editor + ")," + "   summary:getValueOrNull(" + summary + ")," + "   id:" + title + ".attr('id')" + "},'*');";
				String saveScript = "onBlur=\"" + rawSaveScript + "\"";
				if (post.id == 0) {
					saveScript = "";
				}

				post.setTitle("<div " + "class=\"EditableContent " + ((post.getPublished()) ? " Unpublished" : " Published") + ((post.getId() == 0) ? " NewContent" : "") + "\"" + "id=\"BlogPostTitle_" + unique + "\" " + "onClick=\"EditContent('BlogPostTitle_" + unique + "');\" " + saveScript + ">" + post.getTitle() + "</div>");

				post.setSubtitle("<div " + "class=\"EditableContent " + ((post.getPublished()) ? " Unpublished" : " Published") + //Tag the Class with "NewContent"
						((post.getId() == 0) ? " NewContent" : "") + "\"" + //Tag the Class with "NewContent"
						"id=\"BlogPostSubtitle_" + unique + "\" " + "onClick=\"EditContent('BlogPostSubtitle_" + unique + "');\" " + saveScript + ">" + post.getSubtitle() + "</div>");

				post.setSummary("<div " + "class=\"EditableContent " + ((post.getPublished()) ? " Unpublished" : " Published") + //Tag the Class with "NewContent"
						((post.getId() == 0) ? " NewContent" : "") + "\"" + //Tag the Class with "NewContent"
						"id=\"BlogPostSummary_" + unique + "\" " + "onClick=\"EditContent('BlogPostSummary_" + unique + "');\" " + saveScript + ">" + post.getSummary() + "</div>");

				post.setBody("<div " + "class=\"EditableContent " + ((post.getPublished()) ? " Unpublished" : " Published") + //Tag the Class with "NewContent"
						((post.getId() == 0) ? " NewContent" : "") + "\"" + //Tag the Class with "NewContent"
						"id=\"BlogPostBody_" + unique + "\" " + "onClick=\"EditContent('BlogPostBody_" + unique + "');\" " + saveScript + ">" + post.getBody() + "</div>");

				if (post.getId() == 0) {
					post.getCategories().add(Category.fromString(category, BlogPost.class, Website.getCurrent().getOrganization()));
					post.setHeader("<div class='simple_blog_entry simple_blog_entry_new'><label>New</label>");
				} else {
					post.setHeader("<div class='simple_blog_entry'>");
				}

				post.setFooter(post.getFooter() + ((post.getId() == 0) ? "<input type='button' onClick=\"" + rawSaveScript + "\" value='save'/>" : "<input type='button' onClick=\"window.parent.postMessage(" + "{" + "   categories:" + categoryJson + "," + "   method:'edit'," + "   id:" + title + ".attr('id')" + "},'*');\n" + "\" value='edit'/>") +

				((post.getId() == 0) ? ""
						: //If the id = 0 it's new and don't show delete
						"<input type='button' onClick=\"window.parent.postMessage({method:'delete',id:" + title + ".attr('id')},'*');\n" + "\" value='delete'/>") + "</div>");

			}
		}
	}

	public static final String callbackFunction = "" +

	//This listener is listening for window.parent.postMessage call
	//It allows the template to communicate with the admin
	//This function translates that message to a function call
	//that sends to the admin
	"function listener(event) {\n" + "   SendMessageToAdmin(" + "       event.data.title," + "       event.data.subtitle," + "       event.data.summary," + "       event.data.content," + "       event.data.id," + "       event.origin," + "       event.data.method,JSON.stringify(event.data.categories)" + "   );\n" + "}" +

	//This attaches the above code, so that it is listening for
	//between browser window (iframe/parent) messages.
	"if (window.addEventListener) {\n" + "       addEventListener(\"message\", listener, false);\n" + "} else {\n" + "       attachEvent(\"onmessage\", listener);\n" + "}\n" +

	//Sends the actual message to the admin.
	//ep is Extra parameters, it's how you send custom
	//data with wicket
	//Wicket will provide the callbackUrl from the behaviour and
	//it should populate %callbackUrl
	"function SendMessageToAdmin(title,subtitle,summary, msg,id,origin,method,json) {\n" + "Wicket.Ajax.post({u:'%callbackUrl%'," + "ep:[" + "{name:'msg',value:msg}," + "{name:'origin',value:origin}," + "{name:'id',value:id}," + "{name:'method',value:method}," + "{name:'json',value:json}," + "{name:'title',value:title}," + "{name:'subtitle',value:subtitle}," + "{name:'summary',value:summary}" + "]   " + "});\n" + "}\n" + "";

	//  Looks up the text bound to this content tag. If DebugMode is enabled
	//  The content will be wrapped in a Span and made editable.
	//  @Params The name of this ContentBinding
	//  @Returns the String Content RunContext, hiding behind the Content Binding.
	public String bind(String name) {
		TemplateHelperService templateHelperService = SimpleImpl.get().getInjector().getProvider(TemplateHelperService.class).get();
		TemplateHelperService.RequestProperties rp = templateHelperService.getRequestProperties();
		Website website = rp.website;
		boolean debugMode = rp.debugMode;
		WebsiteService service = SimpleImpl.get().getInjector().getProvider(WebsiteService.class).get();
		ContentBinding b = service.findBinding(name, website, name);
		if (debugMode) {
			return "<div " + "class=\"EditableContent\" " + "id=\"" + name + "\" " + "onClick=\"EditContent('" + name + "');\"\"" + ">" + b.getContent().getBody() + "</div>";
		} else {
			return b.getContent().getBody();
		}
	}

	public List<BlogPost> getBlogPostsByCategory(String category) {
		return getBlogPostsByCategory(category, "dateCreated", "DESC", 0, 10);
	}

	public List<BlogPost> getBlogPostsByCategory(String category, int page, int take) {
		return getBlogPostsByCategory(category, "dateCreated", "DESC", page, take);
	}

	public List<BlogPost> getBlogPostsByCategory(String category, String order, String direction, int page, int take) {
		try {
			TemplateHelperService templateHelperService = SimpleImpl.get().getInjector().getProvider(TemplateHelperService.class).get();
			TemplateHelperService.RequestProperties rp = templateHelperService.getRequestProperties();
			Website website = rp.website;

			BlogService service = BlogService.get();
			Category c = Category.fromString(category, BlogPost.class, Website.getCurrent().getOrganization());
			List<BlogPost> posts = service.getBlogPostsByCategory(rp.website.getOrganization(), c, order, direction, page, take);
			if (TemplateHelperService.get().getRequestProperties().debugMode
					&& AdminSession.get().isNewPostAllowed())
				posts.add(0, BlogPost.CreateEditPlaceholder(rp.website));
			processBlogPostsForInlineEditing(posts, category);
			return posts;
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.EMPTY_LIST;
		}
	}

	public BlogPost getBlogPostById(long id) {
		BlogService service = BlogService.get();
		BlogPost post = service.getBlogPostById(id);
		List<BlogPost> posts = Lists.newArrayList(post);
		processBlogPostsForInlineEditing(posts, post.getCategories().get(0).getName());
		return posts.get(0);
	}

	public BlogPost getBlogPostById(String id) {
		if (id == null)
			return new BlogPost(null, null);

		//TemplateHelperService templateHelperService = SimpleImpl.get().getInjector().getProvider(TemplateHelperService.class).get();
		//TemplateHelperService.RequestProperties rp = templateHelperService.getRequestProperties();
		//Website website = rp.website;
		//("Getting posts for category: "+category);
		BlogService service = BlogService.get();
		BlogPost post = service.getBlogPostById(Long.parseLong(id));
		List<BlogPost> posts = Lists.newArrayList(post);
		processBlogPostsForInlineEditing(posts, post.getCategories().get(0).getName());
		return posts.get(0);
	}

	/**
	 * Deletes a blog comment, uses a string because the templates are usually working with that
	 * @param comment_id
	 * @return
	 */
	public boolean deleteBlogComment(String comment_id) {
		try {
			long id = Long.valueOf(comment_id);
			return deleteBlogComment(id);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteBlogComment(long id) {
		try {
			BlogService service = BlogService.get();

			BlogComment bc = service.getBlogCommentById(id);
			service.deleteComment(bc);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			;
			return false;
		}
	}

	public boolean postToBlog(String category, String title, String body) {
		User u = SessionService.get().getSecurityContext(Session.get()).getUser();

		TemplateHelperService templateHelperService = SimpleImpl.get().getInjector().getProvider(TemplateHelperService.class).get();
		TemplateHelperService.RequestProperties rp = templateHelperService.getRequestProperties();
		Website website = rp.website;
		boolean debugMode = rp.debugMode;
		BlogPost blogPost = new BlogPost(u, rp.website.getOrganization());
		List<Category> l = Lists.newArrayList(Category.fromString(category, BlogPost.class, Website.getCurrent().getOrganization()));
		blogPost.setCategories(l);
		blogPost.setTitle(title);
		blogPost.setBody(body);
		BlogService.get().saveBlogPost(blogPost);
		return true;
	}

	public List<BlogComment> getBlogComments(long id, int count) {
		return BlogService.get().getTopLevelBlogComments(id, count);
	}

	public boolean postComment(long blogpost_id, String comment) {
		try {
			long l_id = blogpost_id;
			BlogPost bp = BlogService.get().getBlogPostById(l_id);
			User u = SessionService.get().getSecurityContext(Session.get()).getUser();
			if (u == null)
				return false; //Can't post if not signed in
			BlogComment blogComment = new BlogComment(comment, u, bp);
			BlogService.get().saveBlogComment(blogComment);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Post a Comment
	 * @param id
	 * @param parentCommentId
	 * @param comment
	 * @return
	 */
	public boolean postComment(String id, String parentCommentId, String comment) {
		try {
			long l_id = Long.valueOf(id);
			long parentComment_id = Long.valueOf(parentCommentId);
			BlogPost bp = BlogService.get().getBlogPostById(l_id);
			BlogComment parentBlogComment = BlogService.get().getBlogCommentById(parentComment_id);

			User u = SessionService.get().getSecurityContext(Session.get()).getUser();
			if (u == null)
				return false; //Can't post if not signed in
			BlogComment blogComment = new BlogComment(comment, u, bp);
			blogComment.setParent(parentBlogComment);
			BlogService.get().saveBlogComment(blogComment);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean likeBlogPost(String id) {
		long l_id = Long.valueOf(id);
		return likeBlogPost(l_id);
	}

	public boolean likeBlogPost(long l_id) {
		try {
			BlogPost bp = BlogService.get().getBlogPostById(l_id);
			if (Session.get() == null)
				return false;
			if (SessionService.get().getSecurityContext(Session.get()) == null)
				return false;
			User u = SessionService.get().getSecurityContext(Session.get()).getUser();
			if (u == null)
				return false; //Can't post if not signed in

			Event command = new Event(u, String.valueOf(bp.getId()), Event.Method.BlogPostVote);
			EventQueueService.get().submitCommand(command);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public boolean likeComment(String id) {
		long l_id = Long.valueOf(id);
		return likeComment(l_id);
	}

	public boolean likeComment(long l_id) {
		try {
			BlogComment bp = BlogService.get().getBlogCommentById(l_id);
			if (Session.get() == null)
				return false;
			if (SessionService.get().getSecurityContext(Session.get()) == null)
				return false;
			User u = SessionService.get().getSecurityContext(Session.get()).getUser();
			if (u == null)
				return false; //Can't post if not signed in

			Event command = new Event(u, String.valueOf(bp.getId()), Event.Method.BlogCommentVote);
			EventQueueService.get().submitCommand(command);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
