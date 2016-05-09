package com.sam_chordas.android.stockhawk.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by abhishek on 09/05/16.
 */
public class QueryResponseModel {
    @SerializedName("query")
    public StockDetailModel stockDetailModel;
}
