package com.github.lukaspili.reactivebilling.response;

/**
 * Created by lukasz on 06/05/16.
 */
public abstract class Response {

    protected final int responseCode;

    public Response(int responseCode) {
        this.responseCode = responseCode;
    }

    public boolean isSuccess() {
        return responseCode == 0;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
