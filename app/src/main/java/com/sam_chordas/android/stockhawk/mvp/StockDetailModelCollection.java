package com.sam_chordas.android.stockhawk.mvp;

import android.content.Context;
import android.util.Log;

import com.sam_chordas.android.stockhawk.helpers.StockDetailHelper;
import com.sam_chordas.android.stockhawk.models.QueryResponseModel;
import com.sam_chordas.android.stockhawk.models.StockDailyDetailModel;
import com.sam_chordas.android.stockhawk.models.StockDetailModel;
import com.sam_chordas.android.stockhawk.rest.RestClient;
import com.sam_chordas.android.stockhawk.service.RestService;
import com.sam_chordas.android.stockhawk.ui.DetailActivity;
import com.sam_chordas.android.stockhawk.ui.DetailFragment;

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
public class StockDetailModelCollection extends BaseModelCollection<StockDetailHelper,DetailFragment> {
    String symbol;
    int count;
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
                count = queryResponseModel.stockDetailModel.count;
                StockDetailHelper stockDetailHelper = new StockDetailHelper(queryResponseModel.stockDetailModel.getStockDetails());
                setModels(stockDetailHelper);
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

            stringBuilder.append("select * from yahoo.finance.historicaldata where symbol = ");

            stringBuilder.append("\""+symbol+"\"");
            stringBuilder.append(" and startDate=\"");
            stringBuilder.append(startDate+"\"");
            stringBuilder.append(" and endDate=\"");
            stringBuilder.append(endDate+"\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("StockDetailCollection", stringBuilder.toString());
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
        if (getModels() == null ) {
            return true;
        }
        return false;
    }

    @Override
    protected void SetUpdating() {

    }

    public void OnSelectionChange() {
        updateView(View());
    }

    @Override
    protected void updateView(DetailFragment view) {
        if (View() != null && getModels() != null) {
            if (View().currentSelection == DetailFragment.Selection.WEEKLY)
                View().CreateGraph(getModels().weekly, count);
            else if (View().currentSelection == DetailFragment.Selection.MONTHLY) {
                View().CreateGraph(getModels().monthly, count);
            } else {
                View().CreateGraph(getModels().yearly, count);
            }
        }
    }
}
