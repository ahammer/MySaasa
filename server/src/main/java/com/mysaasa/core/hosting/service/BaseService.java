package com.mysaasa.core.hosting.service;

import com.mysaasa.Simple;

public class BaseService {
    public final void inject() {
        Simple.getInstance().getInjector().injectMembers(this);
    }

}
