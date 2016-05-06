package com.github.lukaspili.reactivebilling.response;

import com.github.lukaspili.reactivebilling.model.SkuDetails;

import java.util.List;

/**
 * Created by lukasz on 04/05/16.
 */
public class GetSkuDetails extends Response {


    private final List<SkuDetails> data;

    public GetSkuDetails(int responseCode, List<SkuDetails> data) {
        super(responseCode);
        this.data = data;
    }

    public List<SkuDetails> getData() {
        return data;
    }
}
