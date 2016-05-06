package com.github.lukaspili.reactivebilling.parser;

import com.github.lukaspili.reactivebilling.model.PurchaseState;

/**
 * Created by lukasz on 06/05/16.
 */
public class PurchaseStateParser {

    public static PurchaseState parse(int value) {
        switch (value) {
            case 0:
                return PurchaseState.PURCHASED;
            case 1:
                return PurchaseState.CANCELED;
            case 2:
                return PurchaseState.REFUNDED;
            default:
                throw new IllegalArgumentException("Unknown purchase state: " + value);
        }
    }

}
