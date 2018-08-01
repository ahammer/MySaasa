package com.mysaasa.api.responses;

import com.mysaasa.api.model.UserReferralData;

public class UserReferralDataResponse extends SimpleResponse {
    public UserReferralData getData() {
        return data;
    }

    private UserReferralData data;
}
