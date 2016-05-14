package com.sam_chordas.android.stockhawk.ui;

import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.jjoe64.graphview.series.DataPoint;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.models.StockDailyDetailModel;
import com.sam_chordas.android.stockhawk.mvp.ModelCollectionManager;
import com.sam_chordas.android.stockhawk.mvp.StockDetailModelCollection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abhishek on 15/05/16.
 */
public class DetailFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.radio_week)
    RadioButton weekRadio;

    @BindView(R.id.radio_month)
    RadioButton monthRadio;

    @BindView(R.id.radio_year)
    RadioButton yearRadio;

    @BindView(R.id.graph_yearly)
    LineChartView graphYearly;

    @BindView(R.id.topTextView)
    TextView symbolView;

    @BindView(R.id.stockTip)
    TextView stockTip;

    public enum Selection {
        WEEKLY,
        MONTHLY,
        YEARLY
    }

    public Selection currentSelection;

    String symbol;
    StockDetailModelCollection stockDetailModelCollection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            symbol = args.getString(MainFragment.SELECTED, "YHOO");
            stockDetailModelCollection = new StockDetailModelCollection(getActivity(), symbol);
        } else {
            symbol = savedInstanceState.getString("symbol");
            stockDetailModelCollection = ModelCollectionManager.getInstance().restoreModels(savedInstanceState);
            if (stockDetailModelCollection == null) {
                stockDetailModelCollection = new StockDetailModelCollection(getActivity(), symbol);
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        ModelCollectionManager.getInstance().saveModels(stockDetailModelCollection, outState);
        outState.putString("symbol",symbol);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        symbolView.setText(symbol);
        stockDetailModelCollection.BindView(this);
        currentSelection = Selection.WEEKLY;
        weekRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelection = Selection.WEEKLY;
                stockDetailModelCollection.OnSelectionChange();

            }
        });
        monthRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelection = Selection.MONTHLY;
                stockDetailModelCollection.OnSelectionChange();
            }
        });
        yearRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelection = Selection.YEARLY;
                stockDetailModelCollection.OnSelectionChange();
            }
        });
        return view;
    }



    public void CreateGraph(List<StockDailyDetailModel> stockDailyDetailModelList, int count) {
        //LineGraphSeries<DataPoint> line = new LineGraphSeries<DataPoint>();
        //ArrayList<DataPoint> datapoints = new ArrayList<>();

        int i = 0;
        float minimum = Float.MAX_VALUE;
        float maximum = 0.0f;
        ArrayList<String> lLabels = new ArrayList<>();
        ArrayList<Float> lVals = new ArrayList<>();

        for (StockDailyDetailModel stockDetail:
                stockDailyDetailModelList) {
            try {
                Float v = Float.parseFloat(stockDetail.close);
               // if (i%3 == 0) {
                    lLabels.add(stockDetail.date);
                    lVals.add(v);
//                    vals[j] = Float.parseFloat(stockDetail.close);
//                    labels[j++] = stockDetail.date;
               // }

                if (minimum > v) {
                    minimum = v;
                }
                if (maximum < v) {
                    maximum = v;
                }
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final float[] vals = dumbConvert1( lVals.toArray());
        final String[] labels = dumbConvert2( lLabels.toArray());
        LineSet lineset = new LineSet(labels, vals);
        graphYearly.dismiss();
        graphYearly.addData(lineset);
        graphYearly.setXAxis(true);
        graphYearly.setAxisBorderValues((int) minimum - 1, (int) maximum + 1);
        graphYearly.setAxisLabelsSpacing(3.0f);
        graphYearly.setXLabels(AxisController.LabelPosition.NONE);
        stockTip.setText(labels[0]+":"+ vals[0]);
        graphYearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        graphYearly.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                Log.i("DetailFragment", "clicked at "+setIndex+" "+entryIndex);
                stockTip.setText(labels[entryIndex]+":"+ vals[entryIndex]);
            }
        });
        graphYearly.show();

    }

    private float[] dumbConvert1(Object[] vals) {
        float[]  res = new float[vals.length];
        int i = 0;
        for (Object val:
             vals) {
            float r = (float) val;
            res[i] = r;
            i++;
        }
        return res;
    }

    private String[] dumbConvert2(Object[] vals) {
        String[]  res = new String[vals.length];
        int i = 0;
        for (Object val:
                vals) {
            String r = (String) val;
            res[i] = r;
            i++;
        }
        return res;
    }
}
