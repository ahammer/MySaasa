package com.mysassa.simple.core.users.model;

import com.google.gson.annotations.Expose;
import com.mysassa.simple.core.organization.model.Organization;
import com.mysassa.simple.core.organization.services.OrganizationService;
import com.mysassa.simple.core.security.PasswordHash;
import com.mysassa.simple.core.users.service.UserService;
import com.mysassa.simple.core.website.model.Website;

import javax.persistence.*;
import java.io.Serializable;

/*
 * A user, identified by a email and a password, with some Contact Info for good measure 
 */
@Entity
@Table(name = "User")
public class User implements Serializable {
	public static final long serialVersionUID = 1L;
	public AccessLevel accessLevel = AccessLevel.ORG;

	public String password_md5;
	@Expose
	public long id;
	@Expose
	public String identifier;
	@Expose
	public Organization organization;
	@Expose
	public Website website;
	public ContactInfo contactInfo = new ContactInfo();

	@Column(name = "accessLevel")
	public AccessLevel getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(AccessLevel level) {
		this.accessLevel = level;
	};

	public Organization findOrganization() {
		return OrganizationService.get().findUsersOrganization(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		User user = (User) o;

		if (id != user.id)
			return false;
		if (accessLevel != user.accessLevel)
			return false;
		if (identifier != null ? !identifier.equals(user.identifier) : user.identifier != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + identifier.hashCode();
		return result;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "organization")
	public Organization getOrganization() {
		return organization;
	}

	public static enum AccessLevel {
		ROOT(100), //All Access (hosting+all)
		ORG(70), //All Organization Access (blog+website+users)

		WWW(30), //All Website access (blog+website)
		GUEST(0); //Visitors, Members, whatever you want to call them, but not Admin users
		public final int priority;

		AccessLevel(int i) {
			this.priority = i;
		}

	}

	public User() {}

	public User(String identifier, String password, AccessLevel access_level) {
		contactInfo = new ContactInfo();
		this.accessLevel = access_level;
		this.identifier = identifier;
		this.setPassword_md5(PasswordHash.createHash(password));
	}

	public User(String identifier, String password, Organization org) {
		contactInfo = new ContactInfo();
		this.accessLevel = AccessLevel.ORG;
		this.identifier = identifier;
		this.setPassword_md5(PasswordHash.createHash(password));
		this.organization = org;
	}

	@Override
	public String toString() {
		return "User{" + "id=" + getId() + ", identifier='" + getIdentifier() + '\'' + ", password_md5='" + getPassword_md5() + '\'' + ", contactInfo=" + getContactInfo() + '}';
	}

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "contact")
	public ContactInfo getContactInfo() {
		return contactInfo;
	}

	@Column(name = "identifier")
	public String getIdentifier() {
		return identifier;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	@Column(name = "password_md5")
	public String getPassword_md5() {
		return password_md5;
	}

	public void setContactInfo(ContactInfo contactInfo) {
		if (contactInfo == null)
			contactInfo = new ContactInfo(); //Fix for old accounts without default Contact Info
		this.contactInfo = contactInfo;

	}

	/**
	 * @Throws illegalArgumentException if validate fails
	 * @param identifier
	 */
	public void setIdentifier(String identifier) {
		//validate(identifier);
		this.identifier = identifier;

	}

	public void setId(long id) {
		this.id = id;
	}

	public void setPassword_md5(String password_md5) {
		this.password_md5 = password_md5;
	}

	/*
	 * Converts a PlainText password into a hashed password.
	 *
	 * @Param password
	 * @Throws NullPointerException if password = null
	 * @Throws IllegalStateException if Password can't be hashed because of Exception.
	 */
	public static String calculatePasswordHash(String password) {
		if (password == null)
			throw new NullPointerException("password can not be null");
		return (PasswordHash.createHash(password));
	}

	//Hashes and set's the password
	public void setPassword(String password) {
		setPassword_md5(PasswordHash.createHash(password));
	}

	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "website")
	public Website getWebsite() {
		return website;
	}

	public void setWebsite(Website website) {
		this.website = website;
	}

	public static User fromContactInfo(Organization o, ContactInfo ci) {
		User u = UserService.get().findUserByEmail(ci.getEmail());
		if (u == null) {
			u = new User();
			u.setAccessLevel(AccessLevel.GUEST);
			String identifier = ci.getEmail().split("@")[0];
			User taken = null;
			int pos = 1;
			do {
				if (pos == 1) {
					taken = UserService.get().getUser(identifier);
				} else {
					taken = UserService.get().getUser(identifier + pos);
				}
				pos++;
			} while (taken != null);
			String finalIdentifier = (pos == 1) ? identifier : identifier + pos;
			u.setIdentifier(finalIdentifier);
			u.setContactInfo(new ContactInfo(ci));
			u.setOrganization(o);
			u = UserService.get().saveUser(u);
		}
		return u;
	}
}
