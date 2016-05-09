package com.sam_chordas.android.stockhawk.service;

import com.sam_chordas.android.stockhawk.models.QueryResponseModel;
import com.sam_chordas.android.stockhawk.models.StockDetailModel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by abhishek on 09/05/16.
 */
public interface RestService {
    @GET("/v1/public/yql/?diagnostics=true&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys")
    void GetDetails(@Query("q") String query, Callback<QueryResponseModel> stockDetailModelCallback);
}
