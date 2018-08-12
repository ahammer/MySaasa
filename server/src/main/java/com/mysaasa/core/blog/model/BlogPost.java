package com.mysaasa.core.blog.model;

import com.google.gson.annotations.Expose;
import com.mysaasa.core.blog.services.BlogService;
import com.mysaasa.core.media.model.Media;
import com.mysaasa.core.organization.model.Organization;
import com.mysaasa.core.categories.model.Category;
import com.mysaasa.core.security.services.SessionService;
import com.mysaasa.core.security.services.session.AdminSession;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.website.model.Website;
import org.apache.wicket.Session;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * A postToBlog is just a Article or stream of "Text articles" with a series of linked images. They can be used to represent articles or news, depending on how you choose to categorize them. They will be associated with a organization and visible to websites and apps in that organization.
 */
@Entity
@Table(name = "BlogPost")
public class BlogPost implements Serializable {
	private static final long serialVersionUID = 1L;
	public String header = "";
	public String footer = "";

	public BlogPost() {}

	@Expose
	public long id;

	@Expose
	public User author;
	@Expose
	public Date dateCreated = new Date();
	@Expose
	public Date datePublished;
	@Expose
	public String title;
	@Expose
	public String subtitle;

	@Expose
	public String summary;

	@Expose
	public String body;
	@Expose
	public Boolean published;
	@Expose
	public int score;
	@Expose
	public Boolean visible = true;
	@Expose
	public Integer priority = 1;

	@Column(name = "priority")
	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@Expose
	public Organization organization;

	@Expose
	public List<Category> categories = new ArrayList<Category>();
	@Expose
	public List<Media> media = new ArrayList<Media>();

	@Column(name = "visible")
	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		if (visible == null)
			visible = true;
		this.visible = visible;
	}

	@Column(name = "score")
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getFooter() {
		return footer == null ? "" : footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public BlogPost(Organization organization) {
		this.organization = organization;
		dateCreated = new Date();
		dateCreated.setTime(System.currentTimeMillis());
		id = 0;
		author = null;
		title = "";
		body = "";
		subtitle = "";
		summary = "";
	}

	@Override
	public String toString() {
		return "postToBlog{" + "id=" + getId() + ", author=" + getAuthor() + ", dateCreated=" + getDateCreated() + ", datePublished=" + getDatePublished() + ", title='" + getTitle() + '\'' + ", body='" + getBody() + '\'' + ", categoryLinks=" + getCategories() + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		BlogPost blogPost = (BlogPost) o;

		if (id != blogPost.id)
			return false;
		if (!dateCreated.equals(blogPost.dateCreated))
			return false;
		if (!title.equals(blogPost.title))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + dateCreated.hashCode();
		result = 31 * result + title.hashCode();
		return result;
	}

	public BlogPost(User author, Organization o) {
		this.author = author;
		this.organization = o;
		dateCreated = new Date();
		dateCreated.setTime(System.currentTimeMillis());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@OneToOne(cascade = {CascadeType.DETACH, CascadeType.PERSIST})
	@JoinColumn(name = "organization")
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "author")
	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	@Column(name = "title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title.toLowerCase().endsWith("<br/>"))
			title = title.substring(0, title.length() - 5);
		this.title = title;
	}

	@Column(name = "subtitle")
	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	@Lob
	@Column(name = "summary", columnDefinition = "text")
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Lob
	@Column(name = "body", columnDefinition = "text")
	public String getBody() {
		return body;
	}

	/**
	 * Is the body a URL? this is useful for some layout logic on frontend
	 * 
	 * @return this isn't used anymore
	 */
	public boolean checkBodyUrl() {
		if (body == null)
			return false;

		if (body.startsWith("http://")) {
			return true;
		}
		return false;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Column(name = "dateCreated")
	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Column(name = "datePublished")
	public Date getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;
	}

	public BlogPost save() {
		BlogService service = BlogService.get();
		return service.saveBlogPost(this);
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = {CascadeType.DETACH})
	@JoinTable
	public List<Category> getCategories() {
		if (categories == null) {
			categories = new ArrayList();
		}
		return categories;
	}

	public void addCategory(String name) {
		Category category = Category.fromString(name, BlogPost.class, organization);
		if (!getCategories().contains(category))
			getCategories().add(category);
	}

	public void removeCategory(Category bc) {
		if (getCategories().contains(bc))
			getCategories().remove(bc);
	}

	public void setCategories(List<Category> list) {
		categories = list;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = CascadeType.DETACH)
	@JoinTable
	public List<Media> getMedia() {
		if (media == null) {
			media = new ArrayList<>();
		}
		return media;
	}

	public void setMedia(List<Media> media) {
		this.media = media;
	}

	public static BlogPost CreateEditPlaceholder(Website w) {
		BlogPost p = new BlogPost(w.getOrganization());
		p.setId(0);
		p.setTitle("Title");
		p.setBody("Body");
		p.setSummary("Summary");
		p.setSubtitle("Subtitle");
		Session session = Session.get();
		SessionService sessionService = SessionService.get();
		AdminSession as = sessionService.getAdminSession(session);
		p.setAuthor(as.getContext().getUser());
		return p;
	}

	@Column(name = "published")
	public Boolean getPublished() {
		return (published == null) ? false : published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public String CategoriesToJson() {
		String json = "[";
		for (Category bc : getCategories()) {
			json += "'" + bc.getName() + "',";
		}
		json += "]";
		return json;
	}
}
