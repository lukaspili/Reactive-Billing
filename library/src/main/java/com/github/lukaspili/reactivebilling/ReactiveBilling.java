/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.lukaspili.reactivebilling;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.observable.BillingServiceObservable;
import com.github.lukaspili.reactivebilling.observable.ConsumePurchaseObservable;
import com.github.lukaspili.reactivebilling.observable.GetBuyIntentObservable;
import com.github.lukaspili.reactivebilling.observable.GetPurchasesObservable;
import com.github.lukaspili.reactivebilling.observable.GetSkuDetailsObservable;
import com.github.lukaspili.reactivebilling.observable.IsBillingSupportedObservable;
import com.github.lukaspili.reactivebilling.response.PurchaseResponse;
import com.github.lukaspili.reactivebilling.response.GetPurchasesResponse;
import com.github.lukaspili.reactivebilling.response.GetSkuDetailsResponse;
import com.github.lukaspili.reactivebilling.response.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;

public class ReactiveBilling {

    private static ReactiveBilling instance;
    private static ReactiveBillingLogger logger;

    public static ReactiveBilling getInstance(Context context) {
        if (instance == null) {
            instance = new ReactiveBilling(context.getApplicationContext(), new PurchaseFlowService(context.getApplicationContext()));

            // logger is disabled by default
            if (logger == null) {
                initLogger(false);
            }
        }
        return instance;
    }


    public static void initLogger(boolean isEnabled) {
        if (logger != null) {
            throw new IllegalStateException("Logger instance is already set");
        }

        logger = new ReactiveBillingLogger(isEnabled);
    }

    static ReactiveBillingLogger getLogger() {
        return logger;
    }

    private final Context context;
    private final PurchaseFlowService purchaseFlowService;

    ReactiveBilling(Context context, PurchaseFlowService purchaseFlowService) {
        this.context = context;
        this.purchaseFlowService = purchaseFlowService;
    }

    @NonNull public Observable<BillingService> getBillingService() {
        return BillingServiceObservable.create(context);
    }

    @NonNull public Observable<Response> isBillingSupported(@NonNull PurchaseType purchaseType) {
        return IsBillingSupportedObservable.create(context, purchaseType);
    }

    @NonNull public Observable<Response> consumePurchase(@NonNull String purchaseToken) {
        return ConsumePurchaseObservable.create(context, purchaseToken);
    }

    @NonNull public Observable<GetSkuDetailsResponse> getSkuDetails(
        @NonNull PurchaseType purchaseType, @NonNull String productId, String... moreProductIds) {
        List<String> productIds = new ArrayList<>();
        productIds.add(productId);
        if (moreProductIds != null) {
            productIds.addAll(Arrays.asList(moreProductIds));
        }
        return getSkuDetails(purchaseType, productIds);
    }

    @NonNull public Observable<GetSkuDetailsResponse> getSkuDetails(
        @NonNull PurchaseType purchaseType, @NonNull List<String> productIds) {
        return GetSkuDetailsObservable.create(context, purchaseType, productIds);
    }

    @NonNull public Observable<GetPurchasesResponse> getPurchases(
        @NonNull PurchaseType purchaseType) {
        return getPurchases(purchaseType, null);
    }

    @NonNull public Observable<GetPurchasesResponse> getPurchases(
        @NonNull PurchaseType purchaseType, @Nullable String continuationToken) {
        return GetPurchasesObservable.create(context, purchaseType, continuationToken);
    }

    @NonNull public Observable<Response> startPurchase(@NonNull String productId,
        @NonNull PurchaseType purchaseType, @Nullable String developerPayload,
        @Nullable Bundle extras) {
        return GetBuyIntentObservable.create(context, purchaseFlowService, productId, purchaseType,
            developerPayload, extras);
    }

    public Observable<PurchaseResponse> purchaseFlow() {
        return purchaseFlowService.getObservable();
    }

    PurchaseFlowService getPurchaseFlowService() {
        return purchaseFlowService;
    }
}
