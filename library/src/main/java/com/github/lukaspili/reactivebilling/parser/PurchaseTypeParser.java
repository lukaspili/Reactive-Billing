package com.github.lukaspili.reactivebilling.parser;

import android.support.annotation.NonNull;

import com.github.lukaspili.reactivebilling.model.PurchaseType;

/**
 * Created by lukasz on 06/05/16.
 */
public class PurchaseTypeParser {

    public static PurchaseType parse(@NonNull String value) {
        switch (value) {
            case "inapp":
                return PurchaseType.PRODUCT;
            case "subs":
                return PurchaseType.SUBSCRIPTION;
            default:
                throw new IllegalArgumentException("Unknown purchase type: " + value);
        }
    }

}
