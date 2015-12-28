package com.mysassa.simple.core.users.data;

import java.io.Serializable;

import com.mysassa.simple.core.users.model.User;

import static com.google.common.base.Preconditions.checkNotNull;

//This is stuff the form needs over the user. We only store the user, but need a pojo for the rest.
public class UserFormData implements Serializable {

	private static final long serialVersionUID = 1L;
	private final User user;

	private String password; // Temporary storage

	public UserFormData(User modelObject) {
		checkNotNull(modelObject);
		user = modelObject;
	}

	public String getPassword() {
		return password;
	}

	public User getUser() {
		return user;
	}

}
