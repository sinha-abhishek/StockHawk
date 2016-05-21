package com.sam_chordas.android.stockhawk.widgets;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.models.StockDetailModel;

class DataProviderObserver extends ContentObserver {
    private AppWidgetManager mAppWidgetManager;
    private ComponentName mComponentName;

    DataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
        super(h);
        mAppWidgetManager = mgr;
        mComponentName = cn;
    }


    @Override
    public void onChange(boolean selfChange) {
        // The data has changed, so notify the widget that the collection view needs to be updated.
        // In response, the factory's onDataSetChanged() will be called which will requery the
        // cursor for the new data.
        mAppWidgetManager.notifyAppWidgetViewDataChanged(
                mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.weather_list);
    }
}


/**
 * Created by abhishek on 17/05/16.
 */
public class StockWidgetProvider extends AppWidgetProvider {
    public static String CLICK_ACTION = "CLICK";
    public static String REFRESH_ACTION = "REFRESH";
    //public static String EXTRA_DAY_ID = "com.example.android.weatherlistwidget.day";
    private static HandlerThread sWorkerThread;
    private static Handler sWorkerQueue;
    private static DataProviderObserver sDataObserver;
    private static final int sMaxDegrees = 96;
    RemoteViews rv;
    public StockWidgetProvider() {
        sWorkerThread = new HandlerThread("WidgetProvider-worker");
        sWorkerThread.start();
        sWorkerQueue = new Handler(sWorkerThread.getLooper());
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //context.startService(new Intent(context, TodayWidgetIntentService.class));
        for (int i = 0; i < appWidgetIds.length; ++i) {
            RemoteViews layout = buildLayout(context, appWidgetIds[i], true);
            appWidgetManager.updateAppWidget(appWidgetIds[i], layout);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Register for external updates to the data to trigger an update of the widget.  When using
        // content providers, the data is often updated via a background service, or in response to
        // user interaction in the main app.  To ensure that the widget always reflects the current
        // state of the data, we must listen for changes and update ourselves accordingly.
        final ContentResolver r = context.getContentResolver();
        if (sDataObserver == null) {
            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context, StockWidgetProvider.class);
            sDataObserver = new DataProviderObserver(mgr, cn, sWorkerQueue);
            r.registerContentObserver(QuoteProvider.Quotes.CONTENT_URI, true, sDataObserver);
        }
    }

    private RemoteViews buildLayout(final Context context, final int appWidgetId, boolean largeLayout) {
        if (largeLayout) {
            // Specify the service to provide data for the collection widget.  Note that we need to
            // embed the appWidgetId via the data otherwise it will be ignored.
            final Intent intent = new Intent(context, TodayWidgetIntentService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            rv.setRemoteAdapter(appWidgetId, R.id.weather_list, intent);
            final RemoteViews rv1 = rv;
            int id = appWidgetId;
            sWorkerQueue.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    rv1.setScrollPosition(R.id.weather_list, 5);
                    final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                    mgr.partiallyUpdateAppWidget(appWidgetId, rv);
                }

            }, 1000);

            // Set the empty view to be displayed if the collection is empty.  It must be a sibling
            // view of the collection view.
            rv.setEmptyView(R.id.weather_list, R.id.empty_view);

            // Bind a click listener template for the contents of the weather list.  Note that we
            // need to update the intent's data if we set an extra, since the extras will be
            // ignored otherwise.
            final Intent onClickIntent = new Intent(context, StockWidgetProvider.class);
            onClickIntent.setAction(StockWidgetProvider.CLICK_ACTION);
            onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            onClickIntent.setData(Uri.parse(onClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0,
                    onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.weather_list, onClickPendingIntent);




        } else {
//            rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout_small);
//
//            // Update the header to reflect the weather for "today"
//            Cursor c = context.getContentResolver().query(WeatherDataProvider.CONTENT_URI, null,
//                    null, null, null);
//            if (c.moveToPosition(0)) {
//                int tempColIndex = c.getColumnIndex(WeatherDataProvider.Columns.TEMPERATURE);
//                int temp = c.getInt(tempColIndex);
//                String formatStr = context.getResources().getString(R.string.header_format_string);
//                String header = String.format(formatStr, temp,
//                        context.getString(R.string.city_name));
//                rv.setTextViewText(R.id.city_name, header);
//            }
//            c.close();
        }
        return rv;
    }
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        //context.startService(new Intent(context, TodayWidgetIntentService.class));
        int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        RemoteViews layout;

        layout = buildLayout(context, appWidgetId, true);
        appWidgetManager.updateAppWidget(appWidgetId, layout);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }
}
