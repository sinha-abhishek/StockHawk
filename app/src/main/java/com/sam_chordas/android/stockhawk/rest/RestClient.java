package com.sam_chordas.android.stockhawk.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sam_chordas.android.stockhawk.service.RestService;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by abhishek on 17/02/16.
 */
public class RestClient {
    private static final String BASE_URL = "https://query.yahooapis.com/";
    private RestService apiService;

    public RestClient(){
        Gson gson = new GsonBuilder().create();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .setConverter(new GsonConverter(gson))
                .build();
        apiService = restAdapter.create(RestService.class);
    }

    public RestService getApiService()
    {
        return apiService;
    }
}
