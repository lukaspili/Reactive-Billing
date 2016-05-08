package com.github.lukaspili.reactivebilling.sample.inventory;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.lukaspili.reactivebilling.ReactiveBilling;
import com.github.lukaspili.reactivebilling.model.Purchase;
import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.response.GetPurchases;
import com.github.lukaspili.reactivebilling.response.Response;
import com.github.lukaspili.reactivebilling.sample.R;
import com.github.lukaspili.reactivebilling.sample.Utils;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by lukasz on 06/05/16.
 */
public class InventoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter = new InventoryAdapter();

    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_shop, container, false);
        return recyclerView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        adapter.bind(new InventoryAdapter.DidClickItem() {
            @Override
            public void onClick(final Purchase purchase) {
                dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Consume item")
                        .setMessage(String.format("Do you want to consume the %s?", purchase.getProductId()))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                consume(purchase);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
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

        load();
    }

    @Override
    public void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        super.onDestroy();
    }

    private void load() {
        Log.d(getClass().getName(), "Load inventory");

        ReactiveBilling.getInstance(getContext())
                .getPurchases(PurchaseType.PRODUCT, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GetPurchases>() {
                    @Override
                    public void call(GetPurchases getPurchases) {
                        didSucceedGetPurchases(getPurchases);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        didFailGetPurchases();
                    }
                });
    }

    private void didSucceedGetPurchases(GetPurchases getPurchases) {
        if (getPurchases.isSuccess()) {
            Observable.from(getPurchases.getItems())
                    .map(new Func1<GetPurchases.Item, Purchase>() {
                        @Override
                        public Purchase call(GetPurchases.Item item) {
                            return item.getPurchase();
                        }
                    })
                    .toList()
                    .subscribe(new Action1<List<Purchase>>() {
                        @Override
                        public void call(List<Purchase> purchases) {
                            adapter.bind(purchases);
                        }
                    });
        } else {
            // error
            Log.d(getClass().getName(), "error");
        }
    }

    private void didFailGetPurchases() {

    }


    // Consume

    private void consume(Purchase purchase) {
        ReactiveBilling.getInstance(getContext())
                .consumePurchase(purchase.getPurchaseToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        didSucceedConsumePurchase(response);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        didFailConsumePurchase();
                    }
                });
    }

    private void didSucceedConsumePurchase(Response response) {
        // reload the list once the product is consumed
        load();

        String title;
        String message;
        if (response.isSuccess()) {
            title = "Product consumed";
            message = "Hope you enjoyed it";
        } else {
            title = "Failed to consume";
            message = Utils.getMessage(response.getResponseCode());
        }

        dialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }

    private void didFailConsumePurchase() {

    }
}
