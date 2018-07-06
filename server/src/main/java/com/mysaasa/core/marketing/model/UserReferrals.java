package com.mysaasa.core.marketing.model;

import com.google.gson.annotations.Expose;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This tracks the User to Users relationship that is referrals
 *
 * This simply tracks User ID's in a Tree
 */
@Entity
@Table(name = "UserReferrals")
public class UserReferrals {
	final static Integer INITIAL_REFERRAL = 2;

	@Expose
	public long id;

	@Expose
	Long userId;

	@Expose
	Long parentId;

	@Expose
	List<Long> referrals;

	//Index 0 = Second/Direct referrals
	//
	@Expose
	List<Integer> pyramid;

	@Expose
	Integer available = INITIAL_REFERRAL;

	@ElementCollection
	@Column(name = "Pyramid")
	public List<Integer> getPyramid() {
		if (pyramid == null)
			return new ArrayList<>();
		return pyramid;
	}

	public void incrementLevel(int level) {

		List<Integer> pyramid = getPyramid();
		if (pyramid == null) {
			pyramid = new ArrayList<>();
		}
		while (pyramid.size() <= level) {
			pyramid.add(0);
		}

		pyramid.set(level, pyramid.get(level) + 1);
	}

	public void setPyramid(List<Integer> pyramid) {
		this.pyramid = pyramid;
	}

	public UserReferrals() {}

	public UserReferrals(long userId) {
		this.userId = userId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long getId() {
		return id;
	}

	@Column(name = "userId")
	public Long getUserId() {
		return userId;
	}

	@ElementCollection
	@Column(name = "referralIds")
	public List<Long> getReferrals() {
		return referrals;
	}

	public int getAvailableReferrals() {
		return available;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setReferrals(List<Long> referrals) {
		this.referrals = referrals;
	}

	public void setAvailableReferrals(Integer available) {
		this.available = available;
	}

	public void decrementAvailableReferrals() {
		this.available--;
		//Clip to 0
		if (this.available < 0)
			this.available = 0;
	}

	@Column(name = "parentId")
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
}
