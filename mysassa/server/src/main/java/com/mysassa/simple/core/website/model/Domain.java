package com.mysassa.simple.core.website.model;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.io.Serializable;

/**
 * This is a Domain entry, a website might respond to multiple.
 * Created by Adam on 10/11/2014.
 */
@Entity
@Table(name = "Domain")
public class Domain implements Serializable {
	@Expose
	public long id;
	@Expose
	public String domain;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	public Domain() {}

	public Domain(String domain) {
		this.domain = domain;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "domain")
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
