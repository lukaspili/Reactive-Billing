package com.github.lukaspili.reactivebilling.response;

import com.github.lukaspili.reactivebilling.model.Purchase;

import java.util.List;

/**
 * Created by lukasz on 04/05/16.
 */
public class GetPurchasesResponse extends Response {

    private final List<PurchaseResponse> list;
    private final String continuationToken;

    public GetPurchasesResponse(int responseCode, List<PurchaseResponse> list, String continuationToken) {
        super(responseCode);
        this.list = list;
        this.continuationToken = continuationToken;
    }

    public List<PurchaseResponse> getList() {
        return list;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    public static class PurchaseResponse {

        private final String productId;
        private final String signature;
        private final Purchase purchase;

        public PurchaseResponse(String productId, String signature, Purchase purchase) {
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
