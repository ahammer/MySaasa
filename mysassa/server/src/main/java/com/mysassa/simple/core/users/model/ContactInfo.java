package com.mysassa.simple.core.users.model;

import com.google.gson.annotations.Expose;
import com.mysassa.simple.core.website.templating.QueryParamProxy;
import org.apache.wicket.request.IRequestParameters;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/*
 * Basic contact info
 */
@Entity
@Table(name = "ContactInfo")
public class ContactInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Expose
	public long id;
	@Expose
	private String name = "";

	@Expose
	private String email = "";
	@Expose
	private String country = "";
	@Expose
	private String city = "";
	@Expose
	private String province = "";
	@Expose
	private String address1 = "";
	@Expose
	private String address2 = "";
	@Expose
	private String postal = "";
	@Expose
	private String homePhone = "";
	@Expose
	private String mobilePhone = "";
	private String bitcoinAddress = "";

	public ContactInfo() {}

	public ContactInfo(ContactInfo ci) {
		name = ci.name;
		email = ci.email;
		city = ci.city;
		province = ci.province;
		address1 = ci.address1;
		address2 = ci.address2;
		postal = ci.postal;
		homePhone = ci.homePhone;
		mobilePhone = ci.mobilePhone;
		bitcoinAddress = ci.bitcoinAddress;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "address1")
	public String getAddress1() {
		return address1;
	}

	@Column(name = "address2")
	public String getAddress2() {
		return address2;
	}

	@Column(name = "city")
	public String getCity() {
		return city;
	}

	@Column(name = "country")
	public String getCountry() {
		return country;
	}

	@Column(name = "homephone")
	public String getHomePhone() {
		return homePhone;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	@Column(name = "mobilephone")
	public String getMobilePhone() {
		return mobilePhone;
	}

	@Column(name = "postal")
	public String getPostal() {
		return postal;
	}

	@Column(name = "province")
	public String getProvince() {
		return province;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public void setPostal(String postal) {
		this.postal = postal;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	@Column(name = "email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	//Used by TEMPLATE

	/**
	 * Supported Fields
	 *
	 * country
	 * email
	 * city
	 * province
	 * address1
	 * address2
	 * postal
	 * homePhone
	 * mobilePhone
	 * name
	 *
	 * This merges the Query Parameters (via the Template Proxy) to the address object.
	 * If the field names match, we apply them.
	 * @param params
	 */
	public void mergeAddressData(String prefix, QueryParamProxy params) {
		if (prefix == null)
			prefix = "";
		IRequestParameters p = params.getQueryParameters();
		for (String name : p.getParameterNames()) {
			if (p.getParameterValue(name).toString().trim().equals(""))
				continue;
			System.out.println("Found a parameter: " + name + " " + p.getParameterValue(name));
			if (name.equals(prefix + "country")) {
				setCountry(p.getParameterValue(name).toString());
			} else if (name.equals(prefix + "email")) {
				setEmail(p.getParameterValue(name).toString());
			} else if (name.equals(prefix + "city")) {
				setCity(p.getParameterValue(name).toString());
			} else if (name.equals(prefix + "province")) {
				setProvince(p.getParameterValue(name).toString());
			} else if (name.equals(prefix + "address1")) {
				setAddress1(p.getParameterValue(name).toString());
			} else if (name.equals(prefix + "address2")) {
				setAddress2(p.getParameterValue(name).toString());
			} else if (name.equals(prefix + "postal")) {
				setPostal(p.getParameterValue(name).toString());
			} else if (name.equals(prefix + "homePhone")) {
				setHomePhone(p.getParameterValue(name).toString());
			} else if (name.equals(prefix + "mobilePhone")) {
				setMobilePhone(p.getParameterValue(name).toString());
			} else if (name.equals(prefix + "name")) {
				setName(p.getParameterValue(name).toString());
			}

		}
		System.out.println("Updated: " + this);
	}

	@Column(name = "BitcoinAddress")
	public String getBitcoinAddress() {
		return bitcoinAddress;
	}

	public void setBitcoinAddress(String bitcoinAddress) {
		this.bitcoinAddress = bitcoinAddress;
	}

	@Override
	public String toString() {
		String output = "";
		if (has(name) && has(email)) {
			output += name + " (" + email + ")\n";
		} else if (has(name)) {
			output += name + "\n";
		}

		if (has(address1)) {
			output += address1 + "\n";
		}
		if (has(address2)) {
			output += address2 + "\n";
		}
		if (has(city) && has(province)) {
			output += city + ", " + province + "\n";
		} else if (has(city)) {
			output += city + "\n";
		} else if (has(province)) {
			output += province + "\n";
		}
		if (has(country)) {
			output += country + "\n";
		}

		if (has(postal)) {
			output += postal + "\n";
		}

		if (has(homePhone)) {
			output += "Home: " + homePhone + "\n";
		}

		if (has(mobilePhone)) {
			output += "Mobile: " + mobilePhone + "\n";

		}

		return output;
	}

	private boolean has(String field) {
		return (field != null && !field.trim().equals(""));
	}

	/**
	 * Basic information validation.
	 * @return
	 */
	public boolean readyForCheckout() {
		boolean ready = true;
		if (nullOrEmpty(name)) {
			ready = false;
		}
		if (nullOrEmpty(country)) {
			ready = false;
		}
		if (nullOrEmpty(city)) {
			ready = false;
		}
		if (nullOrEmpty(province)) {
			ready = false;
		}
		if (nullOrEmpty(address1)) {
			ready = false;
		}
		if (nullOrEmpty(postal)) {
			ready = false;
		}
		if (nullOrEmpty(homePhone) || nullOrEmpty(mobilePhone)) {
			ready = false;
		}

		return ready;
	}

	private boolean nullOrEmpty(String field) {
		return (field == null || field.trim().equals(""));
	}

}
