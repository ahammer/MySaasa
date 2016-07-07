package com.mysaasa.api.model;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class User implements Serializable {
    public static final long serialVersionUID = 1L;
    public final AccessLevel accessLevel;
    public final String nonce;
    public final String password_md5;
    public final long id;
    public final String identifier;

    public final ContactInfo contactInfo;

    public User(AccessLevel accessLevel, String nonce, String password_md5, int id, String identifier, ContactInfo contactInfo) {
        this.accessLevel = accessLevel;
        this.nonce = nonce;
        this.password_md5 = password_md5;
        this.id = id;
        this.identifier = identifier;
        this.contactInfo = contactInfo;
    }

    public User(JsonObject data) {

        nonce = "";
        password_md5 = "";
        id = data.get("id").getAsInt();
        identifier = data.get("identifier").getAsString();
        if (data.has("accessLevel")) {
            accessLevel = AccessLevel.valueOf(data.get("accessLevel").getAsString());
        } else {
            accessLevel = AccessLevel.GUEST;
        }
        if (data.has("contactInfo")) {
            contactInfo = new ContactInfo(data.getAsJsonObject("contactInfo"));
        } else {
            contactInfo = null;
        }

    }

    public static enum AccessLevel {
        ROOT,       //All Access (hosting+all)
        ORG,        //All Organization Access (blog+website+users)
        WWW,        //All Website access (blog+website)
        GUEST;      //Visitors, Members, whatever you want to call them, but not Admin users
    }

    @Override
    public String toString() {
        return "User{" +
                "identifier='" + identifier + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
