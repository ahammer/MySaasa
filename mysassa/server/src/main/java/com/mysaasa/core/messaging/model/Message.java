package com.mysaasa.core.messaging.model;

import com.google.gson.annotations.Expose;
import com.mysaasa.core.messaging.services.MessagingService;
import com.mysaasa.core.security.services.session.SecurityContext;
import com.mysaasa.core.users.model.ContactInfo;
import com.mysaasa.core.users.model.User;
import org.apache.wicket.request.cycle.RequestCycle;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by adam on 14-12-08.
 */
@Entity
@Table(name = "Message")
public class Message implements Serializable {
	@Expose
	public long id;
	@Expose
	public User recipient;
	@Expose
	public User sender;
	@Expose
	public ContactInfo senderContactInfo = new ContactInfo();
	@Expose
	public String title = "";
	@Expose
	public String body = "";
	@Expose
	public String data = "";
	@Expose
	public Date timeSent = new Date();
	@Expose
	public Message messageThreadRoot = null;

	public Message() {
		try {
			if (RequestCycle.get() != null)
				sender = SecurityContext.get().getUser();
		} catch (Exception e) {
			//No Request Cycle
		}
	}

	//Reply from message
	public Message(Message message) {
		if (RequestCycle.get() != null && SecurityContext.get() != null && SecurityContext.get().getUser() != null) {
			setSender(SecurityContext.get().getUser());
		}

		this.messageThreadRoot = message;
		this.title = message.getTitle();

	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "senderContactInfo")
	public ContactInfo getSenderContactInfo() {
		return senderContactInfo;
	}

	public void setSenderContactInfo(ContactInfo senderContactInfo) {
		this.senderContactInfo = senderContactInfo;
	}

	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "threadRoot")
	public Message getMessageThreadRoot() {
		return messageThreadRoot;
	}

	public void setMessageThreadRoot(Message message) {
		this.messageThreadRoot = message;
	}

	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "recipientUser")
	public User getRecipient() {
		return recipient;
	}

	public void setRecipient(User recipient) {
		this.recipient = recipient;
	}

	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "sendingUser")
	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {

		this.sender = sender;
	}

	@Column(name = "title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Lob
	@Column(name = "body", columnDefinition = "text")
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Lob
	@Column(name = "data", columnDefinition = "text")
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Column(name = "timeSent")
	public Date getTimeSent() {
		return timeSent;
	}

	public void setTimeSent(Date timeSent) {
		this.timeSent = timeSent;
	}

	@Override
	public String toString() {
		return "Message{" + "senderContactInfo=" + senderContactInfo + ", recipient=" + recipient + ", title='" + title + '\'' + ", timeSent=" + timeSent + '}';
	}

	public boolean hasBeenRead(User user) {
		return MessagingService.get().hasBeenRead(this, user);
	}

}
