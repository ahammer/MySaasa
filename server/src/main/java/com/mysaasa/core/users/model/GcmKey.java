package com.mysaasa.core.users.model;

import javax.persistence.*;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Adam on 5/16/2016.
 */
@Entity
@Table(name = "GcmKey")
public class GcmKey {
	protected String key;
	protected Date dateCreated;
	protected long id;

	public GcmKey() {}

	public GcmKey(String key) {
		checkNotNull(key);
		this.key = key;
		this.dateCreated = new Date();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "gcmkey")
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Column(name = "dateCreated")
	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		GcmKey gcmKey = (GcmKey) o;
		return key != null ? key.equals(gcmKey.key) : gcmKey.key == null;
	}

	@Override
	public int hashCode() {
		return key != null ? key.hashCode() : 0;
	}
}
