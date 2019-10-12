package com.cypressworks.mensaplan;

import android.annotation.SuppressLint;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ListView;

import com.cypressworks.mensaplan.planmanager.MensaDropdownAdapter;
import com.cypressworks.mensaplan.planmanager.PlanManager;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.File;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.PagerTitleStrip;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

/**
 * @author Kirill Rakhman
 */
public class MainActivity extends AppCompatActivity implements ScrollListener {

    public static final String PREF_MENSA_NUM = "pref_mensa_num";
    public static final String PACKAGE_WEAR = "com.google.android.wearable.app";

    private MensaDropdownAdapter mensaAdapter;
    private ViewPager viewPager;
    private ListView listDrawer;
    private DrawerLayout drawerLayout;

    private Calendar today = Calendar.getInstance();
    private int currentPagerPosition;
    private SharedPreferences prefs;
    private ActionBarDrawerToggle mDrawerToggle;

    @SuppressLint({"NewApi", "CommitPrefEdits"})
    @Override
    protected void onCreate(final Bundle arg0) {
        super.onCreate(arg0);

        mensaAdapter = new MensaDropdownAdapter(this);
        currentPagerPosition = PlanFragmentPagerAdapter.getDefaultPosition();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("dayChanged", false).apply();

        makeStartUpCleaning();

        setContentView(R.layout.main_new);

        viewPager = Views.findViewById(this, R.id.pager);
        viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.pager_margin));
        viewPager.addOnPageChangeListener(onPageChangeListener);

        drawerLayout = Views.findViewById(this, R.id.drawer_layout);
        listDrawer = Views.findViewById(this, R.id.left_drawer);
        prepareDrawer();

        final PagerTitleStrip titleStrip = Views.findViewById(this, R.id.titleStrip);
        titleStrip.setTextColor(Color.WHITE);

        prepareActionBar();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setTaskDescription();
        }

        if (!prefs.getBoolean("backed_up", false) && HappyCowActivity.getCollectedFile(
                this).exists()) {
            prefs.edit().putBoolean("backed_up", true).apply();
            new BackupManager(this).dataChanged();
        }
    }

    private void setTaskDescription() {
        final TypedValue typedValue = new TypedValue();
        final Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        final int color = typedValue.data;

        AndroidV21Helper.setTaskDescription(this, null, R.drawable.icon_white, color);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("dayChanged", false)) {
            prefs.edit().putBoolean("dayChanged", true).apply();

            today = Calendar.getInstance();
            currentPagerPosition = PlanFragmentPagerAdapter.getDefaultPosition();

            final int defaultItem = PreferenceManager.getDefaultSharedPreferences(this).getInt(
                    PREF_MENSA_NUM, 0);
            onMensaSelected(defaultItem);
        }
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawers();
            return;
        }

        super.onBackPressed();
    }

    private void makeStartUpCleaning() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final int lastClean = prefs.getInt("lastClean", -1);
        final int version = getVersionNumber();

        if (lastClean < version) {
            prefs.edit().putInt("lastClean", version).apply();

            final File cacheDir = getCacheDir();

            for (final File file : cacheDir.listFiles()) {
                if (file.isDirectory()) {
                    Tools.deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        } else {
            for (int i = 0; i < mensaAdapter.getCount(); i++) {
                PlanManager.getInstance(mensaAdapter.getItem(i), this).clearCache(8);
            }
        }

        //        final boolean notificationDialogShown = prefs.getBoolean("notification_dialog_shown",
        //                                                                 false);
        //
        //        if (!notificationDialogShown) {
        //            prefs.edit().putBoolean("notification_dialog_shown", true).apply();
        //
        //            final List<ApplicationInfo> installedApplications = getPackageManager().getInstalledApplications(
        //                    0);
        //
        //            for (final ApplicationInfo info : installedApplications) {
        //                if (info.packageName.equals(PACKAGE_WEAR)) {
        //                    new AlertDialog.Builder(this) //
        //                            .setTitle(R.string.notification_question_title) //
        //                            .setMessage(R.string.notification_question) //
        //                            .setPositiveButton(android.R.string.yes,
        //                                               (dialog, which) -> startActivity(new Intent(this,
        //                                                                                           NotificationSettingsActivity.class))) //
        //                            .setNegativeButton(android.R.string.cancel, null) //
        //                            .show();
        //                    return;
        //                }
        //            }
        //        }

    }

    private int getVersionNumber() {
        try {
            final PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (final NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private final OnPageChangeListener onPageChangeListener = new OnPageChangeAdapter() {
        @Override
        public void onPageSelected(final int position) {
            currentPagerPosition = position;
            log("Page changed: " + currentPagerPosition);
        }
    };

    private void prepareActionBar() {
        final ActionBar actionBar = getSupportActionBar();

        actionBar.setElevation(0);

    }

    private void prepareDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer,
                                                  R.string.close_drawer) {
            public void onDrawerClosed(final View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(final View drawerView) {
                super.onDrawerOpened(drawerView);
            }

        };

        drawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final int defaultItem = PreferenceManager.getDefaultSharedPreferences(this).getInt(
                PREF_MENSA_NUM, 0);

        listDrawer.setAdapter(mensaAdapter);
        listDrawer.setOnItemClickListener((parent, view, position, id) -> {
            onMensaSelected(position);
            drawerLayout.closeDrawers();
        });

        onMensaSelected(defaultItem);
    }

    private void onMensaSelected(final int itemPosition) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(PREF_MENSA_NUM,
                                                                          itemPosition).apply();
        new BackupManager(this).dataChanged();

        final Class<? extends PlanManager> managerClass = mensaAdapter.getItem(itemPosition);

        final PlanFragmentPagerAdapter pagerAdapter = new PlanFragmentPagerAdapter(this,
                                                                                   getSupportFragmentManager(),
                                                                                   today,
                                                                                   managerClass);

        viewPager.setAdapter(pagerAdapter);
        viewPager.removeOnPageChangeListener(onPageChangeListener);
        viewPager.setCurrentItem(currentPagerPosition);
        log("Setting current item: " + currentPagerPosition);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        MensaWidgetProvider.updateWidgets(this);

        getSupportActionBar().setTitle(mensaAdapter.getName(itemPosition));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsmenu_new, menu);

        return true;
    }

    @Override
    public void supportInvalidateOptionsMenu() {
        // unschöner Hack, aber scheinbar verhindert das, dass beim Start das
        // "today" action item fehlt
        viewPager.post(MainActivity.super::supportInvalidateOptionsMenu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {

            case R.id.webcam:
                final Intent webcamActivity = new Intent(getBaseContext(), WebCamActivity.class);
                startActivity(webcamActivity);
                return true;
            case R.id.today:
                scrollToToday();
                return true;
        }

        return false;
    }

    private void scrollToToday() {
        final int todayPosition = PlanFragmentPagerAdapter.getDefaultPosition();

        if (viewPager.getCurrentItem() != todayPosition) {
            viewPager.setCurrentItem(todayPosition);
        } else {

            final View view = viewPager.getChildAt(1);

            final ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationX", 0, dp2px(0),
                                                               dp2px(10), dp2px(0), dp2px(-10),
                                                               dp2px(0), dp2px(2),
                                                               dp2px(0)).setDuration(300);

            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.start();
        }
    }

    @Override
    public void onScrolled(final int yScroll) {
        //        if (VERSION.SDK_INT >= 21) {
        //            float elevation = titleStripMaxElevation * Math.min(1f,
        //                                                                yScroll / titleStripMaxElevationScroll);
        //            AndroidV21Helper.setElevation(titleStrip, elevation);
        //        }
    }

    private float dp2px(final float dp) {
        final Resources r = getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    void log(final String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), msg);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {
        // Verhinden, dass der ViewPager seine Position speichern. Wir wollen
        // immer bei heute beginnen.
    }

}
