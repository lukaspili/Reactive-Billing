package com.github.lukaspili.reactivebilling.observable;

import android.content.Context;

import com.github.lukaspili.reactivebilling.BillingService;

import rx.Observable;
import rx.Observer;

public class BillingServiceObservable extends BaseObservable<BillingService> {

    public static Observable<BillingService> create(Context context) {
        return Observable.create(new BillingServiceObservable(context));
    }

    private BillingServiceObservable(Context context) {
        super(context);
    }

    @Override
    protected void onBillingServiceReady(BillingService billingService, Observer<? super BillingService> observer) {
        observer.onNext(billingService);
        observer.onCompleted();
    }
}
