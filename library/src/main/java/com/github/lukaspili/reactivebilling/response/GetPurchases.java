package com.github.lukaspili.reactivebilling.response;

import com.github.lukaspili.reactivebilling.model.Purchase;

import java.util.List;

/**
 * Created by lukasz on 04/05/16.
 */
public class GetPurchases extends Response {

    private final List<Item> items;
    private final String continuationToken;

    public GetPurchases(int responseCode, List<Item> items, String continuationToken) {
        super(responseCode);
        this.items = items;
        this.continuationToken = continuationToken;
    }

    public List<Item> getItems() {
        return items;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    public static class Item {

        private final String productId;
        private final String signature;
        private final Purchase purchase;

        public Item(String productId, String signature, Purchase purchase) {
            this.productId = productId;
            this.signature = signature;
            this.purchase = purchase;
        }

        public String getProductId() {
            return productId;
        }

        public String getSignature() {
            return signature;
        }

        public Purchase getPurchase() {
            return purchase;
        }
    }
}
