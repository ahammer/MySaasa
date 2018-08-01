package com.mysaasa.api.model;

import java.util.List;

public interface IUserReferralData {
    long getId();

    int getUserId();

    List<Integer> getReferrals();

    List<Integer> getPyramid();

    int getAvailable();
}
