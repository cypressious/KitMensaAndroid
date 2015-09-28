package com.cypressworks.mensaplan;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cypressworks.mensaplan.planmanager.AdenauerPlanManager;
import com.cypressworks.mensaplan.planmanager.ErzbergerPlanManager;
import com.cypressworks.mensaplan.planmanager.GottesauePlanManager;
import com.cypressworks.mensaplan.planmanager.HolzgartenstrPlanManager;
import com.cypressworks.mensaplan.planmanager.MoltkePlanManager;
import com.cypressworks.mensaplan.planmanager.PforzheimPlanManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.GregorianCalendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by Kirill on 29.10.2014.
 */
public class NotificationSettingsActivity extends ActionBarActivity {

    private static final LatLng[] MENSA_LOCATIONS = { //
            new LatLng(49.011874, 8.416812), //Adenauer
            new LatLng(49.015832, 8.389612), //Moltke
            new LatLng(49.026619, 8.385768), //Erzberger
            new LatLng(49.004390, 8.427167), //Gottesaue
            new LatLng(48.878733, 8.717201), //Pforzheim
            new LatLng(48.887772, 8.707701), //Holzgartenstr
    };

    private static final String[] MENSA_NAMES = { //
            AdenauerPlanManager.fullProviderName, //
            MoltkePlanManager.fullProviderName, //
            ErzbergerPlanManager.fullProviderName, //
            GottesauePlanManager.fullProviderName, //
            PforzheimPlanManager.fullProviderName, //
            HolzgartenstrPlanManager.fullProviderName //
    };

    private static final float DEFAULT_ZOOM = 14.5f;

    @InjectView(R.id.switch_enabled) SwitchCompat switchEnabled;
    @InjectView(R.id.buttonStart) Button buttonStart;
    @InjectView(R.id.buttonEnd) Button buttonEnd;
    @InjectView(R.id.textViewRadius) TextView textViewRadius;

    private SharedPreferences prefs;
    private GoogleMap map;

    private int selectedMensa;
    private NotificationSetting setting;
    private Circle circle;
    private java.text.DateFormat timeFormatter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timeFormatter = DateFormat.getTimeFormat(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        selectedMensa = prefs.getInt(MainActivity.PREF_MENSA_NUM, 0);
        setting = NotificationSetting.fromPrefs(this);

        setContentView(R.layout.activity_notification_settings);
        ButterKnife.inject(this);

        final SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(
                R.id.mapFragment);
        map = mapFrag.getMap();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        log("Map: " + map);

        if (map == null) {
            handleMapError();
        }

        setupMap();
        setViewValuesAndSave();

        final boolean notificationDialogShown = prefs.getBoolean("notification_tutorial_shown",
                                                                 false);

        if (!notificationDialogShown) {
            prefs.edit().putBoolean("notification_tutorial_shown", true).apply();

            showTutorial();
        }

    }

    private void showTutorial() {
        new AlertDialog.Builder(this) //
                .setTitle(R.string.notifications) //
                .setMessage(R.string.notification_tutorial) //
                .setPositiveButton(android.R.string.yes, null) //
                .show();
    }

    private void setupMap() {
        //set mensa position markers
        for (int i = 0; i < MENSA_LOCATIONS.length; i++) {
            final LatLng mensaLocation = MENSA_LOCATIONS[i];
            final String name = MENSA_NAMES[i];
            map.addMarker(new MarkerOptions().position(mensaLocation).draggable(false).title(name));
        }

        //set initial position
        LatLng initialPos = setting.getNotifCenter();

        if (initialPos.latitude == 0 && initialPos.longitude == 0) {
            initialPos = MENSA_LOCATIONS[selectedMensa];
        }

        final CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(initialPos, DEFAULT_ZOOM));
        map.moveCamera(cameraUpdate);

        //set circle
        final float notifRadius = setting.getNotifRadius();

        final Resources res = getResources();
        final CircleOptions circleOptions = new CircleOptions()//
                .center(initialPos)//
                .radius(notifRadius)//
                .fillColor(res.getColor(R.color.transparent_blue))//
                .strokeColor(res.getColor(R.color.blue))//
                .strokeWidth(res.getDimension(R.dimen.circle_stroke));//

        circle = map.addCircle(circleOptions);

        saveSetting();

