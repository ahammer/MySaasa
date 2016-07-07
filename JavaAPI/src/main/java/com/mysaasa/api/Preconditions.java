package com.mysaasa.api;

/**
 * Created by adamhammer2 on 2016-07-05.
 */
public class Preconditions {
    public static void checkNotNull(Object obj) {
        if (obj == null) throw new IllegalArgumentException("Can not be null");
    }
}
