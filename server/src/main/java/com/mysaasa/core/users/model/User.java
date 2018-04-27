package com.mysaasa.core.users.model;

import com.google.gson.annotations.Expose;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.core.organization.model.Organization;
import com.mysaasa.core.organization.services.OrganizationService;
import com.mysaasa.core.security.PasswordHash;
import com.mysaasa.core.users.service.UserService;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	public String identifier;

	@Expose
	public long id;

	@Expose
	public Organization organization;

	@Expose
	public Website website;

	public ContactInfo contactInfo = new ContactInfo();

	public Boolean enabled = true;

	public List<GcmKey> gcmKeys;

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

	@Column(name = "enabled")
	public Boolean getEnabled() {
		if (enabled == null)
			return true;
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = "accessLevel")
	public AccessLevel getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(AccessLevel level) {
		this.accessLevel = level;
	}

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

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "organization")
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "contact")
	public ContactInfo getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(ContactInfo contactInfo) {
		if (contactInfo == null)
			contactInfo = new ContactInfo(); //Fix for old accounts without default Contact Info
		this.contactInfo = contactInfo;

	}

	@Column(name = "identifier")
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the expected identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;

	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "password_md5")
	public String getPassword_md5() {
		return password_md5;
	}

	public void setPassword_md5(String password_md5) {
		this.password_md5 = password_md5;
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

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = {CascadeType.ALL})
	@JoinTable
	public List<GcmKey> getGcmKeys() {
		return gcmKeys;
	}

	public void setGcmKeys(List<GcmKey> gcmKeys) {
		this.gcmKeys = gcmKeys;
	}

	public void addGcmKey(GcmKey gc_reg_id) {
		if (gcmKeys == null)
			gcmKeys = new ArrayList<>();
		if (gcmKeys.contains(gc_reg_id))
			return;
		gcmKeys.add(gc_reg_id);
	}

	public enum AccessLevel {
		ROOT(100), //All Access (hosting+all)
		ORG(70), //All Organization Access (blog+website+users)
		WWW(30), //All Website access (blog+website)
		GUEST(0); //Visitors, Members, whatever you want to call them, but not Admin users
		public final int priority;

		AccessLevel(int i) {
			this.priority = i;
		}
	}

	@Override
	public String toString() {
		return "User{" + "accessLevel=" + accessLevel + ", password_md5='" + password_md5 + '\'' + ", identifier='" + identifier + '\'' + ", id=" + id + ", organization=" + organization + ", website=" + website + ", contactInfo=" + contactInfo + ", enabled=" + enabled + ", gcmKeys=" + gcmKeys + '}';
	}
}
