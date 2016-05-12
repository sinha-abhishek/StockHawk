package com.sam_chordas.android.stockhawk.mvp;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.widget.CursorAdapter;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.service.StockIntentService;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.ui.MainFragment;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

import java.util.ArrayList;

/**
 * Created by abhishek on 09/05/16.
 */
public class StocksModelCollection extends BaseModelCollection<Cursor, MainFragment>
        implements LoaderManager.LoaderCallbacks<Cursor> {

    LoaderManager loaderManager;
    private static final int CURSOR_LOADER_ID = 0;

    private BroadcastReceiver myReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int result = intent.getIntExtra("result", GcmNetworkManager.RESULT_FAILURE);
            if (result != GcmNetworkManager.RESULT_SUCCESS) {
                //show error here
                if (View() != null) {
                    if(!Utils.isNetworkAvailable(_context)) {
                        View().showError(_context.getString(R.string.err_network));
                    } else {
                        View().showError(_context.getString(R.string.err_other));
                    }
                }
            } else {
                onSyncDone();
                if (View() != null) {
                    updateView(View());
                }
                if (intent.hasExtra("errors")) {
                    ArrayList<String> errors = intent.getStringArrayListExtra("errors");
                    for (String error:
                         errors) {
                        if (View()!= null) {
                            View().showErrorToast(error);
                        }
                    }
                }
            }
        }
    };


    public StocksModelCollection(Context context, LoaderManager loaderManager) {
        super(context, true, 86400*1000, StocksModelCollection.class.getSimpleName());
        this.loaderManager = loaderManager;
        scheduleDownload();
        loaderManager.initLoader(CURSOR_LOADER_ID, null, this);
        LocalBroadcastManager.getInstance(_context).registerReceiver(myReciever, new IntentFilter(StockTaskService.FETCH_STATUS));
    }


    private void scheduleDownload() {
        long period = 3600L;
        long flex = 10L;
        String periodicTag = "periodic";
        PeriodicTask periodicTask = new PeriodicTask.Builder()
                .setService(StockTaskService.class)
                .setPeriod(period)
                .setFlex(flex)
                .setTag(periodicTag)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setRequiresCharging(false)
                .build();
        // Schedule task with tag "periodic." This ensure that only the stocks present in the DB
        // are updated.
        GcmNetworkManager.getInstance(_context).schedule(periodicTask);
    }

    @Override
    protected void FetchAndSave() {
        Intent mServiceIntent = new Intent(_context, StockIntentService.class);
        mServiceIntent.putExtra("tag", "init");
        _context.startService(mServiceIntent);

    }

    public void handleAdd(String symbol) {
        Cursor c = _context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[] { QuoteColumns.SYMBOL }, QuoteColumns.SYMBOL + "= ?",
                new String[] { symbol }, null);
        if (c.getCount() != 0) {
           //show already saved txt
            if (View()!=null) {
                View().ShowSaved();
            }
            return;
        } else {
            // Add the stock to DB
            Intent mServiceIntent = new Intent(_context, StockIntentService.class);
            mServiceIntent.putExtra("tag", "add");
            mServiceIntent.putExtra("symbol", symbol);
            _context.startService(mServiceIntent);
        }
    }

    @Override
    protected void UploadModels() {

    }

    @Override
    protected void HandleNetworkError() {
        //ask activity to show network toast
        if (getModels() == null || getModels().getCount() == 0) {
            //ask to show error
            if (View() != null) {
                View().showError(_context.getString(R.string.err_network));
            }
        } else {
            if (View() != null) {
                View().networkToast();
            }
        }
    }

    public void onResume() {
        loaderManager.restartLoader(CURSOR_LOADER_ID,null, this);
    }

    public void onOptionsChange() {
        _context.getContentResolver().notifyChange(QuoteProvider.Quotes.CONTENT_URI, null);
    }

    @Override
    protected boolean IsUpdateNeeded() {
        if (getModels() == null || getModels().getCount() == 0) {
            return true;
        }
        return false;
    }

    @Override
    protected void SetUpdating() {
        if (View() != null) {
            View().ShowUpdating();
        }
    }

    @Override
    protected void updateView(MainFragment view) {
        if (View() != null) {
            if (getModels() != null && getModels().getCount() != 0) {
                View().UpdateAdapter(getModels());
            } else {
                if(!Utils.isNetworkAvailable(_context)) {
                    View().showError(_context.getString(R.string.err_network));
                } else {
                    View().showError(_context.getString(R.string.err_other));
                }
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(_context, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0) {
            if (!Utils.isNetworkAvailable(_context)) {
                if (View() != null) {
                    View().networkToast();
                    View().showError(_context.getString(R.string.err_network));
                }
            } else if (View() != null) {
                View().showError(_context.getString(R.string.err_other));
            }
        } else {
            setModels(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (View() != null) {
            View().UpdateAdapter(null);
        }
    }
}
