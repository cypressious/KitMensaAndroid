package com.cypressworks.mensaplan;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.cypressworks.mensaplan.food.Plan;
import com.cypressworks.mensaplan.planmanager.MensaDropdownAdapter;
import com.cypressworks.mensaplan.planmanager.PlanManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

public class NotificationAlarmReceiver extends BroadcastReceiver {

    private static final String ACTION_START_GEOFENCE = "START_GEOFENCE";
    private static final String ACTION_STOP_GEOFENCE = "STOP_GEOFENCE";

    public static void updateStartAlarm(final Context c, final boolean enabled) {
        log("Updating alarm for geofencing start, enabled: " + enabled);

        final AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        final PendingIntent pendingIntent = getPendingIntentStart(c);
        am.cancel(pendingIntent);

        if (!enabled) {
            return;
        }

        final NotificationSetting setting = NotificationSetting.fromPrefs(c);

        final Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, setting.getNotifStartHour());
        cal.set(Calendar.MINUTE, setting.getNotifStartMinute());

        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                               AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void updateStopAlarm(final Context c, final boolean enabled) {
        log("Updating alarm for geofencing stop, enabled: " + enabled);

        final AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        final PendingIntent pendingIntent = getPendingIntentStop(c);
        am.cancel(pendingIntent);

        if (!enabled) {
            return;
        }

        final NotificationSetting setting = NotificationSetting.fromPrefs(c);

        final Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, setting.getNotifEndHour());
        cal.set(Calendar.MINUTE, setting.getNotifEndMinute());

        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }

    private static PendingIntent getPendingIntentStart(final Context c) {
        final Intent intent = new Intent(c, NotificationAlarmReceiver.class) //
                .setAction(ACTION_START_GEOFENCE);
        return PendingIntent.getBroadcast(c, 0, intent, 0);
    }

    private static PendingIntent getPendingIntentStop(final Context c) {
        final Intent intent = new Intent(c, NotificationAlarmReceiver.class) //
                .setAction(ACTION_STOP_GEOFENCE);
        return PendingIntent.getBroadcast(c, 0, intent, 0);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_START_GEOFENCE.equals(action)) {
                if (startGeofence(context)) {
                    updateStopAlarm(context, true);
                }
            } else if (ACTION_STOP_GEOFENCE.equals(action)) {
                stopGeofence(context);
            }
        }
    }

    private boolean startGeofence(final Context c) {
        boolean result = false;
        try {
            result = new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(final Void... params) {

                    final PlanManager manager = MensaDropdownAdapter.getManagerFromPreferences(c);

                    final Plan plan = manager.getPlan(new GregorianCalendar(), false);

                    if (plan.isEmpty()) {
                        return false;
                    }

                    if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(
                            c) != ConnectionResult.SUCCESS) {
                        return false;
                    }

                    final GoogleApiClient apiClient = new GoogleApiClient.Builder(c).addApi(
                            LocationServices.API).build();

                    final NotificationSetting setting = NotificationSetting.fromPrefs(c);

                    //compute expiration
                    final Calendar cal = new GregorianCalendar();
                    cal.set(Calendar.HOUR_OF_DAY, setting.getNotifEndHour());
                    cal.set(Calendar.MINUTE, setting.getNotifEndMinute());

                    final long expiration = cal.getTimeInMillis() - System.currentTimeMillis();

                    apiClient.blockingConnect();

                    if (!apiClient.isConnected()) {
                        log("Api Client could not connect.");
                        return false;
                    }

                    final Geofence geofence = new Geofence.Builder() //
                            .setCircularRegion(setting.getNotifCenter().latitude,
                                               setting.getNotifCenter().longitude,
                                               setting.getNotifRadius()) //
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER) //
                            .setRequestId("notification") //
                            .setExpirationDuration(expiration) //
                            .build();

                    final PendingIntent pendingIntent = GeofenceReceiverService.getPendingIntent(c);
                    LocationServices.GeofencingApi.addGeofences(apiClient, Arrays.asList(geofence),
                                                                pendingIntent);

                    apiClient.disconnect();
                    return true;
                }
            }.execute().get();
        } catch (InterruptedException | ExecutionException ignored) {
        }

        log("Geofence started: " + result);

        return result;

    }

    private void stopGeofence(final Context c) {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(c) != ConnectionResult.SUCCESS) {
            return;
        }

        final GoogleApiClient apiClient = new GoogleApiClient.Builder(c).addApi(
                LocationServices.API).build();

        try {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(final Void... params) {

                    apiClient.blockingConnect();

                    if (!apiClient.isConnected()) {
                        log("Api Client could not connect.");
                        return null;
                    }

                    final PendingIntent pendingIntent = GeofenceReceiverService.getPendingIntent(c);
                    LocationServices.GeofencingApi.removeGeofences(apiClient, pendingIntent);

                    apiClient.disconnect();
                    return null;
                }
            }.execute().get();
        } catch (InterruptedException | ExecutionException ignored) {
        }

    }

    static void log(final Object msg) {
        if (BuildConfig.DEBUG) {
            Log.d(NotificationAlarmReceiver.class.getSimpleName(), String.valueOf(msg));
        }
    }
}
