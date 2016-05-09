package com.sam_chordas.android.stockhawk.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by abhishek on 09/05/16.
 */
public class StockDailyDetailModel {
    @SerializedName("Symbol")
    public String symbol;
    @SerializedName("Date")
    public String date;
    @SerializedName("Close")
    public String close;
}
