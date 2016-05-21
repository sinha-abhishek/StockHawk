package com.sam_chordas.android.stockhawk.widgets;



import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteCallbackList;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;

import com.google.android.gms.gcm.GcmTaskService;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.ui.DetailActivity;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;




/**
 * Provider for a scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetProvider extends AppWidgetProvider {
    private static WeatherDataProviderObserver sDataObserver;
    private static Handler sWorkerQueue;
    private static HandlerThread sWorkerThread;
    RemoteViews remoteViews;

    class WeatherDataProviderObserver extends ContentObserver {
        private AppWidgetManager mAppWidgetManager;
        private ComponentName mComponentName;

        WeatherDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
            super(h);
            mAppWidgetManager = mgr;
            mComponentName = cn;
        }



        @Override
        public void onChange(boolean selfChange) {
            // The data has changed, so notify the widget that the collection view needs to be updated.
            // In response, the factory's onDataSetChanged() will be called which will requery the
            // cursor for the new data.
            //int[] appWidgetIds = mAppWidgetManager.getAppWidgetIds(mComponentName);
            mAppWidgetManager.notifyAppWidgetViewDataChanged(
                    mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.weather_list);
        }
    }

    public DetailWidgetProvider() {
        //LocalBroadcastManager.getInstance().registerReceiver(myReciever, new IntentFilter(StockTaskService.FETCH_STATUS));

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
            final ComponentName cn = new ComponentName(context, QuoteProvider.class);
            sDataObserver = new WeatherDataProviderObserver(mgr, cn, null);
            r.registerContentObserver(QuoteProvider.Quotes.CONTENT_URI, true, sDataObserver);
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, DetailWidgetProvider.class));
            for (int i=0; i< appWidgetIds.length ; i++) {

                int appWidgetId = appWidgetIds[i];
                // Tell the AppWidgetManager to perform an update on the current app widget
                RemoteViews views = updateRemoteView(context,appWidgetId, appWidgetManager);
                appWidgetManager.updateAppWidget(appWidgetId, views);

            }
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
//            // Create an Intent to launch MainActivity
//            Intent intent1 = new Intent(context, MyStocksActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
//            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
//
//            // Set up the collection
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                setRemoteAdapter(context, views);
//            } else {
//                setRemoteAdapterV11(context, views);
//            }
//            boolean useDetailActivity = false;
//            Intent clickIntentTemplate = new Intent(context, MyStocksActivity.class);
//            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
//                    .addNextIntentWithParentStack(clickIntentTemplate)
//                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//            views.setPendingIntentTemplate(R.id.weather_list, clickPendingIntentTemplate);
//            views.setEmptyView(R.id.weather_list, R.id.empty_view);
//            appWidgetManager.updateAppWidget(appWidgetIds, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.weather_list);
            //super.onReceive(context,intent);
        }
    };

    private RemoteViews updateRemoteView(Context context, int appWidgetId, AppWidgetManager appWidgetManager) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // Create an Intent to launch MainActivity
        Intent intent = new Intent(context, MyStocksActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        // Set up the collection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views);
        } else {
            setRemoteAdapterV11(context, views);
        }
        boolean useDetailActivity = false;
        Intent clickIntentTemplate = new Intent(context, MyStocksActivity.class);
        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntentTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.weather_list, clickPendingIntentTemplate);
//            Intent clickIntentTemplate = useDetailActivity
//                    ? new Intent(context, DetailActivity.class)
//                    : new Intent(context, MyStocksActivity.class);
//            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
//                    .addNextIntentWithParentStack(clickIntentTemplate)
//                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//            views.setPendingIntentTemplate(R.id.weather_list, clickPendingIntentTemplate);
        views.setEmptyView(R.id.weather_list, R.id.empty_view);
        remoteViews = views;
        return views;
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter(StockTaskService.FETCH_STATUS));

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {


            // Tell the AppWidgetManager to perform an update on the current app widget
            RemoteViews views = updateRemoteView(context,appWidgetId, appWidgetManager);
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.weather_list);
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, getClass()));
        for (int appWidgetId : appWidgetIds) {
            // Tell the AppWidgetManager to perform an update on the current app widget
            RemoteViews views = updateRemoteView(context,appWidgetId, appWidgetManager);
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.weather_list);
        super.onReceive(context, intent);
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.weather_list,
                new Intent(context, DetailWidgetRemoteViewsService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.weather_list,
                new Intent(context, DetailWidgetRemoteViewsService.class));
    }
}