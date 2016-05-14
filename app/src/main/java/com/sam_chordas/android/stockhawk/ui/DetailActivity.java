package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.sam_chordas.android.stockhawk.R;


public class DetailActivity extends AppCompatActivity {

    public static final String DETAILFRAGMENT_TAG = "detail_fragment";
    private ShareActionProvider mShareActionProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            String data = getIntent().getStringExtra(MainFragment.SELECTED);
            Bundle args = new Bundle();
            args.putString(MainFragment.SELECTED, data);
            DetailFragment detailActivityFragment = new DetailFragment();
            detailActivityFragment.setArguments(args);
            //detailActivityFragment.SetShareProvider(mShareActionProvider);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_detail,
                    detailActivityFragment, DETAILFRAGMENT_TAG);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail, detailActivityFragment, DETAILFRAGMENT_TAG)
                    .commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }



}
