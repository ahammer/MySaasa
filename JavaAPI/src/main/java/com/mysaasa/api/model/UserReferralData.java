package com.mysaasa.api.model;

import java.util.Collections;
import java.util.List;

public class UserReferralData {
    private long id;
    private int userId;
    private List<Integer> referrals = Collections.emptyList();
    private List<Integer> pyramid = Collections.emptyList();
    private int available;

    public long getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public List<Integer> getReferrals() {
        return referrals;
    }

    public List<Integer> getPyramid() {
        return pyramid;
    }

    public int getAvailable() {
        return available;
    }
}
