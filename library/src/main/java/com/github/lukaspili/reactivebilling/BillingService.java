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
import com.github.lukaspili.reactivebilling.response.GetBuyIntent;
import com.github.lukaspili.reactivebilling.response.GetPurchases;
import com.github.lukaspili.reactivebilling.response.GetSkuDetails;
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
        ReactiveBillingLogger.log("Is billing supported - request (thread %s)", Thread.currentThread().getName());

        int response = billingService.isBillingSupported(BillingService.API_VERSION, context.getPackageName(), purchaseType.getIdentifier());
        ReactiveBillingLogger.log("Is billing supported - response: %d", response);
        return new Response(response);
    }

    public Response consumePurchase(String purchaseToken) throws RemoteException {
        ReactiveBillingLogger.log("Consume purchase - request (thread %s)", Thread.currentThread().getName());

        int response = billingService.consumePurchase(BillingService.API_VERSION, context.getPackageName(), purchaseToken);
        ReactiveBillingLogger.log("Consume purchase - response: %d", response);
        return new Response(response);
    }

    public GetPurchases getPurchases(PurchaseType purchaseType, String continuationToken) throws RemoteException {
        ReactiveBillingLogger.log("Get purchases - request (thread %s)", Thread.currentThread().getName());
        Bundle bundle = billingService.getPurchases(BillingService.API_VERSION, context.getPackageName(), purchaseType.getIdentifier(), continuationToken);

        int response = bundle.getInt("RESPONSE_CODE", -1);
        ReactiveBillingLogger.log("Get purchases - response code: %s", response);

        if (response != 0) {
            return new GetPurchases(response, null, null);
        }

        List<String> productsIds = bundle.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
        List<String> purchases = bundle.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
        List<String> signatures = bundle.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");

        List<GetPurchases.Item> items = new ArrayList<>();
        for (int i = 0; i < productsIds.size(); i++) {
            items.add(new GetPurchases.Item(
                    productsIds.get(i),
                    signatures.get(i),
                    PurchaseParser.parse(purchases.get(i))
            ));
        }

        ReactiveBillingLogger.log("Get purchases - items size: %s", items.size());
        return new GetPurchases(response, items, bundle.getString("INAPP_CONTINUATION_TOKEN"));
    }

    public GetSkuDetails getSkuDetails(PurchaseType purchaseType, String... productIds) throws RemoteException {
        if (productIds == null || productIds.length == 0) {
            throw new IllegalArgumentException("Product ids cannot be blank");
        }

        ReactiveBillingLogger.log("Get sku details - request: %s (thread %s)", TextUtils.join(", ", productIds), Thread.currentThread().getName());

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("ITEM_ID_LIST", new ArrayList(Arrays.asList(productIds)));

        bundle = billingService.getSkuDetails(BillingService.API_VERSION, context.getPackageName(), purchaseType.getIdentifier(), bundle);

        int response = bundle.getInt("RESPONSE_CODE", -1);
        ReactiveBillingLogger.log("Get sku details - response code: %s", response);

        if (response != 0) {
            return new GetSkuDetails(response, null);
        }

        List<String> detailsJson = bundle.getStringArrayList("DETAILS_LIST");
        List<SkuDetails> skuDetailsList = new ArrayList<>();

        if (detailsJson == null || detailsJson.isEmpty()) {
            ReactiveBillingLogger.log("Get sku details - empty list");
            return new GetSkuDetails(response, skuDetailsList);
        }

        SkuDetails skuDetails;
        for (int i = 0; i < detailsJson.size(); i++) {
            skuDetails = SkuDetailsParser.parse(detailsJson.get(i));
            if (skuDetails != null) {
                skuDetailsList.add(skuDetails);
            }
        }

        ReactiveBillingLogger.log("Get sku details - list size: %s", skuDetailsList.size());
        return new GetSkuDetails(response, skuDetailsList);
    }

    public GetBuyIntent getBuyIntent(String productId, PurchaseType purchaseType, String developerPayload) throws RemoteException {
        ReactiveBillingLogger.log("Get buy intent - request: %s (thread %s)", productId, Thread.currentThread().getName());

        Bundle bundle = billingService.getBuyIntent(BillingService.API_VERSION, context.getPackageName(), productId, purchaseType.getIdentifier(), developerPayload);

        int response = bundle.getInt("RESPONSE_CODE", -1);
        ReactiveBillingLogger.log("Get buy intent - response code: %s", response);

        if (response != 0) {
            return new GetBuyIntent(response, null);
        }

        PendingIntent buyIntent = bundle.getParcelable("BUY_INTENT");
        return new GetBuyIntent(response, buyIntent);
    }
}
