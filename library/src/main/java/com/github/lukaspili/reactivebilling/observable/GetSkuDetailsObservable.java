package com.github.lukaspili.reactivebilling.observable;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.github.lukaspili.reactivebilling.BillingService;
import com.github.lukaspili.reactivebilling.ReactiveBilling;
import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.response.GetSkuDetailsResponse;

import org.json.JSONException;

import java.util.List;
import rx.Observable;
import rx.Observer;

public class GetSkuDetailsObservable extends BaseObservable<GetSkuDetailsResponse> {

    @NonNull public static Observable<GetSkuDetailsResponse> create(@NonNull Context context,
        @NonNull PurchaseType purchaseType, @NonNull List<String> productIds) {
        return Observable.create(new GetSkuDetailsObservable(context, purchaseType, productIds));
    }

    private PurchaseType purchaseType;
    private List<String> productIds;

    protected GetSkuDetailsObservable(Context context, PurchaseType purchaseType, List<String> productIds) {
        super(context);
        this.purchaseType = purchaseType;
        this.productIds = productIds;
    }

    @Override
    protected void onBillingServiceReady(BillingService billingService, Observer<? super GetSkuDetailsResponse> observer) {
        try {
            observer.onNext(billingService.getSkuDetails(purchaseType, productIds));
            observer.onCompleted();
        } catch (JSONException e) {
            ReactiveBilling.log(e, "Cannot parse purchase json");
            observer.onError(e);
        } catch (RemoteException e) {
            ReactiveBilling.log(e, "Remote exception");
            observer.onError(e);
        }
    }
}
