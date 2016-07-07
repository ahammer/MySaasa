package com.mysaasa.api.model;

import com.google.gson.JsonObject;

/**
 * Created by adam on 2014-10-31.
 */
public class Organization {
    private final long id;
    private final String json;
    private final String name;
    private final ContactInfo contactInfo;

    public Organization(JsonObject organization) {
        json = organization.toString();
        name = organization.get("name").getAsString();
        id = organization.get("id").getAsLong();
        contactInfo = new ContactInfo(organization.getAsJsonObject("contactInfo"));


    }

    @Override
    public String toString() {
        return name+"\n"+contactInfo.toString();
    }
}
