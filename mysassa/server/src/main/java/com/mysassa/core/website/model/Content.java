package com.mysassa.core.website.model;

import java.io.Serializable;

import javax.persistence.*;

import com.google.gson.annotations.Expose;

@Entity
@Table(name = "Content")
public class Content implements Serializable {
	public static final long serialVersionUID = 1L;

	@Expose
	public long id;
	@Expose
	public String body;
	@Expose
	public String name;

	public Content() {}

	public Content(String name, String content) {
		setName(name);
		setBody(content);
	}

	@Lob
	@Column(name = "body")
	public String getBody() {
		return body;
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

	public void setBody(String content) {
		this.body = content;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

}
