package com.github.lukaspili.reactivebilling.observable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.lukaspili.reactivebilling.BillingService;
import com.github.lukaspili.reactivebilling.PurchaseFlowService;
import com.github.lukaspili.reactivebilling.ReactiveBilling;
import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.parser.PurchaseParser;
import com.github.lukaspili.reactivebilling.response.GetBuyIntentResponse;
import com.github.lukaspili.reactivebilling.response.GetPurchasesResponse;
import com.github.lukaspili.reactivebilling.response.Response;

import java.util.List;

import rx.Observable;
import rx.Observer;

public class GetBuyIntentObservable extends BaseObservable<Response> {

    @NonNull
    public static Observable<Response> create(@NonNull Context context,
                                              @NonNull PurchaseFlowService purchaseFlowService,
                                              @NonNull String productId,
                                              @NonNull PurchaseType purchaseType,
                                              @Nullable String developerPayload,
                                              @Nullable Bundle extras) {
        return Observable.create(new GetBuyIntentObservable(context, purchaseFlowService, productId, purchaseType, developerPayload, extras));
    }

    private final PurchaseFlowService purchaseFlowService;
    private final String productId;
    private final PurchaseType purchaseType;
    private final String developerPayload;
    private final Bundle extras;

    protected GetBuyIntentObservable(Context context, PurchaseFlowService purchaseFlowService, String productId, PurchaseType purchaseType, String developerPayload, Bundle extras) {
        super(context);
        this.purchaseFlowService = purchaseFlowService;
        this.productId = productId;
        this.purchaseType = purchaseType;
        this.developerPayload = developerPayload;
        this.extras = extras;
    }

    @Override
    protected void onBillingServiceReady(BillingService billingService, Observer<? super Response> observer) {
        GetBuyIntentResponse response = null;
        try {
            boolean tryRestoreFirst = extras.getBoolean("TRY_RESTORE_FIRST", false);
            GetPurchasesResponse purchasesResponse = billingService.getPurchases(purchaseType, null);

            if (tryRestoreFirst && purchasesResponse.isSuccess()) {
                List<GetPurchasesResponse.PurchaseResponse> purchasesList = purchasesResponse.getList();
                for (GetPurchasesResponse.PurchaseResponse purchaseResponse : purchasesList) {
                    if (purchaseResponse.getProductId().equals(productId)) {
                        Intent data = new Intent();
                        data.putExtra("INAPP_PURCHASE_DATA", PurchaseParser.toString(purchaseResponse.getPurchase()));
                        data.putExtra("INAPP_DATA_SIGNATURE", purchaseResponse.getSignature());
                        data.putExtra("RESPONSE_CODE", 0);

                        purchaseFlowService.onActivityResult(Activity.RESULT_OK, data, new Bundle());
                        observer.onNext(response);
                        observer.onCompleted();

                        ReactiveBilling.log(null, "Product was restored, purchase flow will not start (thread %s)", response.isSuccess(), Thread.currentThread().getName());
                        return;
                    }
                }
            }

            response = billingService.getBuyIntent(productId, purchaseType, developerPayload);
            observer.onNext(response);
            observer.onCompleted();

            ReactiveBilling.log(null, "Will start purchase flow: %b (thread %s)", response.isSuccess(), Thread.currentThread().getName());
            if (response.isSuccess()) {
                purchaseFlowService.startFlow(response.getIntent(), extras);
            }
        } catch (RemoteException e) {
            observer.onError(e);
        }
    }
}
