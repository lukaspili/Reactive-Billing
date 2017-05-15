package com.github.lukaspili.reactivebilling.parser;

import com.github.lukaspili.reactivebilling.ReactiveBilling;
import com.github.lukaspili.reactivebilling.model.Purchase;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lukasz on 06/05/16.
 */
public class PurchaseParser {

    public static Purchase parse(String value) {
        ReactiveBilling.log(null, "purchase json: %s", value);

        JSONObject json;
        try {
            json = new JSONObject(value);
        } catch (JSONException e) {
            ReactiveBilling.log(e, "Cannot parse purchase json");
            return null;
        }

        return new Purchase(
                json.optString("orderId"),
                json.optString("packageName"),
                json.optString("productId"),
                json.optString("developerPayload"),
                json.optString("purchaseToken"),
                PurchaseStateParser.parse(json.optInt("purchaseState")),
                json.optLong("purchaseTime"),
                json.optBoolean("autoRenewing")
        );
    }

    public static String toString(Purchase purchase) {
        JSONObject json = new JSONObject();

        try {
            json.putOpt("orderId", purchase.getOrderId());
            json.putOpt("packageName", purchase.getPackageName());
            json.putOpt("productId", purchase.getProductId());
            json.putOpt("developerPayload", purchase.getDeveloperPayload());
            json.putOpt("purchaseToken", purchase.getPurchaseToken());
            json.putOpt("purchaseState", purchase.getPurchaseState().getValue());
            json.putOpt("purchaseTime", purchase.getPurchaseTime());
            json.putOpt("autoRenewing", purchase.isAutoRenewing());
        } catch (JSONException e) {
            ReactiveBilling.log(e, "Cannot create json from the Purchase object");
            return null;
        }

        return json.toString();
    }
}
