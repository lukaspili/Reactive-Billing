package com.github.lukaspili.reactivebilling;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import com.android.vending.billing.IInAppBillingService;
import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.model.SkuDetails;
import com.github.lukaspili.reactivebilling.parser.PurchaseParser;
import com.github.lukaspili.reactivebilling.parser.SkuDetailsParser;
import com.github.lukaspili.reactivebilling.response.GetBuyIntentResponse;
import com.github.lukaspili.reactivebilling.response.GetPurchasesResponse;
import com.github.lukaspili.reactivebilling.response.GetSkuDetailsResponse;
import com.github.lukaspili.reactivebilling.response.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lukasz on 04/05/16.
 */
public class BillingService {

    private static final int API_VERSION = 3;

    private final Context context;
    private final IInAppBillingService billingService;

    public BillingService(Context context, IInAppBillingService billingService) {
        this.context = context;
        this.billingService = billingService;
    }

    public Response isBillingSupported(PurchaseType purchaseType) throws RemoteException {
        ReactiveBilling.log(null, "Is billing supported - request (thread %s)", Thread.currentThread().getName());

        int response = billingService.isBillingSupported(BillingService.API_VERSION, context.getPackageName(), purchaseType.getIdentifier());
        ReactiveBilling.log(null, "Is billing supported - response: %d", response);
        return new Response(response);
    }

    public Response consumePurchase(String purchaseToken) throws RemoteException {
        ReactiveBilling.log(null, "Consume purchase - request (thread %s)", Thread.currentThread().getName());

        int response = billingService.consumePurchase(BillingService.API_VERSION, context.getPackageName(), purchaseToken);
        ReactiveBilling.log(null, "Consume purchase - response: %d", response);
        return new Response(response);
    }

    public GetPurchasesResponse getPurchases(PurchaseType purchaseType, String continuationToken) throws RemoteException {
        ReactiveBilling.log(null, "Get purchases - request (thread %s)", Thread.currentThread().getName());
        Bundle bundle = billingService.getPurchases(BillingService.API_VERSION, context.getPackageName(), purchaseType.getIdentifier(), continuationToken);

        int response = bundle.getInt("RESPONSE_CODE", -1);
        ReactiveBilling.log(null, "Get purchases - response code: %s", response);

        if (response != 0) {
            return new GetPurchasesResponse(response, null, null);
        }

        List<String> productsIds = bundle.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
        List<String> purchases = bundle.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
        List<String> signatures = bundle.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");

        List<GetPurchasesResponse.PurchaseResponse> purchaseResponses = new ArrayList<>();
        if (productsIds != null && purchases != null && signatures != null) {
            for (int i = 0; i < productsIds.size(); i++) {
                purchaseResponses.add(
                    new GetPurchasesResponse.PurchaseResponse(productsIds.get(i), signatures.get(i),
                        PurchaseParser.parse(purchases.get(i))));
            }
        }

        ReactiveBilling.log(null, "Get purchases - items size: %s", purchaseResponses.size());
        return new GetPurchasesResponse(response, purchaseResponses, bundle.getString("INAPP_CONTINUATION_TOKEN"));
    }

    public GetSkuDetailsResponse getSkuDetails(PurchaseType purchaseType, List<String> productIds) throws RemoteException {
        if (productIds == null || productIds.size() == 0) {
            throw new IllegalArgumentException("Product ids cannot be blank");
        }

        ReactiveBilling.log(null, "Get sku details - request: %s (thread %s)", TextUtils.join(", ", productIds), Thread.currentThread().getName());

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("ITEM_ID_LIST", new ArrayList<>(productIds));

        bundle = billingService.getSkuDetails(BillingService.API_VERSION, context.getPackageName(), purchaseType.getIdentifier(), bundle);

        int response = bundle.getInt("RESPONSE_CODE", -1);
        ReactiveBilling.log(null, "Get sku details - response code: %s", response);

        if (response != 0) {
            return new GetSkuDetailsResponse(response, null);
        }

        List<String> detailsJson = bundle.getStringArrayList("DETAILS_LIST");
        List<SkuDetails> skuDetailsList = new ArrayList<>();

        if (detailsJson == null || detailsJson.isEmpty()) {
            ReactiveBilling.log(null, "Get sku details - empty list");
            return new GetSkuDetailsResponse(response, skuDetailsList);
        }

        SkuDetails skuDetails;
        for (int i = 0; i < detailsJson.size(); i++) {
            skuDetails = SkuDetailsParser.parse(detailsJson.get(i));
            if (skuDetails != null) {
                skuDetailsList.add(skuDetails);
            }
        }

        ReactiveBilling.log(null, "Get sku details - list size: %s", skuDetailsList.size());
        return new GetSkuDetailsResponse(response, skuDetailsList);
    }

    public GetBuyIntentResponse getBuyIntent(String productId, PurchaseType purchaseType, String developerPayload) throws RemoteException {
        ReactiveBilling.log(null, "Get buy intent - request: %s (thread %s)", productId, Thread.currentThread().getName());

        Bundle bundle = billingService.getBuyIntent(BillingService.API_VERSION, context.getPackageName(), productId, purchaseType.getIdentifier(), developerPayload);

        int response = bundle.getInt("RESPONSE_CODE", -1);
        ReactiveBilling.log(null, "Get buy intent - response code: %s", response);

        if (response != 0) {
            return new GetBuyIntentResponse(response, null);
        }

        PendingIntent buyIntent = bundle.getParcelable("BUY_INTENT");
        return new GetBuyIntentResponse(response, buyIntent);
    }
}
