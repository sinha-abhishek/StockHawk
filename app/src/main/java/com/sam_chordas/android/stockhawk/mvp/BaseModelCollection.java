package com.sam_chordas.android.stockhawk.mvp;

import android.content.Context;
import android.util.Log;

import com.byjus.network.networkutils.utils.PrefsHelper;
import com.byjus.network.networkutils.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.Date;

/**
 * Created by abhishek on 29/04/16.
 */
public abstract class BaseModelCollection<T,E> {
    private T _model;
    protected Context _context;
    private WeakReference<E> wView;
    protected boolean isUpdated;
    protected boolean isUpdating;
    private static String LOG_TAG = BaseModelCollection.class.getSimpleName();
    private boolean enablePeriodicRefresh;
    private long refreshFrequency;
    private Date lastRefreshed;
    private String name;


    public BaseModelCollection(Context context, boolean enablePeriodicRefresh, long refreshFrequency, String name) {
        this._context = context;
        this.enablePeriodicRefresh = enablePeriodicRefresh;
        this.refreshFrequency = refreshFrequency;
        this.name = name;
        lastRefreshed = getLastRefreshTime();
    }

    public void BindView(E view) {
        this.wView = new WeakReference<E>(view);
        if (shouldRefresh()) {
            syncModels();
        } else if (setupDone()) {
            updateView(View());
        }
    }

    protected E View() {
        if (wView == null || wView.get() == null) {
            return null;
        }
        return wView.get();
    }

    public T getModels() {
        return _model;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public boolean isUpdating() {
        return isUpdating;
    }

    public void setModels(T model) {
        this._model = model;
        if (shouldRefresh() ) {
            syncModels();
        } else if (setupDone()) {
            updateView(View());
        }
    }

    private boolean shouldRefresh() {
        if (IsUpdateNeeded()) {
            return true;
        }
        if (!enablePeriodicRefresh) {
            return false;
        }
        Date cur = new Date();
        if (lastRefreshed == null) {
            return true;
        }
        long timeSinceLastRefresh = cur.getTime() - lastRefreshed.getTime();
        if (timeSinceLastRefresh > refreshFrequency) {
            return true;
        }
        return false;
    }

    protected abstract void FetchAndSave();
    protected abstract void UploadModels();
    protected abstract void HandleNetworkError();
    protected abstract boolean IsUpdateNeeded();
    protected abstract void SetUpdating();

    public boolean syncModels() {
        if(Utils.isNetworkAvailable(_context)) {
            isUpdating = true;
            SetUpdating();
            FetchAndSave();
            return true;
        } else {
            Log.i(LOG_TAG, "Can't sync now");
            HandleNetworkError();
            return false;
        }
    }

    protected abstract void updateView(E view) ;

    protected void onSyncDone() {
        //updateView(View());
        lastRefreshed = new Date();
        saveLastRefreshTime();
    }

    private String getKey() {
        return name+"_last_refresh";
    }

    private void saveLastRefreshTime() {
        PrefsHelper helper = new PrefsHelper(_context);
        helper.savePreference(PrefsHelper.PREF_TYPE_LONG, getKey(), lastRefreshed.getTime() );
    }

    private Date getLastRefreshTime() {
        PrefsHelper helper = new PrefsHelper(_context);
        long timestamp = 0;
        if (helper.getSharedPref().contains(getKey())) {
            timestamp = (long) helper.getPreference(PrefsHelper.PREF_TYPE_LONG, getKey());
        } else {
            return null;
        }
        return new Date(timestamp);
    }

    public boolean uploadPending() {
        if (Utils.isNetworkAvailable(_context)) {
            UploadModels();
            return true;
        } else {
            Log.e(LOG_TAG, "Can't sync now");
            return false;
        }
    }


    protected boolean setupDone() {
        return View() != null && _model != null;
    }
}
