package com.github.lukaspili.reactivebilling.observable;

import android.content.Context;
import android.os.RemoteException;

import com.github.lukaspili.reactivebilling.BillingService;
import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.response.IsBillingSupported;

import rx.Observable;
import rx.Observer;

public class IsBillingSupportedObservable extends BaseObservable<IsBillingSupported> {

    public static Observable<IsBillingSupported> create(Context context, PurchaseType purchaseType) {
        return Observable.create(new IsBillingSupportedObservable(context, purchaseType));
    }

    private final PurchaseType purchaseType;

    private IsBillingSupportedObservable(Context context, PurchaseType purchaseType) {
        super(context);
        this.purchaseType = purchaseType;
    }

    @Override
    protected void onBillingServiceReady(BillingService billingService, Observer<? super IsBillingSupported> observer) {
        try {
            observer.onNext(billingService.isBillingSupported(purchaseType));
            observer.onCompleted();
        } catch (RemoteException e) {
            observer.onError(e);
        }
    }
}
