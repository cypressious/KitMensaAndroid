package com.cypressworks.mensaplan.planmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cypressworks.mensaplan.NotificationAlarmReceiver;
import com.cypressworks.mensaplan.NotificationSetting;

public class AppUpdatedReceiver extends BroadcastReceiver {
    public AppUpdatedReceiver() {
    }

    @Override
    public void onReceive(final Context c, final Intent intent) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);

        final boolean notificationEnabled = prefs.getBoolean(
                NotificationSetting.PREF_NOTIFICATION_ENABLED, false);
        NotificationAlarmReceiver.updateStartAlarm(c, notificationEnabled);

    }
}
