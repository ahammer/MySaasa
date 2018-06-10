package com.mysaasa.core.marketing.model;

import com.google.gson.annotations.Expose;
import com.mysaasa.core.users.model.User;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

/**
 * This tracks the User->Users relationship that is referrals
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
    long userId;

    @Expose
    List<Long> referrals;

    @Expose
    Integer available = INITIAL_REFERRAL;

    public UserReferrals(){}
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
}
