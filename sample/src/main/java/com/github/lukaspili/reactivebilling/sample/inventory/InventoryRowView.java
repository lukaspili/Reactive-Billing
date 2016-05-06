package com.github.lukaspili.reactivebilling.sample.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lukaspili.reactivebilling.model.Purchase;
import com.github.lukaspili.reactivebilling.sample.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lukasz on 06/05/16.
 */
public class InventoryRowView extends LinearLayout {

    private TextView titleTextView;
    private TextView descriptionTextView;

    public InventoryRowView(Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.row_inventory, this);
        titleTextView = (TextView) view.findViewById(R.id.title);
        descriptionTextView = (TextView) view.findViewById(R.id.description);
    }

    public void bind(Purchase purchase, SimpleDateFormat dateFormat) {
        titleTextView.setText(purchase.getProductId());

        String date = "Bought on: " + dateFormat.format(new Date(purchase.getPurchaseTime()));
        String state = "State: " + purchase.getPurchaseState().toString();

        descriptionTextView.setText(String.format("%s\n%s", date, state));
    }
}
