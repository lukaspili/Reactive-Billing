package com.github.lukaspili.reactivebilling.observable;

import android.content.Context;
import android.os.RemoteException;

import com.github.lukaspili.reactivebilling.BillingService;
import com.github.lukaspili.reactivebilling.PurchaseFlowService;
import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.response.GetBuyIntent;

import rx.Observable;
import rx.Observer;

public class GetBuyIntentObservable extends BaseObservable<GetBuyIntent> {

    public static Observable<GetBuyIntent> create(Context context, String productId, PurchaseType purchaseType, String developerPayload) {
        return Observable.create(new GetBuyIntentObservable(context, productId, purchaseType, developerPayload));
    }

    private final String productId;
    private final PurchaseType purchaseType;
    private final String developerPayload;

    protected GetBuyIntentObservable(Context context, String productId, PurchaseType purchaseType, String developerPayload) {
        super(context);
        this.productId = productId;
        this.purchaseType = purchaseType;
        this.developerPayload = developerPayload;
    }

    @Override
    protected void onBillingServiceReady(BillingService billingService, Observer<? super GetBuyIntent> observer) {
        try {
            observer.onNext(billingService.getBuyIntent(productId, purchaseType, developerPayload));
            observer.onCompleted();
        } catch (RemoteException e) {
            observer.onError(e);
        }
    }
}
