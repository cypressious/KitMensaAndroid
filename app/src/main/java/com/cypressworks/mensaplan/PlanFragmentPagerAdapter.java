package com.cypressworks.mensaplan;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;

import com.cypressworks.mensaplan.food.PlanFragment;
import com.cypressworks.mensaplan.planmanager.PlanManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * @author Kirill Rakhman
 */
class PlanFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private static final SimpleDateFormat weekDayDateFormat = new SimpleDateFormat("E (dd.MM.)",
                                                                                   Locale.GERMANY);
    private static final SimpleDateFormat weekDayFormat = new SimpleDateFormat("E", Locale.GERMANY);

    private final Calendar startDate;
    private final Class<? extends PlanManager> managerClass;

    private static final int daysInFuture = 7;
    private static final int daysInPast = 7;

    private String[] names;

    public PlanFragmentPagerAdapter(
            final Context context, final FragmentManager fm, final Calendar startDate,
            final Class<? extends PlanManager> managerClass) {
        super(fm);

        this.startDate = startDate;
        this.managerClass = managerClass;

        generateTitles(context);
    }

    @NonNull
    @Override
    public Fragment getItem(final int position) {

        final Calendar cal = getDateAtPosition(position);
        return PlanFragment.getInstance(cal, managerClass);
    }

    @Override
    public int getCount() {
        return daysInPast + 1 + daysInFuture;
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return names[position];
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    Calendar getDateAtPosition(final int position) {
        // relative position zu Mitte bestimmen
        final int relativeToToday = position - daysInPast;

        // Calendar Instanz für diesen Tag
        final Calendar cal = (Calendar) startDate.clone();
        cal.add(Calendar.DAY_OF_MONTH, relativeToToday);
        return cal;
    }

    public static int getDefaultPosition() {
        return daysInPast;
    }

    private void generateTitles(final Context context) {
        final int count = getCount();
        this.names = new String[count];

        final Calendar today = Calendar.getInstance();

        for (int i = 0; i < count; i++) {
            final Calendar dateAtPosiion = getDateAtPosition(i);

            if (isToday(dateAtPosiion, today)) {
                names[i] = weekDayFormat.format(dateAtPosiion.getTime()) + " (" + context.getString(
                        R.string.date_today) + ")";
            } else {
                names[i] = weekDayDateFormat.format(dateAtPosiion.getTime());
            }
        }
    }

    private static boolean isToday(final Calendar cal, final Calendar today) {

        return (cal.get(Calendar.ERA) == today.get(Calendar.ERA) //
                && cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) //
                && cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR));
    }

    protected void log(final String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), msg);
        }
    }
}
