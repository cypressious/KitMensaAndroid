package com.cypressworks.mensaplan.planmanager;

import android.content.Context;
import android.util.Log;

import com.cypressworks.mensaplan.BuildConfig;
import com.cypressworks.mensaplan.Tools;
import com.cypressworks.mensaplan.food.Plan;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Kirill Rakhman
 */
public abstract class PlanManager {

    private interface PlanManagerFactory {
        PlanManager create(Context c);
    }

    private static final Map<Class<? extends PlanManager>, PlanManager> managerCache = new HashMap<>();
    private static final Map<Class<? extends PlanManager>, PlanManagerFactory> managerFactories = new HashMap<>();

    static {
        managerFactories.put(AdenauerPlanManager.class, AdenauerPlanManager::new);
        managerFactories.put(MoltkePlanManager.class, MoltkePlanManager::new);
        managerFactories.put(ErzbergerPlanManager.class, ErzbergerPlanManager::new);
        managerFactories.put(GottesauePlanManager.class, GottesauePlanManager::new);
        managerFactories.put(PforzheimPlanManager.class, PforzheimPlanManager::new);
        managerFactories.put(HolzgartenstrPlanManager.class, HolzgartenstrPlanManager::new);
    }

    public static synchronized PlanManager getInstance(
            final Class<? extends PlanManager> clazz, final Context c) {
        if (!managerCache.containsKey(clazz)) {
            final PlanManager manager = managerFactories.get(clazz).create(c);
            managerCache.put(clazz, manager);
        }
        return managerCache.get(clazz);

    }

    public enum RelativeDate {
        PAST, TODAY, FUTURE
    }

    private static final SimpleDateFormat dayMonthYearFormat = new SimpleDateFormat("dd.MM.yyyy",
                                                                                    Locale.GERMANY);

    static RelativeDate getRelativeDate(final Calendar date) {
        final Calendar today = Calendar.getInstance();
        if (date.after(today)) {
            // ############## Schauen, ob in der Zukunft
            return RelativeDate.FUTURE;

        } else if (date.get(Calendar.ERA) == today.get(Calendar.ERA) && date.get(
                Calendar.YEAR) == today.get(Calendar.YEAR) && date.get(
                Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            // ################# Schauen, ob heute
            return RelativeDate.TODAY;

        } else {
            // ################## von AKK laden
            return RelativeDate.PAST;

        }
    }

    private final Context c;

    PlanManager(final Context c) {
        this.c = c;
    }

    public synchronized Plan getPlan(final Calendar date, final boolean reload) {
        if (!reload && existsInCache(date)) {
            // Wenn im Cache, dann einfach lesen und zurückgeben
            final Plan planFromCache = getPlanFromCache(date);

            if (planFromCache == null || planFromCache.getAgeInDays() >= 1) {
                log("Plan not cached or too old for " + dayMonthYearFormat.format(date.getTime()));
                return downloadPlan(date);
            } else {
                return planFromCache;
            }
        } else {
            // Existiert nicht im Cache oder reload => downloaden
            log("(Re)loading plan for " + dayMonthYearFormat.format(date.getTime()));
            return downloadPlan(date);
        }

    }

    public Plan getPlanFromCache(final Calendar date) {
        final File file = getCacheFile(date);

        if (!file.exists()) {
            return null;
        }

        try {
            return Tools.readObject(file);
        } catch (final Exception e) {
            e.printStackTrace();
            file.delete();
            return null;
        }

    }

    final Plan cachePlan(final Plan plan, final Calendar date) {

        try {
            final File target = getCacheFile(date);
            Tools.writeObject(plan, target);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return plan;
    }

    public final void clearCache(final int leaveDays) {
        // TODO clean cache
        // Referenzdatum anlegen
        Calendar cref = Calendar.getInstance(Locale.GERMANY);
        cref.add(Calendar.DAY_OF_YEAR, -leaveDays);
        cref.set(Calendar.HOUR_OF_DAY, 23);
        cref.set(Calendar.MINUTE, 59);
        cref.set(Calendar.SECOND, 59);
        final Date dref1 = cref.getTime();

        cref = Calendar.getInstance(Locale.GERMANY);
        cref.add(Calendar.DAY_OF_YEAR, leaveDays);
        cref.set(Calendar.HOUR_OF_DAY, 0);
        cref.set(Calendar.MINUTE, 0);
        cref.set(Calendar.SECOND, 0);
        final Date dref2 = cref.getTime();

        final File[] listFiles = getCacheDir().listFiles();
        for (final File f : listFiles) {
            try {
                // Datum der Datei parsen
                final Date d = dayMonthYearFormat.parse(f.getName());

                // Vergleichen und ggf. löschen
                if (d.before(dref1) || d.after(dref2)) {
                    f.delete();
                }

            } catch (final java.text.ParseException e) {
                e.printStackTrace();
                f.delete();
            }
        }
    }

    boolean existsInCache(final Calendar date) {
        final File target = getCacheFile(date);
        return target.exists();
    }

    private File getCacheDir() {
        final File dir = new File(this.c.getCacheDir(), getProviderName());
        if (dir.exists() && dir.isFile()) {
            dir.delete();
        }
        dir.mkdirs();
        return dir;
    }

    private File getCacheFile(final Calendar date) {
        final String fname = dayMonthYearFormat.format(date.getTime());
        return new File(getCacheDir(), fname);
    }

    abstract protected Plan downloadPlan(final Calendar date);

    abstract public String getFullProviderName();

    abstract public String getProviderName();

    void log(final String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), msg);
        }
    }
}
