package com.github.lukaspili.reactivebilling;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * Created by lukasz on 04/05/16.
 */
public class ReactiveBillingLogger {

    public static void log(String message, Object... args) {
        ReactiveBilling.getLogger().log_(null, message, args);
    }

    public static void log(Throwable t, String message, Object... args) {
        ReactiveBilling.getLogger().log_(t, message, args);
    }

    private static final String TAG = "io.univelo.ReactiveBilling";

    private final boolean isEnabled;

    public ReactiveBillingLogger(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @SuppressLint("LongLogTag")
    private void log_(Throwable t, String message, Object... args) {
        //noinspection PointlessBooleanExpression
        if (!isEnabled) {
            return;
        }

        if (args != null && args.length > 0) {
            message = String.format(message, args);
        }

        message = String.format("ReactiveBilling - %s", message);

        if (t != null) {
            Log.d(TAG, message, t);
        } else {
            Log.d(TAG, message);
        }
    }
}
