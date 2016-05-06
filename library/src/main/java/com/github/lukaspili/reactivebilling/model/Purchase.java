package com.github.lukaspili.reactivebilling.model;

/**
 * Created by lukasz on 06/05/16.
 */
public class Purchase {

    private final String orderId;
    private final String packageName;
    private final String productId;
    private final String developerPayload;
    private final String purchaseToken;
    private final PurchaseState purchaseState;
    private final long purchaseTime;
    private final boolean autoRenewing;

    public Purchase(String orderId, String packageName, String productId, String developerPayload, String purchaseToken, PurchaseState purchaseState, long purchaseTime, boolean autoRenewing) {
        this.orderId = orderId;
        this.packageName = packageName;
        this.productId = productId;
        this.developerPayload = developerPayload;
        this.purchaseToken = purchaseToken;
        this.purchaseState = purchaseState;
        this.purchaseTime = purchaseTime;
        this.autoRenewing = autoRenewing;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getProductId() {
        return productId;
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public PurchaseState getPurchaseState() {
        return purchaseState;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public boolean isAutoRenewing() {
        return autoRenewing;
    }
}
