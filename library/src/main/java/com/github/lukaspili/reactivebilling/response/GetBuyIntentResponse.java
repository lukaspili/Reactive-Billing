package com.github.lukaspili.reactivebilling.response;

import android.app.PendingIntent;

/**
 * Created by lukasz on 06/05/16.
 */
public class GetBuyIntentResponse extends Response {

    private final PendingIntent intent;

    public GetBuyIntentResponse(int responseCode, PendingIntent intent) {
        super(responseCode);
        this.intent = intent;
    }

    public PendingIntent getIntent() {
        return intent;
    }
}
