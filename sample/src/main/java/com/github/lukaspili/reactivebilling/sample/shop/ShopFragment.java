package com.github.lukaspili.reactivebilling.sample.shop;

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
import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.model.SkuDetails;
import com.github.lukaspili.reactivebilling.response.DidBuy;
import com.github.lukaspili.reactivebilling.response.GetSkuDetails;
import com.github.lukaspili.reactivebilling.sample.R;
import com.github.lukaspili.reactivebilling.sample.Utils;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lukasz on 06/05/16.
 */
public class ShopFragment extends Fragment {

    private RecyclerView recyclerView;
    private ShopAdapter adapter = new ShopAdapter();
    private Subscription subscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_shop, container, false);
        return recyclerView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                load();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        adapter.bind(new ShopAdapter.DidClickItem() {
            @Override
            public void onClick(SkuDetails skuDetails) {
                purchaseSkuDetails(skuDetails);
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        Log.d(getClass().getName(), "Subscribe to purchase flow");
        subscription = ReactiveBilling.getInstance(getContext()).purchaseFlow()
                .subscribe(new Action1<DidBuy>() {
                    @Override
                    public void call(DidBuy didBuy) {
                        didBuy(didBuy);
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
                .subscribe(new Action1<GetSkuDetails>() {
                    @Override
                    public void call(GetSkuDetails getSkuDetails) {
                        didSucceedGetSkuDetails(getSkuDetails);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        didFailGetSkuDetails();
                    }
                });
    }

    private void didSucceedGetSkuDetails(GetSkuDetails getSkuDetails) {
        if (getSkuDetails.isSuccess()) {
            adapter.bind(getSkuDetails.getData());
        } else {
            // error
            Log.d(getClass().getName(), "Error");
        }
    }

    private void didFailGetSkuDetails() {

    }


    // Purchase

    private void purchaseSkuDetails(SkuDetails skuDetails) {
        Log.d(getClass().getName(), String.format("Purchase %s", skuDetails.getTitle()));

        ReactiveBilling.getInstance(getContext())
                .buy(skuDetails.getProductId(), skuDetails.getPurchaseType(), null);
    }

    private void didBuy(DidBuy didBuy) {
        Log.d(getClass().getName(), String.format("Did buy, success: %b", didBuy.isSuccess()));

        String title;
        String reason;
        if (didBuy.isSuccess()) {
            title = "Item bought!";
            reason = "Congrats on buying a " + didBuy.getPurchase().getProductId();
        } else {
            title = "Cannot buy the item";
            reason = Utils.getMessage(didBuy.getResponseCode());
        }

        new AlertDialog.Builder(getContext())
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
}