        //add listener
        map.setOnMapClickListener(latLng -> {
            circle.setCenter(latLng);
            saveSetting();
        });

        map.setOnMarkerClickListener(marker -> {
            circle.setCenter(marker.getPosition());
            saveSetting();
            return false;
        });
    }

    private void setViewValuesAndSave() {
        switchEnabled.setChecked(
                prefs.getBoolean(NotificationSetting.PREF_NOTIFICATION_ENABLED, false));
        buttonStart.setText(timeFormatter.format(
                new GregorianCalendar(2000, 1, 1, setting.getNotifStartHour(),
                                      setting.getNotifStartMinute(), 0).getTime()));
        buttonEnd.setText(timeFormatter.format(
                new GregorianCalendar(2000, 1, 1, setting.getNotifEndHour(),
                                      setting.getNotifEndMinute(), 0).getTime()));
        textViewRadius.setText(getString(R.string.notification_radius, circle.getRadius()));

        saveSetting();
    }

    private void updateAlarm() {
        NotificationAlarmReceiver.updateStartAlarm(this, prefs.getBoolean(
                NotificationSetting.PREF_NOTIFICATION_ENABLED, false));
    }

    @OnCheckedChanged(R.id.switch_enabled)
    void onSwitchCheckedChanged(final boolean checked) {
        log("onSwitchCheckedChanged");
        if (checked) {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(
                    this) != ConnectionResult.SUCCESS) {

                Toast.makeText(this, R.string.google_play_services_not_installed,
                               Toast.LENGTH_SHORT).show();
                switchEnabled.setChecked(false);
                return;
            }
        }

        prefs.edit().putBoolean(NotificationSetting.PREF_NOTIFICATION_ENABLED, checked).apply();
        NotificationAlarmReceiver.updateStartAlarm(this, checked);
    }

    @OnClick(R.id.buttonStart)
    void onClickStartTime(final Button button) {
        final int endHour = setting.getNotifEndHour();
        final int endMinute = setting.getNotifEndMinute();

        final RangeTimePickerDialog pickerDialog = new RangeTimePickerDialog(this, (
                view, newHour, newMinute) -> {
            if (newHour > endHour || (newHour == endHour && newMinute >= endMinute)) {
                return;
            }

            setting.setNotifStartHour(newHour);
            setting.setNotifStartMinute(newMinute);
            setViewValuesAndSave();

        }, setting.getNotifStartHour(), setting.getNotifStartMinute(), true);

        pickerDialog.setMax(endHour, endMinute);
        pickerDialog.show();
    }

    @OnClick(R.id.buttonEnd)
    void onClickEndTime(final Button button) {
        final int startHour = setting.getNotifStartHour();
        final int startMinute = setting.getNotifStartMinute();

        final RangeTimePickerDialog pickerDialog = new RangeTimePickerDialog(this, (
                view, newHour, newMinute) -> {
            if (newHour < startHour || (newHour == startHour && newMinute <= startMinute)) {
                return;
            }

            setting.setNotifEndHour(newHour);
            setting.setNotifEndMinute(newMinute);
            setViewValuesAndSave();

        }, setting.getNotifEndHour(), setting.getNotifEndMinute(), true);

        pickerDialog.setMin(startHour, startMinute);
        pickerDialog.show();
    }

    @OnClick({R.id.buttonRadiusIncrease, R.id.buttonRadiusDecrease})
    void onClickRadiusChange(final Button button) {
        float newRadius = (float) circle.getRadius();

        if (button.getId() == R.id.buttonRadiusDecrease) {
            newRadius = Math.max(100, newRadius - 100);
        } else {
            newRadius += 100;
        }

        setting.setNotifRadius(newRadius);
        setViewValuesAndSave();

        circle.setRadius(newRadius);

    }

    private void saveSetting() {

        setting.setNotifCenter(circle.getCenter());
        setting.setNotifRadius((float) circle.getRadius());
        setting.setNotifMensa(selectedMensa);

        setting.saveToPrefs(this);

        updateAlarm();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsmenu_notifications, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.optionTest:
                new Thread(() -> GeofenceReceiverService.showNotification(
                        NotificationSettingsActivity.this)).start();
                return true;
            case R.id.optionTut:
                showTutorial();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void handleMapError() {

    }

    void log(final Object msg) {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), String.valueOf(msg));
        }
    }
}
