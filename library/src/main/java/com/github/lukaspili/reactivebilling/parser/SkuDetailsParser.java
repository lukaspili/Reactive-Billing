package com.github.lukaspili.reactivebilling.parser;

import com.github.lukaspili.reactivebilling.model.SkuDetails;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lukasz on 06/05/16.
 */
public class SkuDetailsParser {

    public static SkuDetails parse(String value) throws JSONException {
        JSONObject json = new JSONObject(value);

        return new SkuDetails(
                json.optString("productId"),
                json.optLong("price_amount_micros"),
                PurchaseTypeParser.parse(json.optString("type")),
                json.optString("price"),
                json.optString("price_currency_code"),
                json.optString("title"),
                json.optString("description")
        );
    }
}
