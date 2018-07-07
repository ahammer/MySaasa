package com.mysaasa.api.model;

import java.util.Collections;
import java.util.List;

public class UserReferralData {
    public long id;
    public int userId;
    public List<Integer> referrals = Collections.emptyList();
    public List<Integer> pyramid = Collections.emptyList();
    public int available;

}
