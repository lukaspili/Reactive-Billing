package com.github.lukaspili.reactivebilling.sample;

import android.app.Application;

import com.github.lukaspili.reactivebilling.ReactiveBilling;

/**
 * Created by lukasz on 08/05/16.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ReactiveBilling.initLogger(true);
    }
}
