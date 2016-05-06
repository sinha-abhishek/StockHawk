package com.sam_chordas.android.stockhawk.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sam_chordas.android.stockhawk.R;

import butterknife.BindView;

/**
 * Created by abhishek on 06/05/16.
 */
public class MainFragment extends Fragment {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_main, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
