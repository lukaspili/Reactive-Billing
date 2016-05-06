package com.github.lukaspili.reactivebilling;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * Created by lukasz on 04/05/16.
 */
public class ReactiveBillingLogger {

    private static final String TAG = "io.univelo.ReactiveBilling";
    private static final boolean ENABLED = true;

    public static void log(String message, Object... args) {
        log(null, message, args);
    }

    @SuppressLint("LongLogTag")
    public static void log(Throwable t, String message, Object... args) {
        //noinspection PointlessBooleanExpression
        if (!ENABLED) {
            return;
        }

        if (args != null && args.length > 0) {
            message = String.format(message, args);
        }

        if (t != null) {
            Log.d(TAG, message, t);
        } else {
            Log.d(TAG, message);
        }
    }
}
