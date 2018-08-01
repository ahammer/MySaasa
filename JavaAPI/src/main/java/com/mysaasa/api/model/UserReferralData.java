package com.mysaasa.api.model;

import java.util.Collections;
import java.util.List;

public class UserReferralData implements IUserReferralData {
    private long id;
    private int userId;
    private List<Integer> referrals = Collections.emptyList();
    private List<Integer> pyramid = Collections.emptyList();
    private int available;

    public UserReferralData() { }

    public UserReferralData(long id, int userId, int available) {
        this.id = id;
        this.userId = userId;
        this.available = available;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public int getUserId() {
        return userId;
    }

    @Override
    public List<Integer> getReferrals() {
        return referrals;
    }

    @Override
    public List<Integer> getPyramid() {
        return pyramid;
    }

    @Override
    public int getAvailable() {
        return available;
    }
}
