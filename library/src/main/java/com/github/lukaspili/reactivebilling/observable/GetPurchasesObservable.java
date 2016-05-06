package com.github.lukaspili.reactivebilling.observable;

import android.content.Context;
import android.os.RemoteException;

import com.github.lukaspili.reactivebilling.BillingService;
import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.response.GetPurchases;

import rx.Observable;
import rx.Observer;

public class GetPurchasesObservable extends BaseObservable<GetPurchases> {

    public static Observable<GetPurchases> create(Context context, PurchaseType purchaseType, String continuationToken) {
        return Observable.create(new GetPurchasesObservable(context, purchaseType, continuationToken));
    }

    private PurchaseType purchaseType;
    private String continuationToken;

    protected GetPurchasesObservable(Context context, PurchaseType purchaseType, String continuationToken) {
        super(context);
        this.purchaseType = purchaseType;
        this.continuationToken = continuationToken;
    }

    @Override
    protected void onBillingServiceReady(BillingService billingService, Observer<? super GetPurchases> observer) {
        try {
            observer.onNext(billingService.getPurchases(purchaseType, continuationToken));
            observer.onCompleted();
        } catch (RemoteException e) {
            observer.onError(e);
        }
    }

//    @Override
//    protected void onBillingServiceReady(IInAppBillingService billingService, Observer<? super IInAppBillingService> observer) {
//        ReactiveBillingLogger.log("Billing service observable - service ready");
//        observer.onNext(billingService);
//        observer.onCompleted();
//    }
}
