package com.github.lukaspili.reactivebilling.model;

/**
 * Created by lukasz on 06/05/16.
 */
public enum PurchaseState {

    PURCHASED(0), CANCELED(1), REFUNDED(2);

    private final int value;

    PurchaseState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
