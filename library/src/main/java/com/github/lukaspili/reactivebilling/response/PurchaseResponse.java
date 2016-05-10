package com.github.lukaspili.reactivebilling.response;

import android.os.Bundle;

import com.github.lukaspili.reactivebilling.model.Purchase;

/**
 * Created by lukasz on 06/05/16.
 */
public class PurchaseResponse extends Response {

    private final Purchase purchase;
    private final String signature;
    private final Bundle extras;
    private final boolean isCancelled;

    public PurchaseResponse(int responseCode, Purchase purchase, String signature, Bundle extras, boolean isCancelled) {
        super(responseCode);
        this.purchase = purchase;
        this.signature = signature;
        this.extras = extras;
        this.isCancelled = isCancelled;
    }

    @Override
    public boolean isSuccess() {
        return super.isSuccess() && !isCancelled;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public String getSignature() {
        return signature;
    }

    public Bundle getExtras() {
        return extras;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
