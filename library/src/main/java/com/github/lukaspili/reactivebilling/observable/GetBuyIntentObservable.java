package com.github.lukaspili.reactivebilling.observable;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.lukaspili.reactivebilling.BillingService;
import com.github.lukaspili.reactivebilling.PurchaseFlowService;
import com.github.lukaspili.reactivebilling.ReactiveBilling;
import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.response.GetBuyIntentResponse;
import com.github.lukaspili.reactivebilling.response.Response;

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
        GetBuyIntentResponse response;
        try {
            response = billingService.getBuyIntent(productId, purchaseType, developerPayload);

            observer.onNext(response);
            observer.onCompleted();
        } catch (RemoteException e) {
            observer.onError(e);
            return;
        }

        ReactiveBilling.log(null, "Will start purchase flow: %b (thread %s)", response.isSuccess(), Thread.currentThread().getName());
        if (response.isSuccess()) {
            purchaseFlowService.startFlow(response.getIntent(), extras);
        }
    }
}
