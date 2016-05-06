package com.github.lukaspili.reactivebilling.sample.shop;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.github.lukaspili.reactivebilling.model.SkuDetails;

import java.util.ArrayList;
import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private DidClickItem didClick;
    private List<SkuDetails> items = new ArrayList<>();

    public void bind(DidClickItem didClick) {
        this.didClick = didClick;
    }

    public void bind(List<SkuDetails> items) {
        Log.d(getClass().getName(), String.format("Bind items: %d", items.size()));

        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public ShopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new ShopRowView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final SkuDetails skuDetails = items.get(position);

        holder.rowView.bind(skuDetails);
        holder.rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didClick.onClick(skuDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ShopRowView rowView;

        public ViewHolder(ShopRowView rowView) {
            super(rowView);
            this.rowView = rowView;
        }
    }

    interface DidClickItem {
        void onClick(SkuDetails skuDetails);
    }
}