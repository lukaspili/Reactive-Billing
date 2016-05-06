package com.github.lukaspili.reactivebilling.sample.inventory;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.github.lukaspili.reactivebilling.model.Purchase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private DidClickItem didClick;
    private List<Purchase> items = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mma");

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public InventoryRowView rowView;

        public ViewHolder(InventoryRowView rowView) {
            super(rowView);
            this.rowView = rowView;
        }
    }

    public void bind(DidClickItem didClick) {
        this.didClick = didClick;
    }

    public void bind(List<Purchase> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public InventoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new InventoryRowView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Purchase purchase = items.get(position);
        holder.rowView.bind(purchase, dateFormat);
        holder.rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didClick.onClick(purchase);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    interface DidClickItem {
        void onClick(Purchase purchase);
    }
}