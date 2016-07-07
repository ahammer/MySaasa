package com.mysaasa.api.enums;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;

/**
 * Created by adam on 2014-10-31.
 */
public enum Currency {
    CAD("CAD");
    @Expose
    String unit;
    Currency (String unit) {
        this.unit = unit;
    }

    public static Currency fromString(String currency) {
        for (Currency c: values()) {
            if (c.unit.equals(currency.toUpperCase())) {
                return c;
            }
        }
        return null;
    }
    public String printAmount(BigDecimal bigDecimal) {
        switch (this) {
            case CAD:
                return "$"+bigDecimal.setScale(2, BigDecimal.ROUND_UP);
        }
        return bigDecimal.toString();
    }

}
