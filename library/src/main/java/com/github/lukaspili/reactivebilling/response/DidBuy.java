package com.github.lukaspili.reactivebilling.response;

import com.github.lukaspili.reactivebilling.model.Purchase;

/**
 * Created by lukasz on 06/05/16.
 */
public class DidBuy extends Response {

    public static DidBuy error(Throwable throwable) {
        return new DidBuy(-1, null, null, throwable);
    }

    private final Purchase purchase;
    private final String signature;
    private final Throwable throwable;

    public DidBuy(int responseCode, Purchase purchase, String signature, Throwable throwable) {
        super(responseCode);
        this.purchase = purchase;
        this.signature = signature;
        this.throwable = throwable;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public String getSignature() {
        return signature;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
