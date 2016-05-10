package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.models.StockDailyDetailModel;
import com.sam_chordas.android.stockhawk.mvp.StockDetailModelCollection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abhishek on 09/05/16.
 */
public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.graph)
    LineChartView graphView;

    StockDetailModelCollection stockDetailModelCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_detail);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        stockDetailModelCollection = new StockDetailModelCollection(this,"YHOO");
        stockDetailModelCollection.BindView(this);
    }

    private Date toDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedCurrentDate = sdf.parse(dateString);
        return convertedCurrentDate;
    }

    public void CreateGraph(List<StockDailyDetailModel> stockDailyDetailModelList, int count) {
        //LineGraphSeries<DataPoint> line = new LineGraphSeries<DataPoint>();
        //ArrayList<DataPoint> datapoints = new ArrayList<>();

        int i = 0;
        float minimum = Float.MAX_VALUE;
        float maximum = 0.0f;
        String[] labels = new String[count];
        float[] vals = new float[count];

        for (StockDailyDetailModel stockDetail:
             stockDailyDetailModelList) {
            try {
                DataPoint dp = new DataPoint(toDate(stockDetail.date), Float.parseFloat(stockDetail.close));
                labels[i] =stockDetail.date;
                vals[i] = Float.parseFloat(stockDetail.close);
                if (minimum > vals[i]) {
                    minimum = vals[i];
                }
                if (maximum < vals[i]) {
                    maximum = vals[i];
                }
                i++;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
       // LineGraphSeries<DataPoint> line = new LineGraphSeries<>(datapoints);
        //graphView.
        LineSet lineset = new LineSet(labels, vals);
        graphView.addData(lineset);
        graphView.setAxisBorderValues((int) minimum -5, (int)maximum + 5);
        graphView.show();
        //graphView.addSeries(line);
    }


}
