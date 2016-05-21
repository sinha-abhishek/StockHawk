package com.sam_chordas.android.stockhawk.widgets;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by abhishek on 20/05/16.
 */
public class TodayWidgetIntentService extends RemoteViewsService {
    private static final String[] STOCK_COLUMNS = {
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE
    };

    private static final int INDEX_ID = 0;
    private static final int INDEX_SYMBOL = 1;
    private static final int INDEX_BID = 2;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
//    public TodayWidgetIntentService() {
//        super("todaywidgetintentservice");
//    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }

//    @Override
//    protected void onHandleIntent(Intent intent) {
//// Retrieve all of the Today widget ids: these are the widgets we need to update
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
//        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
//                StockWidgetProvider.class));
//
//        // Get today's data from the ContentProvider
//        Uri stockURI = QuoteProvider.Quotes.CONTENT_URI;
//        Cursor data = getContentResolver().query(stockURI, STOCK_COLUMNS, null,
//                null,  null);
//        if (data == null) {
//            return;
//        }
//        if (!data.moveToFirst()) {
//            data.close();
//            return;
//        }
//
//        String symbol = data.getString(INDEX_SYMBOL);
//        String bid = data.getString(INDEX_BID);
//        Log.i("#####Widget","symbol="+symbol+" bid="+bid);
//
//        // Extract the  data from the Cursor
////        int weatherId = data.getInt(INDEX_WEATHER_ID);
////        int weatherArtResourceId = Utility.getArtResourceForWeatherCondition(weatherId);
////        String description = data.getString(INDEX_SHORT_DESC);
////        double maxTemp = data.getDouble(INDEX_MAX_TEMP);
////        String formattedMaxTemperature = Utility.formatTemperature(this, maxTemp);
////        data.close();
//
//        // Perform this loop procedure for each Today widget
//        for (int appWidgetId : appWidgetIds) {
//            int layoutId = R.layout.widget_today_small;
//            RemoteViews views = new RemoteViews(getPackageName(), layoutId);
//
//            // Add the data to the RemoteViews
//            views.setImageViewResource(R.id.widget_icon, R.drawable.common_google_signin_btn_icon_dark);
//            // Content Descriptions for RemoteViews were only added in ICS MR1
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                setRemoteContentDescription(views, "");
//            }
//            views.setTextViewText(R.id.widget_high_temperature, bid);
//
//            // Create an Intent to launch MainActivity
//            Intent launchIntent = new Intent(this, MyStocksActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
//            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
//
//            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views);
//        }
//    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }
}

/**
 * This is the factory that will provide data to the collection widget.
 */
class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor mCursor;
    private int mAppWidgetId;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        // Since we reload the cursor in onDataSetChanged() which gets called immediately after
        // onCreate(), we do nothing here.
    }

    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    public int getCount() {
        return mCursor.getCount();
    }

    public RemoteViews getViewAt(int position) {
        Log.i("####", "getAt"+position);
        // Get the data for this position from the content provider
        String day = "Unknown Day";
        int temp = 0;
        if (mCursor.moveToPosition(position)) {
            final int symbolIndex = mCursor.getColumnIndex(QuoteColumns.SYMBOL);
            final int bidIndex = mCursor.getColumnIndex(
                    QuoteColumns.BIDPRICE);
            day = mCursor.getString(symbolIndex);
            temp = mCursor.getInt(bidIndex);
            Log.i("####","stock="+day+" temp="+temp);
        }

        // Return a proper item with the proper day and temperature
        final String formatStr = mContext.getResources().getString(R.string.item_format_string);
        final int itemId = R.layout.widget_item;
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);
        rv.setTextViewText(R.id.widget_item, String.format(formatStr, temp, day));
        //rv.setScrollPosition(R.id.weather_list, 5);

        // Set the click intent so that we can handle it and show a toast message
//        final Intent fillInIntent = new Intent();
//        final Bundle extras = new Bundle();
//        extras.putString(Qu.EXTRA_DAY_ID, day);
//        fillInIntent.putExtras(extras);
//        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

        return rv;
    }
    public RemoteViews getLoadingView() {
        // We aren't going to return a default loading view in this sample
        return null;
    }

    public int getViewTypeCount() {
        // Technically, we have two types of views (the dark and light background views)
        return 2;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // Refresh the cursor
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI, null, null,
                null, null);
    }
}
