package com.sam_chordas.android.stockhawk.widgets;

/**
 * Created by abhishek on 21/05/16.
 */

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.concurrent.ExecutionException;

/**
 * RemoteViewsService controlling the data being shown in the scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();
    private static final String[] COLUMNS = {
            QuoteColumns._ID ,
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE
    };
    // these indices must match the projection
    static final int INDEX_ID = 0;
    static final int INDEX_SYMBOL = 1;
    static final int INDEX_PRICE = 2;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                Uri stockURI = QuoteProvider.Quotes.CONTENT_URI;
                data = getContentResolver().query(stockURI, COLUMNS, null,
                        null,  null);

                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
               Uri uri = QuoteProvider.Quotes.CONTENT_URI;
                data = getContentResolver().query(uri,
                        COLUMNS,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                Log.i("##WTF","pos="+position);
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_item);
                int id = data.getInt(INDEX_ID);
                String symbol = data.getString(INDEX_SYMBOL);
                String price = data.getString(INDEX_PRICE);
//                String formattedDate = Utility.getFriendlyDayString(
//                        DetailWidgetRemoteViewsService.this, dateInMillis, false);
//                double maxTemp = data.getDouble(INDEX_WEATHER_MAX_TEMP);
//                double minTemp = data.getDouble(INDEX_WEATHER_MIN_TEMP);
//                String formattedMaxTemperature =
//                        Utility.formatTemperature(DetailWidgetRemoteViewsService.this, maxTemp);
//                String formattedMinTemperature =
//                        Utility.formatTemperature(DetailWidgetRemoteViewsService.this, minTemp);
//                if (weatherArtImage != null) {
//                    views.setImageViewBitmap(R.id.widget_icon, weatherArtImage);
//                } else {
//                    views.setImageViewResource(R.id.widget_icon, weatherArtResourceId);
//                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views,symbol+" "+price);
                }
                final String formatStr = getApplicationContext().getResources().getString(R.string.item_format_string);
                //final int itemId = R.layout.widget_item;
//                RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);
//                rv.setTextViewText(R.id.widget_item, String.format(formatStr, temp, day));
                views.setTextViewText(R.id.widget_item, symbol+" "+price);
//                views.setTextViewText(R.id.widget_description, description);
//                views.setTextViewText(R.id.widget_high_temperature, formattedMaxTemperature);
//                views.setTextViewText(R.id.widget_low_temperature, formattedMinTemperature);

                final Intent fillInIntent = new Intent();
//                String locationSetting =
//                        Utility.getPreferredLocation(DetailWidgetRemoteViewsService.this);

                fillInIntent.setData(QuoteProvider.Quotes.CONTENT_URI);
                views.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_icon, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}