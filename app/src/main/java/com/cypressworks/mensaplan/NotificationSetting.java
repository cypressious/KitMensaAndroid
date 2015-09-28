package com.cypressworks.mensaplan;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Kirill on 29.10.2014.
 */
public class NotificationSetting {
    public static final String PREF_NOTIFICATION_MENSA = "notif_mensa";
    public static final String PREF_NOTIFICATION_CENTER_LAT = "notif_center_lat";
    public static final String PREF_NOTIFICATION_CENTER_LNG = "notif_center_lng";
    public static final String PREF_NOTIFICATION_RADIUS = "notif_radius";
    public static final String PREF_NOTIFICATION_START_HOUR = "notif_start_hour";
    public static final String PREF_NOTIFICATION_START_MINUTE = "notif_start_minute";
    public static final String PREF_NOTIFICATION_END_HOUR = "notif_end_hour";
    public static final String PREF_NOTIFICATION_END_MINUTE = "notif_end_minute";

    public static final String PREF_NOTIFICATION_ENABLED = "notif_enabled";

    private static final float RADIUS_DEFAULT = 900; //in meters

    public static NotificationSetting fromPrefs(final Context c) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);

        final int notif_mensa = prefs.getInt(PREF_NOTIFICATION_MENSA, 0);
        final float notif_center_lat = prefs.getFloat(PREF_NOTIFICATION_CENTER_LAT, 0);
        final float notif_center_lng = prefs.getFloat(PREF_NOTIFICATION_CENTER_LNG, 0);
        final float notif_radius = prefs.getFloat(PREF_NOTIFICATION_RADIUS, RADIUS_DEFAULT);
        final int notif_start_hour = prefs.getInt(PREF_NOTIFICATION_START_HOUR, 11);
        final int notif_start_minute = prefs.getInt(PREF_NOTIFICATION_START_MINUTE, 0);
        final int notif_end_hour = prefs.getInt(PREF_NOTIFICATION_END_HOUR, 14);
        final int notif_end_minute = prefs.getInt(PREF_NOTIFICATION_END_MINUTE, 0);

        return new NotificationSetting(notif_mensa, new LatLng(notif_center_lat, notif_center_lng),
                                       notif_radius, notif_start_hour, notif_start_minute,
                                       notif_end_hour, notif_end_minute);
    }

    private int notifMensa;
    private LatLng notifCenter;
    private float notifRadius;

    private int notifStartHour;
    private int notifStartMinute;
    private int notifEndHour;
    private int notifEndMinute;

    public NotificationSetting(
            final int notifMensa, final LatLng notifCenter, final float notifRadius,
            final int notifStartHour, final int notifStartMinute, final int notifEndHour,
            final int notifEndMinute) {
        this.notifMensa = notifMensa;
        this.notifCenter = notifCenter;
        this.notifRadius = notifRadius;
        this.notifStartHour = notifStartHour;
        this.notifStartMinute = notifStartMinute;
        this.notifEndHour = notifEndHour;
        this.notifEndMinute = notifEndMinute;
    }

    public void saveToPrefs(final Context c) {

        Log.d(getClass().getSimpleName(), "Saving setting " + this);

        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                c).edit();

        editor.putInt(PREF_NOTIFICATION_MENSA, notifMensa);
        editor.putFloat(PREF_NOTIFICATION_CENTER_LAT, (float) notifCenter.latitude);
        editor.putFloat(PREF_NOTIFICATION_CENTER_LNG, (float) notifCenter.longitude);
        editor.putFloat(PREF_NOTIFICATION_RADIUS, notifRadius);
        editor.putInt(PREF_NOTIFICATION_START_HOUR, notifStartHour);
        editor.putInt(PREF_NOTIFICATION_START_MINUTE, notifStartMinute);
        editor.putInt(PREF_NOTIFICATION_END_HOUR, notifEndHour);
        editor.putInt(PREF_NOTIFICATION_END_MINUTE, notifEndMinute);

        editor.apply();
    }

    public int getNotifMensa() {
        return notifMensa;
    }

    public void setNotifMensa(final int notifMensa) {
        this.notifMensa = notifMensa;
    }

    public LatLng getNotifCenter() {
        return notifCenter;
    }

    public void setNotifCenter(final LatLng notifCenter) {
        this.notifCenter = notifCenter;
    }

    public float getNotifRadius() {
        return notifRadius;
    }

    public void setNotifRadius(final float notifRadius) {
        this.notifRadius = notifRadius;
    }

    public int getNotifStartHour() {
        return notifStartHour;
    }

    public void setNotifStartHour(final int notifStartHour) {
        this.notifStartHour = notifStartHour;
    }

    public int getNotifStartMinute() {
        return notifStartMinute;
    }

    public void setNotifStartMinute(final int notifStartMinute) {
        this.notifStartMinute = notifStartMinute;
    }

    public int getNotifEndHour() {
        return notifEndHour;
    }

    public void setNotifEndHour(final int notifEndHour) {
        this.notifEndHour = notifEndHour;
    }

    public int getNotifEndMinute() {
        return notifEndMinute;
    }

    public void setNotifEndMinute(final int notifEndMinute) {
        this.notifEndMinute = notifEndMinute;
    }

    @Override
    public String toString() {
        return "NotificationSetting{" +
                "notifMensa=" + notifMensa +
                ", notifCenter=" + notifCenter +
                ", notifRadius=" + notifRadius +
                ", notifStartHour=" + notifStartHour +
                ", notifStartMinute=" + notifStartMinute +
                ", notifEndHour=" + notifEndHour +
                ", notifEndMinute=" + notifEndMinute +
                '}';
    }
}

