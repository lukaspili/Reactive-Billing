package com.github.lukaspili.reactivebilling.model;

/**
 * Created by lukasz on 06/05/16.
 */
public class SkuDetails {

    private final String productId;

    private final long priceAmountMicros;

    private final PurchaseType purchaseType;

    private final String price;

    private final String priceCurrencyCode;

    private final String title;

    private final String description;

    public SkuDetails(String productId, long priceAmountMicros, PurchaseType purchaseType, String price, String priceCurrencyCode, String title, String description) {
        this.productId = productId;
        this.priceAmountMicros = priceAmountMicros;
        this.purchaseType = purchaseType;
        this.price = price;
        this.priceCurrencyCode = priceCurrencyCode;
        this.title = title;
        this.description = description;
    }

    public String getProductId() {
        return productId;
    }

    public PurchaseType getPurchaseType() {
        return purchaseType;
    }

    public String getPrice() {
        return price;
    }

    public long getPriceAmountMicros() {
        return priceAmountMicros;
    }

    public String getPriceCurrencyCode() {
        return priceCurrencyCode;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
