package com.mysassa.core.categories.model;

import com.google.gson.annotations.Expose;
import com.mysassa.core.categories.CategoryService;
import com.mysassa.core.organization.model.Organization;

import javax.persistence.*;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * A category, generic
 */
@Entity
public class Category implements Serializable {
	public static final long serialVersionUID = 1L;
	@Expose
	public long id;
	@Expose
	public String name;
	public Organization organization;
	public String type;

	public Category() {}

	public Category(String type, Organization organization) {
		this.type = type;
		this.organization = organization;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "organization")
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static Category fromString(String name, Class type, Organization organization) {
		CategoryService service = CategoryService.get();
		return service.findCategory(name, type, organization);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Category category = (Category) o;

		if (id != category.id)
			return false;
		if (name != null ? !name.equals(category.name) : category.name != null)
			return false;
		if (organization != null ? !organization.equals(category.organization) : category.organization != null)
			return false;
		if (type != null ? !type.equals(category.type) : category.type != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (organization != null ? organization.hashCode() : 0);
		return result;
	}

	public String toFriendlyString() {

		return toFriendlyType() + " : " + name;
	}

	public String toFriendlyType() {
		String[] t = type.split(Pattern.quote("."));
		return t[t.length - 1];
	}
}
