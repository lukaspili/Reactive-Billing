package com.github.lukaspili.reactivebilling.sample.inventory;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.github.lukaspili.reactivebilling.model.SkuDetails;
import com.github.lukaspili.reactivebilling.sample.shop.ShopRowView;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<SkuDetails> items = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ShopRowView rowView;

        public ViewHolder(ShopRowView rowView) {
            super(rowView);
            this.rowView = rowView;
        }
    }

    public void bind(List<SkuDetails> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public InventoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new ShopRowView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.rowView.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}