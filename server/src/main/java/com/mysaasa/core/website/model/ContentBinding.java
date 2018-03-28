package com.mysaasa.core.website.model;

import java.io.Serializable;

import javax.persistence.*;

import com.google.gson.annotations.Expose;
import com.mysaasa.SimpleImpl;
import com.mysaasa.core.website.services.WebsiteService;

@Entity
@Table(name = "ContentBinding")
public class ContentBinding implements Serializable {
	@Expose
	public static final long serialVersionUID = 1L;
	@Expose
	public Website website;
	@Expose
	public Content content;
	@Expose
	public String name;

	@Expose
	public long id;

	public ContentBinding() {

	}

	public ContentBinding(String name, Content content, Website website) {
		if (website == null)
			throw new NullPointerException("Unnacceptable!! Null website in BlogAdmin Binding");
		setName(name);
		setContent(content);
		this.website = website;
	}

	@OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
	@JoinColumn(name = "content")
	public Content getContent() {
		return content;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	@OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH})
	@JoinColumn(name = "website")
	public Website getWebsite() {
		return website;
	}

	public void save() {
		SimpleImpl.getInstance().getInjector().getProvider(WebsiteService.class).get().saveContentBinding(this);
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWebsite(Website w) {
		website = w;
	}

	@Override
	public String toString() {
		if (getContent() != null)
			return getContent().getBody();
		return "unbound";
	}

}
