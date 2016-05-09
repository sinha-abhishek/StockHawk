package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.mvp.StockDetailModelCollection;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abhishek on 09/05/16.
 */
public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.graph)
    GraphView graphView;

    StockDetailModelCollection stockDetailModelCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_detail);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        stockDetailModelCollection = new StockDetailModelCollection(this,"YHOO");
    }


}
