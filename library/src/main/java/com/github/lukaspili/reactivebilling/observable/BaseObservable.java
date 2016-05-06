package com.github.lukaspili.reactivebilling.observable;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;

import com.android.vending.billing.IInAppBillingService;
import com.github.lukaspili.reactivebilling.BillingService;
import com.github.lukaspili.reactivebilling.ReactiveBillingLogger;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;


public abstract class BaseObservable<T> implements Observable.OnSubscribe<T> {

    protected final Context context;

    BaseObservable(Context context) {
        this.context = context;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        ReactiveBillingLogger.log("Base observable call on thread = " + Thread.currentThread().getName());
        ReactiveBillingLogger.log("Current thread looper = " + Looper.myLooper());

        final Connection connection = new Connection(subscriber);

        try {
            ReactiveBillingLogger.log("Bind service");
            Intent iapIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
            iapIntent.setPackage("com.android.vending");
            context.bindService(iapIntent, connection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            ReactiveBillingLogger.log(e, "Bind service error");
            subscriber.onError(e);
        }

        subscriber.add(Subscriptions.create(new Action0() {
            @Override
            public void call() {
                ReactiveBillingLogger.log("Base observable unsubscribe and unbind on thread: " + Thread.currentThread().getName());
                context.unbindService(connection);
            }
        }));
    }

    protected abstract void onBillingServiceReady(BillingService billingService, Observer<? super T> observer);

//    protected abstract void onBillingServiceReady(IInAppBillingService billingService, Observer<? super T> observer);

    private class Connection implements ServiceConnection {

        final private Observer<? super T> observer;

        private Connection(Observer<? super T> observer) {
            this.observer = observer;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReactiveBillingLogger.log("Service connection connected on thread = " + Thread.currentThread().getName());

            IInAppBillingService billingService = IInAppBillingService.Stub.asInterface(service);
            BillingService reactiveBillingService = new BillingService(context, billingService);
            onBillingServiceReady(reactiveBillingService, observer);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ReactiveBillingLogger.log("Service connection disconnected on thread = " + Thread.currentThread().getName());
        }
    }
}
