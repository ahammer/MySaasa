package com.mysaasa.core.blog.model;

import com.google.gson.annotations.Expose;
import com.mysaasa.core.blog.services.BlogService;
import com.mysaasa.core.users.model.User;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * This is a comment on a blog post
 * Created by adam on 2014-09-16.
 */
@Entity
@Table(name = "BlogComment")
public class BlogComment {
	public BlogComment() {}

	@Expose
	public long id;
	@Expose
	public long parent_id = 0; //We provide this so we can link on the client without providing the hierarchy (waste of space, shared parents)
	@Expose
	public String content;
	@Expose
	public User author;
	@Expose
	public int score = 0;
	@Expose
	public Boolean visible = true;
	@Expose
	public Date dateCreated = new Date();

	public BlogPost post;
	public BlogComment parent;

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

	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "parentComment")
	public BlogComment getParent() {
		return parent;
	}

	public void setParent(BlogComment parent) {
		if (parent != null)
			parent_id = parent.id;
		this.parent = parent;
	}

	public BlogComment(String content, User author, BlogPost post) {
		this.content = content;
		this.author = author;
		this.post = post;
	}

	@Column(name = "dateCreated")
	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		if (dateCreated == null)
			dateCreated = new Date();
		this.dateCreated = dateCreated;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "content", columnDefinition = "text")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "author")
	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "BlogComment{" + "id=" + id + ", content='" + content + '\'' + ", author=" + author + ", post=" + post + '}';
	}

	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "post")
	public BlogPost getPost() {
		return post;
	}

	public void setPost(BlogPost post) {
		this.post = post;
	}

	public List<BlogComment> retrieveChildComments() {
		List<BlogComment> value = BlogService.get().getBlogComments(this);
		return value;
	}

}
