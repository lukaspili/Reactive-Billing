package com.github.lukaspili.reactivebilling;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.github.lukaspili.reactivebilling.model.Purchase;
import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.observable.GetBuyIntentObservable;
import com.github.lukaspili.reactivebilling.parser.PurchaseParser;
import com.github.lukaspili.reactivebilling.response.DidBuy;
import com.github.lukaspili.reactivebilling.response.GetBuyIntent;
import com.jakewharton.rxrelay.PublishRelay;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lukasz on 06/05/16.
 */
public class PurchaseFlowService {

    private final Context context;
    private final PublishRelay<DidBuy> subject = PublishRelay.create();
    private final Observable<DidBuy> observable = subject.doOnSubscribe(new Action0() {
        @Override
        public void call() {
            if (hasSubscription) {
                throw new IllegalStateException("Already has subscription");
            }

            ReactiveBillingLogger.log("Purchase flow - subscribe");
            hasSubscription = true;
        }
    }).doOnUnsubscribe(new Action0() {
        @Override
        public void call() {
            if (!hasSubscription) {
                throw new IllegalStateException("Doesn't have any subscription");
            }

            ReactiveBillingLogger.log("Purchase flow - unsubscribe");
            hasSubscription = false;
        }
    });

    private boolean hasSubscription;

    public PurchaseFlowService(Context context) {
        this.context = context;
    }

    Observable<DidBuy> flow() {
        return observable;
    }

    void requestFlow(String productId, PurchaseType purchaseType, String developerPayload) {
        if (!hasSubscription) {
            throw new IllegalStateException("Cannot request flow without subscription");
        }

        GetBuyIntentObservable.create(context, productId, purchaseType, developerPayload)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GetBuyIntent>() {
                    @Override
                    public void call(GetBuyIntent getBuyIntent) {
                        ReactiveBillingLogger.log("Request flow - on next");

                        // not sure the subject can be null
                        // possible cause: request buy intent, then configuration changes happens before buy intent comes back
                        if (!hasSubscription) {
                            ReactiveBillingLogger.log("Request flow - on next but subject null, abort");
                            return;
                        }

                        if (getBuyIntent.isSuccess()) {
                            startFlow(getBuyIntent.getIntent());
                        } else {
                            subject.call(new DidBuy(getBuyIntent.getResponseCode(), null, null, null));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ReactiveBillingLogger.log("Request flow - on error");

                        // not sure the subject can be null
                        // possible cause: request buy intent, then configuration changes happens before buy intent comes back
                        if (!hasSubscription) {
                            ReactiveBillingLogger.log("Request flow - on error but subject null, abort");
                            return;
                        }

                        subject.call(DidBuy.error(throwable));
                    }
                });
    }

    void startFlow(PendingIntent buyIntent) {
        if (!hasSubscription) {
            throw new IllegalStateException("Cannot start flow without subscribers");
        }

        Intent intent = new Intent(context, ReactiveBillingShadowActivity.class);
        intent.putExtra("BUY_INTENT", buyIntent);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    void onActivityResult(int resultCode, Intent data) {
        if (!hasSubscription) {
            throw new IllegalStateException("Subject cannot be null when receiving purchase result");
        }

        if (resultCode == Activity.RESULT_OK) {
            ReactiveBillingLogger.log("Did buy result - OK");

            int response = data.getIntExtra("RESPONSE_CODE", -1);
            ReactiveBillingLogger.log("Did buy result - response: %d", response);

            if (response != 0) {
                Purchase purchase = PurchaseParser.parse(data.getStringExtra("INAPP_PURCHASE_DATA"));
                String signature = data.getStringExtra("INAPP_DATA_SIGNATURE");
                subject.call(new DidBuy(response, purchase, signature, null));
            } else {
                subject.call(new DidBuy(response, null, null, null));
            }
        } else {
            ReactiveBillingLogger.log("Did buy result - CANCELED");
            subject.call(new DidBuy(-1, null, null, null));
        }
    }
}
