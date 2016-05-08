package com.github.lukaspili.reactivebilling.observable;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.android.vending.billing.IInAppBillingService;
import com.github.lukaspili.reactivebilling.BillingService;
import com.github.lukaspili.reactivebilling.ReactiveBillingLogger;

import java.util.concurrent.Semaphore;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;


public abstract class BaseObservable<T> implements Observable.OnSubscribe<T> {

    protected final Context context;
    private final Semaphore semaphore = new Semaphore(0);

    private BillingService billingService;

    BaseObservable(Context context) {
        this.context = context;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        final Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");

        final Connection connection = new Connection();

        ReactiveBillingLogger.log("Bind service (thread %s)", Thread.currentThread().getName());
        try {
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        } catch (SecurityException e) {
            ReactiveBillingLogger.log(e, "Bind service error");
            subscriber.onError(e);
        }

        subscriber.add(Subscriptions.create(new Action0() {
            @Override
            public void call() {
                ReactiveBillingLogger.log("Unbind service (thread %s)", Thread.currentThread().getName());
                context.unbindService(connection);
            }
        }));

        // freeze the current RX thread until service is connected
        // because bindService() will call the connection callback on the main thread
        // we want to get back on the current RX thread
        ReactiveBillingLogger.log("Acquire semaphore until service is ready");
        semaphore.acquireUninterruptibly();

        // once the semaphore is released
        // it means that the service is connected and available
        //TODO: what happens if the service is never connected?
        ReactiveBillingLogger.log("Billing service ready (thread %s)", Thread.currentThread().getName());
        onBillingServiceReady(billingService, subscriber);
    }

    protected abstract void onBillingServiceReady(BillingService billingService, Observer<? super T> observer);

    private class Connection implements ServiceConnection {

        /**
         * For some reason, that method is always called on the main thread
         * Regardless of the originating thread executing bindService()
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReactiveBillingLogger.log("Service connected (thread %s)", Thread.currentThread().getName());

            IInAppBillingService inAppBillingService = IInAppBillingService.Stub.asInterface(service);
            billingService = new BillingService(context, inAppBillingService);

            // once the service is available, release the semaphore
            // that is blocking the originating thread
            semaphore.release();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ReactiveBillingLogger.log("Service disconnected (thread %s)", Thread.currentThread().getName());
            billingService = null;
        }
    }
}
