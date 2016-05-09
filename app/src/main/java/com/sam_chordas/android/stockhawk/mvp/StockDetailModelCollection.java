package com.sam_chordas.android.stockhawk.mvp;

import android.content.Context;
import android.util.Log;

import com.sam_chordas.android.stockhawk.models.QueryResponseModel;
import com.sam_chordas.android.stockhawk.models.StockDailyDetailModel;
import com.sam_chordas.android.stockhawk.models.StockDetailModel;
import com.sam_chordas.android.stockhawk.rest.RestClient;
import com.sam_chordas.android.stockhawk.service.RestService;
import com.sam_chordas.android.stockhawk.ui.DetailActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by abhishek on 09/05/16.
 */
public class StockDetailModelCollection extends BaseModelCollection<List<StockDailyDetailModel>,DetailActivity> {
    String symbol;
    public StockDetailModelCollection(Context context, String symbol) {
        super(context, false, 0, StockDetailModelCollection.class.getSimpleName());
        this.symbol = symbol;
        this.syncModels();
    }

    @Override
    protected void FetchAndSave() {
        RestClient client = new RestClient();
        RestService service = client.getApiService();
        service.GetDetails(formQuery(), new Callback<QueryResponseModel>() {
            @Override
            public void success(QueryResponseModel queryResponseModel, Response response) {
                setModels(queryResponseModel.stockDetailModel.getStockDetails());
            }

            @Override
            public void failure(RetrofitError error) {
                //show error
                setModels(null);
            }
        });
    }

    private String DateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String format = formatter.format(date);
        return format;
    }

    private Date oneYearAgo() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTime();
    }

    private String formQuery() {

        String startDate = DateToString(oneYearAgo()) ;
        String endDate = DateToString(Calendar.getInstance().getTime());
        StringBuilder stringBuilder = new StringBuilder();
        try {
//            stringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol = ", "UTF-8"));
//
//            stringBuilder.append(URLEncoder.encode("\""+symbol+"\"", "UTF-8"));
//            stringBuilder.append(URLEncoder.encode(" and startDate=\"", "UTF-8"));
//            stringBuilder.append(URLEncoder.encode(startDate+"\"", "UTF-8"));
//            stringBuilder.append(URLEncoder.encode(" and endDate=\"", "UTF-8"));
//            stringBuilder.append(URLEncoder.encode(endDate+"\"", "UTF-8"));
            stringBuilder.append("select * from yahoo.finance.historicaldata where symbol = ");

            stringBuilder.append("\""+symbol+"\"");
            stringBuilder.append(" and startDate=\"");
            stringBuilder.append(startDate+"\"");
            stringBuilder.append(" and endDate=\"");
            stringBuilder.append(endDate+"\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("StockDetailModelCollection", stringBuilder.toString());
        return stringBuilder.toString();

    }

    @Override
    protected void UploadModels() {

    }

    @Override
    protected void HandleNetworkError() {

    }

    @Override
    protected boolean IsUpdateNeeded() {
        if (getModels() == null || getModels().size() == 0) {
            return true;
        }
        return false;
    }

    @Override
    protected void SetUpdating() {

    }

    @Override
    protected void updateView(DetailActivity view) {

    }
}
