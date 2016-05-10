package com.github.lukaspili.reactivebilling.sample.shop;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.lukaspili.reactivebilling.ReactiveBilling;
import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.model.SkuDetails;
import com.github.lukaspili.reactivebilling.response.GetSkuDetailsResponse;
import com.github.lukaspili.reactivebilling.response.PurchaseResponse;
import com.github.lukaspili.reactivebilling.response.Response;
import com.github.lukaspili.reactivebilling.sample.R;
import com.github.lukaspili.reactivebilling.sample.TabsAdapter;
import com.github.lukaspili.reactivebilling.sample.Utils;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by lukasz on 06/05/16.
 */
public class ShopFragment extends Fragment implements TabsAdapter.Tab {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private ShopAdapter adapter = new ShopAdapter();
    private Subscription subscription;

    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        refreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment, container, false);
        recyclerView = (RecyclerView) refreshLayout.findViewById(R.id.recyclerview);

        return refreshLayout;
    }

    @Override
    public void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        adapter.bind(new ShopAdapter.DidClickItem() {
            @Override
            public void onClick(final SkuDetails skuDetails) {
                dialog = new AlertDialog.Builder(getContext())
                        .setTitle(skuDetails.getTitle())
                        .setMessage("Do you want to buy or buy and consume?")
                        .setPositiveButton("Buy only", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                purchaseSkuDetails(skuDetails, false);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Buy & Consume", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                purchaseSkuDetails(skuDetails, true);
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(true)
                        .show();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        Log.d(getClass().getName(), "Subscribe to purchase flow");
        subscription = ReactiveBilling.getInstance(getContext()).purchaseFlow()
                .flatMap(new Func1<PurchaseResponse, Observable<PurchaseResponse>>() {
                    @Override
                    public Observable<PurchaseResponse> call(final PurchaseResponse purchaseResponse) {
                        // shall we consume directly?
                        boolean consume = purchaseResponse.getExtras().getBoolean("consume");

                        if (purchaseResponse.isSuccess() && consume) {
                            Log.d(getClass().getName(), "Item bought, consume it directly");
                            return ReactiveBilling.getInstance(getContext()).consumePurchase(purchaseResponse.getPurchase().getPurchaseToken())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .map(new Func1<Response, PurchaseResponse>() {
                                        @Override
                                        public PurchaseResponse call(Response consumeResponse) {
                                            if (consumeResponse.isSuccess()) {
                                                return purchaseResponse;
                                            } else {
                                                // you would probably want to have something better in a real world app
                                                return new PurchaseResponse(consumeResponse.getResponseCode(), null, null, null, false);
                                            }
                                        }
                                    });
                        } else {
                            return Observable.just(purchaseResponse);
                        }
                    }
                })
                .subscribe(new Action1<PurchaseResponse>() {
                    @Override
                    public void call(PurchaseResponse purchaseResponse) {
                        if (getActivity() == null) return;
                        didPurchase(purchaseResponse);
                    }
                });

        load();
    }

    @Override
    public void onDestroyView() {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }

        super.onDestroyView();
    }

    // Get sku details

    private void load() {
        Log.d(getClass().getName(), "Load shop");

        ReactiveBilling.getInstance(getContext())
                .getSkuDetails(PurchaseType.PRODUCT, "coffee", "beer")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GetSkuDetailsResponse>() {
                    @Override
                    public void call(GetSkuDetailsResponse response) {
                        if (getActivity() == null) return;
                        refreshLayout.setRefreshing(false);
                        didSucceedGetSkuDetails(response);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (getActivity() == null) return;
                        refreshLayout.setRefreshing(false);
                        didFailGetSkuDetails();
                    }
                });
    }

    private void didSucceedGetSkuDetails(GetSkuDetailsResponse getSkuDetailsResponse) {
        if (getSkuDetailsResponse.isSuccess()) {
            adapter.bind(getSkuDetailsResponse.getList());
        } else {
            // error
            Log.d(getClass().getName(), "Error");
        }
    }

    private void didFailGetSkuDetails() {

    }


    // Purchase

    private void purchaseSkuDetails(SkuDetails skuDetails, final boolean consume) {
        Log.d(getClass().getName(), String.format("Purchase %s", skuDetails.getTitle()));

        final Bundle extras = new Bundle();
        extras.putBoolean("consume", consume);

        ReactiveBilling.getInstance(getContext())
                .startPurchase(skuDetails.getProductId(), skuDetails.getPurchaseType(), null, extras)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        if (getActivity() == null) return;

                        if (response.isSuccess()) {
                            // purchase flow was started successfully, nothing to do here
                        } else {
                            // handle cannot start purchase flow
                            didFailStartPurchase(response);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (getActivity() == null) return;
                        didFailStartPurchase(new Response(-1));
                    }
                });
    }

    private void didFailStartPurchase(Response response) {
        Log.d(getClass().getName(), "Did fail to start purchase");

        dialog = new AlertDialog.Builder(getContext())
                .setTitle("Cannot buy the item")
                .setMessage(Utils.getMessage(response.getResponseCode()))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }

    private void didPurchase(PurchaseResponse response) {
        Log.d(getClass().getName(), String.format("Did purchase, success: %b", response.isSuccess()));

        String title;
        String reason;
        if (response.isSuccess()) {
            title = "Item bought!";
            reason = "Congrats on buying a " + response.getPurchase().getProductId();
        } else if (response.isCancelled()) {
            title = "Purchase cancelled";
            reason = "Nothing was billed";
        } else {
            title = "Cannot buy the item";
            reason = Utils.getMessage(response.getResponseCode());
        }

        dialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(reason)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }

    @Override
    public void didFocus() {
        Log.d(getClass().getName(), "Shop did focus");
        load();
    }
}
