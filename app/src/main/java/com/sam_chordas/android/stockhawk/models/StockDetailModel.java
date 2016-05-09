package com.sam_chordas.android.stockhawk.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by abhishek on 09/05/16.
 */
public class StockDetailModel {

    public class Quote {
        @SerializedName("quote")
        List<StockDailyDetailModel> stockDailyDetailModelList;
    }

    @SerializedName("count")
    public long count;

    @SerializedName("created")
    public String created;

    @SerializedName("results")
    public Quote quote;

    public List<StockDailyDetailModel> getStockDetails() {
        return quote.stockDailyDetailModelList;
    }

}
