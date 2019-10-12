package com.cypressworks.mensaplan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Kirill Rakhman
 */
public class DayChangeListener extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        MensaWidgetProvider.updateWidgets(context);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean("dayChanged", true).apply();
    }

}
