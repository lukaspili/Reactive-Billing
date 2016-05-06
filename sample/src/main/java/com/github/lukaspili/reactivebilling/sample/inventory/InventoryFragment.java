package com.github.lukaspili.reactivebilling.sample.inventory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.ReactiveBilling;
import com.github.lukaspili.reactivebilling.response.GetSkuDetails;
import com.github.lukaspili.reactivebilling.sample.R;
import com.github.lukaspili.reactivebilling.sample.shop.ShopAdapter;

import rx.functions.Action1;

/**
 * Created by lukasz on 06/05/16.
 */
public class InventoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private ShopAdapter adapter = new ShopAdapter();

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

//        setHasOptionsMenu(true);
//
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(adapter);
//
//        load();
    }

    private void load() {
        Log.d(getClass().getName(), "Load sku details");

        ReactiveBilling.getInstance(getContext())
                .getSkuDetails(PurchaseType.PRODUCT, "coffee", "beer")
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
            Log.d(getClass().getName(), "error");
        }
    }

    private void didFailGetSkuDetails() {

    }
}
