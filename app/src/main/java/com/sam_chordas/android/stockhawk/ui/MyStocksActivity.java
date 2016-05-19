package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;
import com.sam_chordas.android.stockhawk.rest.RecyclerViewItemClickListener;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.service.StockIntentService;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.melnykov.fab.FloatingActionButton;
import com.sam_chordas.android.stockhawk.touch_helper.SimpleItemTouchHelperCallback;

public class MyStocksActivity extends AppCompatActivity  implements MainFragment.FragmentCallback{//implements LoaderManager.LoaderCallbacks<Cursor>{

  /**
   * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
   */

  /**
   * Used to store the last screen title. For use in {@link #restoreActionBar()}.
   */
  private CharSequence mTitle;
  private ProgressBar spinner;
  private Intent mServiceIntent;
  private ItemTouchHelper mItemTouchHelper;
  private static final int CURSOR_LOADER_ID = 0;
  private QuoteCursorAdapter mCursorAdapter;
  private Context mContext;
  private Cursor mCursor;
  boolean isConnected;
  Bundle savedState;
  boolean mHasTwoFragments;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_my_stocks);
    spinner = (ProgressBar)findViewById(R.id.spinnerView);
    mTitle = getTitle();
    savedState = savedInstanceState;
    hideSpinner();
    if (findViewById(R.id.fragment_detail) != null) {
      mHasTwoFragments = true;
      if (savedInstanceState == null) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_detail, new DetailFragment(),DetailActivity.DETAILFRAGMENT_TAG)
                .commit();
      }
      DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DetailActivity.DETAILFRAGMENT_TAG);

    } else {
      mHasTwoFragments = false;
    }

  }


  @Override
  public void onResume() {
    super.onResume();
    //getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
  }

  public void networkToast(){
    Toast.makeText(mContext, getString(R.string.network_toast), Toast.LENGTH_SHORT).show();
  }

  @Override
  public void restoreActionBar() {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    actionBar.setDisplayShowTitleEnabled(true);
    actionBar.setTitle(mTitle);
  }

  @Override
  public void showNoContent(){

  }

  @Override
  public void onItemSelected(String symbol) {
    if (!mHasTwoFragments) {
      Intent i = new Intent(this, DetailActivity.class);
      i.putExtra(MainFragment.SELECTED, symbol);
      startActivity(i);
      return;
    }
    Bundle args = new Bundle();
    args.putString(MainFragment.SELECTED, symbol);
    DetailFragment fragment = new DetailFragment();
    fragment.setArguments(args);
    getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_detail, fragment, DetailActivity.DETAILFRAGMENT_TAG)
            .commit();
    return;
  }

  @Override
  public void onStocksLoaded(RecyclerView view, int position) {
    if (mHasTwoFragments == true){
      RecyclerView.ViewHolder holder = view.findViewHolderForLayoutPosition(position);
      if (holder != null && holder.itemView!=null)
        holder.itemView.performClick();
    }
  }


  public void ShowSpinner(boolean show) {
    if (spinner != null) {
      if (show) {
        spinner.setVisibility(View.VISIBLE);
      } else {
        spinner.setVisibility(View.GONE);
      }
    }
  }

  @Override
  public void showSpinner() {
    ShowSpinner(true);
  }

  @Override
  public void hideSpinner() {
    ShowSpinner(false);
  }




}
