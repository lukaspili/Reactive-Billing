package com.github.lukaspili.reactivebilling.observable;

import android.content.Context;
import android.os.RemoteException;

import android.support.annotation.NonNull;
import com.github.lukaspili.reactivebilling.BillingService;
import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.response.Response;

import rx.Observable;
import rx.Observer;

public class IsBillingSupportedObservable extends BaseObservable<Response> {

    @NonNull public static Observable<Response> create(@NonNull Context context, @NonNull PurchaseType purchaseType) {
        return Observable.create(new IsBillingSupportedObservable(context, purchaseType));
    }

    private final PurchaseType purchaseType;

    private IsBillingSupportedObservable(Context context, PurchaseType purchaseType) {
        super(context);
        this.purchaseType = purchaseType;
    }

    @Override
    protected void onBillingServiceReady(BillingService billingService, Observer<? super Response> observer) {
        try {
            observer.onNext(billingService.isBillingSupported(purchaseType));
            observer.onCompleted();
        } catch (RemoteException e) {
            observer.onError(e);
        }
    }
}
