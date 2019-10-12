package com.cypressworks.mensaplan;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.cypressworks.mensaplan.planmanager.MensaDropdownAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Kirill Rakhman
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MensaWidgetProvider extends AppWidgetProvider {

    private static final SimpleDateFormat weekDayDateFormat = new SimpleDateFormat("E (dd.MM.)",
                                                                                   Locale.GERMANY);

    private static final String ACTION_RELOAD = "com.cypressworks.mensaplan.REFRESH";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final String action = intent.getAction();

        if (action != null && action.equals(ACTION_RELOAD)) {
            log("Reloading data for widget");

            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("reload",
                                                                                     true).putBoolean(
                    "dayChanged", true).apply();

            updateWidgets(context);
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(
            final Context context, final AppWidgetManager appWidgetManager,
            final int[] appWidgetIds) {
        // update each of the app widgets with the remote adapter
        for (final int appWidgetId : appWidgetIds) {
            final RemoteViews rv = new RemoteViews(context.getPackageName(),
                                                   R.layout.widget_layout);

            // header
            final String mensaName = MensaDropdownAdapter.getManagerFromPreferences(
                    context).getFullProviderName();
            rv.setTextViewText(R.id.textViewMensa, mensaName);

            rv.setTextViewText(R.id.textViewDay, weekDayDateFormat.format(new Date()));

            final Intent refreshIntent = new Intent(context, getClass());
            refreshIntent.setAction(ACTION_RELOAD);
            final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,
                                                                                  refreshIntent, 0);
            rv.setOnClickPendingIntent(R.id.widgetButtonRefresh, refreshPendingIntent);

            // reload button
            final Intent mainActIntent = new Intent(context, MainActivity.class);
            final PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0,
                                                                              mainActIntent, 0);
            rv.setOnClickPendingIntent(R.id.LinearLayoutHeader, mainPendingIntent);

            // list
            final Intent widgetServiceIntent = new Intent(context, MensaWidgetService.class);
            widgetServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            widgetServiceIntent.setData(
                    Uri.parse(widgetServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));
            rv.setRemoteAdapter(appWidgetId, R.id.listViewWidget, widgetServiceIntent);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    void log(final String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), msg);
        }
    }

    public static void updateWidgets(final Context c) {

        final AppWidgetManager widgetManager = AppWidgetManager.getInstance(c);
        final int[] ids = widgetManager.getAppWidgetIds(
                new ComponentName(c, MensaWidgetProvider.class));

        final Intent intent = new Intent(c, MensaWidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

        c.sendBroadcast(intent);

        widgetManager.notifyAppWidgetViewDataChanged(ids, R.id.listViewWidget);
    }
}
