package com.mysaasa.api.model;

import com.google.gson.JsonObject;

import java.io.Serializable;

/*
 * Basic contact info
 */
public class ContactInfo implements Serializable {
    public static final long serialVersionUID = 1L;

    public final long id;

    public final String name;
    public final String email;

    public final String country;
    public final String city;
    public final String province;
    public final String address1;
    public final String address2;
    public final String postal;
    public final String homePhone;
    public final String mobilePhone;

    public ContactInfo(long id, String name, String email, String country, String city, String province, String address1, String address2, String postal, String homePhone, String mobilePhone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.country = country;
        this.city = city;
        this.province = province;
        this.address1 = address1;
        this.address2 = address2;
        this.postal = postal;
        this.homePhone = homePhone;
        this.mobilePhone = mobilePhone;
    }

    public ContactInfo(JsonObject shippingInformation) {
        id = shippingInformation.get("id").getAsLong();
        country = shippingInformation.has("country")?shippingInformation.get("country").getAsString():"";
        name = shippingInformation.has("name")?shippingInformation.get("name").getAsString():"";
        email = shippingInformation.has("email")?shippingInformation.get("email").getAsString():"";
        city = shippingInformation.has("city")?shippingInformation.get("city").getAsString():"";
        province = shippingInformation.has("province")?shippingInformation.get("province").getAsString():"";
        address1 = shippingInformation.has("address1")?shippingInformation.get("address1").getAsString():"";
        address2 = shippingInformation.has("address2")?shippingInformation.get("address2").getAsString():"";
        postal = shippingInformation.has("postal")?shippingInformation.get("postal").getAsString():"";
        homePhone = shippingInformation.has("homePhone")?shippingInformation.get("homePhone").getAsString():"";
        mobilePhone = shippingInformation.has("mobilePhone")?shippingInformation.get("mobilePhone").getAsString():"";
    }

    @Override
    public String toString() {
        String output = "";
        if (has(name) && has(email)) {
            output += name + " ("+email+")\n";
        } else if (has(name)) {
            output += name + "\n";
        }

        if (has(address1)) {
            output += address1 + "\n";
        }
        if (has(address2)) {
            output += address2 + "\n";
        }
        if (has(city) && has(province)) {
            output += city +", "+province+"\n";
        } else if (has(city)) {
            output += city +"\n";
        } else if (has(province)) {
            output += province +"\n";
        }

        if (has(country)) {
            output+= country + "\n";
        }


        if (has(postal)) {
            output+= postal + "\n";
        }

        if (has(homePhone)) {
            output+= "Home: "+homePhone+"\n";
        }

        if (has(mobilePhone)) {
            output+= "Mobile: "+mobilePhone+"\n";

        }
        if (output.length() > 0) {
            return output.substring(0,output.length()-1);
        }
        return output;

    }

    private boolean has(String field) {
        return field != null && field.trim().length() != 0;
    }

    public boolean isPopulated() {
        return toString().trim()!="";
    }
}