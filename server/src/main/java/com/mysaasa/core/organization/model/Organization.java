package com.mysaasa.core.organization.model;

import com.google.gson.annotations.Expose;
import com.mysaasa.core.organization.services.OrganizationService;
import com.mysaasa.core.users.model.ContactInfo;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.core.hosting.service.HostingService;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/*
 * An organization is a grouping of websites and apps and users.
 *
 */
@Entity
@Table(name = "Organization")
public class Organization implements Serializable {
	public static final long serialVersionUID = 1L;
	@Expose
	public long id;

	@Expose
	public ContactInfo contactInfo;

	@Expose
	public String name;

	public Boolean enabled = true;

	public String stripeKey;

	@Column(name = "stripeKey")
	public String getStripeKey() {
		return stripeKey;
	}

	public void setStripeKey(String stripeKey) {
		this.stripeKey = stripeKey;
	}

	public Organization() {
		contactInfo = new ContactInfo();
	}

	public Organization(String name) {
		this.name = name;
	}

	@Column(name = "enabled")
	public Boolean isEnabled() {
		if (enabled == null)
			return true;
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "contactInfo")
	public ContactInfo getContactInfo() {
		if (contactInfo == null)
			contactInfo = new ContactInfo();
		return contactInfo;
	}

	public void setContactInfo(ContactInfo contactInfo) {
		if (contactInfo == null)
			contactInfo = new ContactInfo();
		this.contactInfo = contactInfo;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Organization that = (Organization) o;

		if (id != that.id)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}

	public List<Website> retrieveWebsites() {

		return HostingService.get().getWebsites(this);
	}

	public Organization save() {
		return OrganizationService.get().saveOrganization(this);
	}

	@Override
	public String toString() {
		return name;
	}

}
