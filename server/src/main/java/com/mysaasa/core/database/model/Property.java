package com.mysaasa.core.database.model;

/**
 * Created by adam on 14-12-12.
 */

import com.google.gson.annotations.Expose;

import javax.persistence.*;

/**
 *
 */
@Entity(name = "properties")
public class Property {
	@Expose
	public long id;
	@Expose
	public String key;
	@Expose
	public String value;

	public Property(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "key")
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Column(name = "value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
