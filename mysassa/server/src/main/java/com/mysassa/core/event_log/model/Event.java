package com.mysassa.core.event_log.model;

import com.mysassa.core.users.model.User;

import javax.persistence.*;

/**
 * Created by adam on 2014-10-07.
 */
@Entity
@Table(name = "Events")
public class Event {
	public long id;

	@Override
	public String toString() {
		return "Event{" + "user=" + user + ", payload='" + payload + '\'' + ", consumed=" + consumed + ", method=" + method + '}';
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public static enum Method {
		BlogCommentVote, BlogPostVote
	}

	public User user;
	public String payload;
	public String result;

	@Column(name = "result")
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Boolean consumed = false;
	public Method method;

	public Event() {}

	public Event(User user, String payload, Method method) {
		this.user = user;
		this.payload = payload;
		this.method = method;
	}

	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "author")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "payload")
	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	@Column(name = "consumed")
	public Boolean getConsumed() {
		return consumed;
	}

	@Column(name = "method")
	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public void setConsumed(Boolean consumed) {
		this.consumed = consumed;
	}
}
