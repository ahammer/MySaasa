package com.mysassa.core.messaging.model;

import com.google.gson.annotations.Expose;
import com.mysassa.core.users.model.User;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Adam on 4/11/2015.
 */
@Entity
@Table(name = "MessageReadReceipt")
public class MessageReadReceipt implements Serializable {
	@Expose
	boolean read = false;

	@Expose
	User user;

	@Expose
	Message message;

	@Expose
	public long id;

	public MessageReadReceipt() {}

	public MessageReadReceipt(Message message, User user) {
		this.user = user;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "read")
	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	@OneToOne(cascade = CascadeType.DETACH)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@OneToOne(cascade = CascadeType.DETACH)
	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
}
