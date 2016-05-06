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

import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.observable.BillingServiceObservable;
import com.github.lukaspili.reactivebilling.observable.GetPurchasesObservable;
import com.github.lukaspili.reactivebilling.observable.GetSkuDetailsObservable;
import com.github.lukaspili.reactivebilling.observable.IsBillingSupportedObservable;
import com.github.lukaspili.reactivebilling.response.DidBuy;
import com.github.lukaspili.reactivebilling.response.GetPurchases;
import com.github.lukaspili.reactivebilling.response.GetSkuDetails;
import com.github.lukaspili.reactivebilling.response.IsBillingSupported;

import rx.Observable;

public class ReactiveBilling {

    private static ReactiveBilling instance;

    public static ReactiveBilling getInstance(Context context) {
        if (instance == null) {
            instance = new ReactiveBilling(context.getApplicationContext(), new PurchaseFlowService(context.getApplicationContext()));
        }
        return instance;
    }

    private final Context context;
    private final PurchaseFlowService purchaseFlowService;

    public ReactiveBilling(Context context, PurchaseFlowService purchaseFlowService) {
        this.context = context;
        this.purchaseFlowService = purchaseFlowService;
    }

    public Observable<BillingService> getBillingService() {
        return BillingServiceObservable.create(context);
    }

    public Observable<IsBillingSupported> isBillingSupported(PurchaseType purchaseType) {
        return IsBillingSupportedObservable.create(context, purchaseType);
    }

    public Observable<GetSkuDetails> getSkuDetails(PurchaseType purchaseType, String... productIds) {
        return GetSkuDetailsObservable.create(context, purchaseType, productIds);
    }

    public Observable<GetPurchases> getPurchases(PurchaseType purchaseType, String continuationToken) {
        return GetPurchasesObservable.create(context, purchaseType, continuationToken);
    }

    public Observable<DidBuy> purchaseFlow() {
        return purchaseFlowService.flow();
    }

    public void buy(String productId, PurchaseType purchaseType, String developerPayload) {
        purchaseFlowService.requestFlow(productId, purchaseType, developerPayload);
    }



    PurchaseFlowService getPurchaseFlowService() {
        return purchaseFlowService;
    }


//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    // Contains all the current permission requests.
//    // Once granted or denied, they are removed from it.
//    private Map<String, PublishSubject<Permission>> mSubjects = new HashMap<>();
//    private boolean mLogging;
//
//    ReactiveBilling(Context ctx) {
//        context = ctx;
//    }
//
//    public void setLogging(boolean logging) {
//        mLogging = logging;
//    }
//
//    private void log(String message) {
//        if (mLogging) {
//            Log.d(TAG, message);
//        }
//    }
//
//    /**
//     * Map emitted items from the source observable into {@code true} if permissions in parameters
//     * are granted, or {@code false} if not.
//     * <p>
//     * If one or several permissions have never been requested, invoke the related framework method
//     * to ask the user if he allows the permissions.
//     */
//    public Observable.Transformer<Object, Boolean> ensure(final String... permissions) {
//        return new Observable.Transformer<Object, Boolean>() {
//            @Override
//            public Observable<Boolean> call(Observable<Object> o) {
//                return request(o, permissions)
//                        // Transform Observable<Permission> to Observable<Boolean>
//                        .buffer(permissions.length)
//                        .flatMap(new Func1<List<Permission>, Observable<Boolean>>() {
//                            @Override
//                            public Observable<Boolean> call(List<Permission> permissions) {
//                                if (permissions.isEmpty()) {
//                                    // Occurs during orientation change, when the subject receives onComplete.
//                                    // In that case we don't want to propagate that empty list to the
//                                    // subscriber, only the onComplete.
//                                    return Observable.empty();
//                                }
//                                // Return true if all permissions are granted.
//                                for (Permission p : permissions) {
//                                    if (!p.granted) {
//                                        return Observable.just(false);
//                                    }
//                                }
//                                return Observable.just(true);
//                            }
//                        });
//            }
//        };
//    }
//
//    /**
//     * Map emitted items from the source observable into {@link Permission} objects for each
//     * permissions in parameters.
//     * <p>
//     * If one or several permissions have never been requested, invoke the related framework method
//     * to ask the user if he allows the permissions.
//     */
//    public Observable.Transformer<Object, Permission> ensureEach(final String... permissions) {
//        return new Observable.Transformer<Object, Permission>() {
//            @Override
//            public Observable<Permission> call(Observable<Object> o) {
//                return request(o, permissions);
//            }
//        };
//    }
//
//    /**
//     * Request permissions immediately, <b>must be invoked during initialization phase
//     * of your application</b>.
//     */
//    public Observable<Boolean> request(final String... permissions) {
//        return Observable.just(null).compose(ensure(permissions));
//    }
//
//    /**
//     * Request permissions immediately, <b>must be invoked during initialization phase
//     * of your application</b>.
//     */
//    public Observable<Permission> requestEach(final String... permissions) {
//        return Observable.just(null).compose(ensureEach(permissions));
//    }
//
//    private Observable<Permission> request(final Observable<?> trigger,
//                                           final String... permissions) {
//        if (permissions == null || permissions.length == 0) {
//            throw new IllegalArgumentException("RxPermissions.request/requestEach requires at least one input permission");
//        }
//        return oneOf(trigger, pending(permissions))
//                .flatMap(new Func1<Object, Observable<Permission>>() {
//                    @Override
//                    public Observable<Permission> call(Object o) {
//                        return request_(permissions);
//                    }
//                });
//    }
//
//    private Observable<?> pending(final String... permissions) {
//        for (String p : permissions) {
//            if (!mSubjects.containsKey(p)) {
//                return Observable.empty();
//            }
//        }
//        return Observable.just(null);
//    }
//
//    private Observable<?> oneOf(Observable<?> trigger, Observable<?> pending) {
//        if (trigger == null) {
//            return Observable.just(null);
//        }
//        return Observable.merge(trigger, pending);
//    }
//
//    @TargetApi(Build.VERSION_CODES.M)
//    private Observable<Permission> request_(final String... permissions) {
//
//        List<Observable<Permission>> list = new ArrayList<>(permissions.length);
//        List<String> unrequestedPermissions = new ArrayList<>();
//
//        // In case of multiple permissions, we create a observable for each of them.
//        // At the end, the observables are combined to have a unique response.
//        for (String permission : permissions) {
//            log("Requesting permission " + permission);
//            if (isGranted(permission)) {
//                // Already granted, or not Android M
//                // Return a granted Permission object.
//                list.add(Observable.just(new Permission(permission, true)));
//                continue;
//            }
//
//            if (isRevoked(permission)) {
//                // Revoked by a policy, return a denied Permission object.
//                list.add(Observable.just(new Permission(permission, false)));
//                continue;
//            }
//
//            PublishSubject<Permission> subject = mSubjects.get(permission);
//            // Create a new subject if not exists
//            if (subject == null) {
//                unrequestedPermissions.add(permission);
//                subject = PublishSubject.create();
//                mSubjects.put(permission, subject);
//            }
//
//            list.add(subject);
//        }
//
//        if (!unrequestedPermissions.isEmpty()) {
//            startShadowActivity(unrequestedPermissions
//                    .toArray(new String[unrequestedPermissions.size()]));
//        }
//        return Observable.concat(Observable.from(list));
//    }


//    void startShadowActivity(String[] permissions) {
////        log("startShadowActivity " + TextUtils.join(", ", permissions));
//        Intent intent = new Intent(context, ReactiveBillingShadowActivity.class);
//        intent.putExtra("permissions", permissions);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//    }


}
