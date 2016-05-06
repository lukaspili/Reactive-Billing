package com.github.lukaspili.reactivebilling.response;

/**
 * Created by lukasz on 04/05/16.
 */
public class IsBillingSupported extends Response {

    private final boolean isSupported;

    public IsBillingSupported(int responseCode, boolean isSupported) {
        super(responseCode);
        this.isSupported = isSupported;
    }

    public boolean isSupported() {
        return isSupported;
    }
}
