package com.github.lukaspili.reactivebilling.observable;

import android.content.Context;
import android.os.RemoteException;

import com.github.lukaspili.reactivebilling.BillingService;
import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.response.GetSkuDetails;

import rx.Observable;
import rx.Observer;

public class GetSkuDetailsObservable extends BaseObservable<GetSkuDetails> {

    public static Observable<GetSkuDetails> create(Context context, PurchaseType purchaseType, String... productIds) {
        return Observable.create(new GetSkuDetailsObservable(context, purchaseType, productIds));
    }

    private PurchaseType purchaseType;
    private String[] productIds;

    protected GetSkuDetailsObservable(Context context, PurchaseType purchaseType, String... productIds) {
        super(context);
        this.purchaseType = purchaseType;
        this.productIds = productIds;
    }

    @Override
    protected void onBillingServiceReady(BillingService billingService, Observer<? super GetSkuDetails> observer) {
        try {
            GetSkuDetails getSkuDetails = billingService.getSkuDetails(purchaseType, productIds);
            observer.onNext(getSkuDetails);
            observer.onCompleted();
        } catch (RemoteException e) {
            observer.onError(e);
        }
    }
}
