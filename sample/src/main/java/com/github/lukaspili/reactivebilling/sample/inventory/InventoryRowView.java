package com.github.lukaspili.reactivebilling.sample.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lukaspili.reactivebilling.model.SkuDetails;
import com.github.lukaspili.reactivebilling.sample.R;

/**
 * Created by lukasz on 06/05/16.
 */
public class InventoryRowView extends LinearLayout {

    private TextView titleTextView;

    public InventoryRowView(Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.row_shop, this);
        titleTextView = (TextView) view.findViewById(R.id.title);
    }

    public void bind(SkuDetails skuDetails) {
        titleTextView.setText(skuDetails.getTitle());
    }
}
