package com.github.lukaspili.reactivebilling.model;

/**
 * Created by lukasz on 04/05/16.
 */
public enum PurchaseType {
    PRODUCT("inapp"), SUBSCRIPTION("subs");

    private final String identifier;

    PurchaseType(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
