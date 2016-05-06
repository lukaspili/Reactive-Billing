package com.github.lukaspili.reactivebilling.sample;

/**
 * Created by lukasz on 06/05/16.
 */
public class Utils {

    public static String getMessage(int responseCode) {
        String reason;
        switch (responseCode) {
            case -1:
                reason = "CANCELED PURCHASE FLOW";
                break;
            case 1:
                reason = "BILLING_RESPONSE_RESULT_USER_CANCELED";
                break;
            case 2:
                reason = "BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE";
                break;
            case 3:
                reason = "BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE";
                break;
            case 4:
                reason = "BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE";
                break;
            case 5:
                reason = "BILLING_RESPONSE_RESULT_DEVELOPER_ERROR";
                break;
            case 6:
                reason = "BILLING_RESPONSE_RESULT_ERROR";
                break;
            case 7:
                reason = "BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED";
                break;
            case 8:
                reason = "BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED";
                break;
            default:
                throw new IllegalStateException(String.format("Unknown response code: %d", responseCode));
        }

        return "Reason = " + reason;
    }
}
