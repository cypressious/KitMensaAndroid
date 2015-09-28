package com.cypressworks.mensaplan;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.cypressworks.mensaplan.food.Line;
import com.cypressworks.mensaplan.food.Meal;
import com.cypressworks.mensaplan.food.Plan;
import com.cypressworks.mensaplan.planmanager.MensaDropdownAdapter;
import com.cypressworks.mensaplan.planmanager.PlanManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class GeofenceReceiverService extends IntentService implements
                                                           GoogleApiClient.ConnectionCallbacks {

    private static interface Ingredient {
        boolean isContainedIn(Meal m);
    }

    private static final Map<Ingredient, Integer> ingredientDrawables;

    static {
        ingredientDrawables = new HashMap<>();

        ingredientDrawables.put(Meal::isBio, R.drawable.ic_meal_bio);
        ingredientDrawables.put(Meal::isFish, R.drawable.ic_meal_fish);
        ingredientDrawables.put(Meal::isPork, R.drawable.ic_meal_pork);
        ingredientDrawables.put(Meal::isCow, R.drawable.ic_meal_cow);
        ingredientDrawables.put(Meal::isCow_aw, R.drawable.ic_meal_cow_aw);
        ingredientDrawables.put(Meal::isVegan, R.drawable.ic_meal_vegan);
        ingredientDrawables.put(Meal::isVeg, R.drawable.ic_meal_veg);
    }

    private GoogleApiClient apiClient;

    public static PendingIntent getPendingIntent(final Context c) {
        final Intent intent = new Intent(c, GeofenceReceiverService.class);
        return PendingIntent.getService(c, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    public GeofenceReceiverService() {
        super("GeofenceReceiverService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        log(intent);

        if (intent == null) {
            return;
        }

        //prevent additional alarm for stopping geofence
        NotificationAlarmReceiver.updateStopAlarm(this, false);

        //disable geofence async
        apiClient = new GoogleApiClient.Builder(this).addApi(
                LocationServices.API).addConnectionCallbacks(this).build();
        apiClient.connect();

        //display notification
        showNotification(this);
    }

    @Override
    public void onConnected(final Bundle bundle) {
        LocationServices.GeofencingApi.removeGeofences(apiClient, getPendingIntent(this));
        apiClient.disconnect();

    }

    @Override
    public void onConnectionSuspended(final int i) {

    }

    static void showNotification(final Context c) {
        final PlanManager manager = MensaDropdownAdapter.getManagerFromPreferences(c);

        final Plan plan = manager.getPlan(new GregorianCalendar(), true);

        if (plan.isEmpty()) {
            return;
        }

        final String notificationTitle = manager.getFullProviderName();

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(c);
        builder.setContentTitle(notificationTitle);
        builder.setContentText(c.getString(R.string.click_to_open));
        builder.setSmallIcon(R.drawable.ic_notify);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(PendingIntent.getActivity(c, 0, new Intent(c, MainActivity.class),
                                                           PendingIntent.FLAG_UPDATE_CURRENT));

        final Resources res = c.getResources();
        final int colorAccent = res.getColor(R.color.accent);

        final NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(notificationTitle);

        for (final Line line : plan) {

            final SpannableString lineTitle = new SpannableString(line.getName());
            lineTitle.setSpan(new ForegroundColorSpan(colorAccent), 0, lineTitle.length(),
                              Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            inboxStyle.addLine(lineTitle);

            for (final Meal meal : line) {

                inboxStyle.addLine(meal.getName());
            }
        }

        builder.setStyle(inboxStyle);

        final Notification notification = builder.build();

        final NotificationManager notificationManager = (NotificationManager) c.getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

    }

    void log(final Object msg) {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), String.valueOf(msg));
        }
    }
}
