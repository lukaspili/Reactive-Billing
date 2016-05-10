package com.github.lukaspili.reactivebilling.response;

import com.github.lukaspili.reactivebilling.model.SkuDetails;

import java.util.List;

/**
 * Created by lukasz on 04/05/16.
 */
public class GetSkuDetailsResponse extends Response {

    private final List<SkuDetails> list;

    public GetSkuDetailsResponse(int responseCode, List<SkuDetails> list) {
        super(responseCode);
        this.list = list;
    }

    public List<SkuDetails> getList() {
        return list;
    }
}
