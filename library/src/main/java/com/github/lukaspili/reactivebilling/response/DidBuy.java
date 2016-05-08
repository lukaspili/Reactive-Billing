package com.github.lukaspili.reactivebilling.response;

import com.github.lukaspili.reactivebilling.model.Purchase;

/**
 * Created by lukasz on 06/05/16.
 */
public class DidBuy extends Response {

    public static final int LOCAL_ERROR_RESPONSE_CODE = -1;

    public static DidBuy error(Throwable throwable) {
        return new DidBuy(LOCAL_ERROR_RESPONSE_CODE, null, null, throwable);
    }

    public static DidBuy invalid(int responseCode) {
        return new DidBuy(responseCode, null, null, null);
    }

    public static DidBuy valid(int responseCode, Purchase purchase, String signature) {
        return new DidBuy(responseCode, purchase, signature, null);
    }

    private final Purchase purchase;
    private final String signature;
    private final Throwable throwable;

    DidBuy(int responseCode, Purchase purchase, String signature, Throwable throwable) {
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
