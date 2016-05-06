package com.github.lukaspili.reactivebilling.parser;

import com.github.lukaspili.reactivebilling.model.PurchaseType;

/**
 * Created by lukasz on 06/05/16.
 */
public class PurchaseTypeParser {

    public static PurchaseType parse(String value) {
        if (value.equals("inapp")) {
            return PurchaseType.PRODUCT;
        } else if (value.equals("subs")) {
            return PurchaseType.SUBSCRIPTION;
        } else {
            throw new IllegalArgumentException("Unknown purchase type: " + value);
        }
    }

}
