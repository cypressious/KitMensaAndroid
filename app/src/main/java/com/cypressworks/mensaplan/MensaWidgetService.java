package com.cypressworks.mensaplan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.cypressworks.mensaplan.food.Line;
import com.cypressworks.mensaplan.food.Meal;
import com.cypressworks.mensaplan.food.Plan;
import com.cypressworks.mensaplan.food.likes.LikeManager;
import com.cypressworks.mensaplan.food.likes.LikeStatus;
import com.cypressworks.mensaplan.planmanager.MensaDropdownAdapter;
import com.cypressworks.mensaplan.planmanager.PlanManager;
import com.cypressworks.mensaplan.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import androidx.core.content.ContextCompat;

/**
 * @author Kirill Rakhman
 */
public class MensaWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new MensaRemoteViewsFactory(getApplicationContext());
    }

    static class MensaRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private final Context c;
        private List<Object> items = Collections.emptyList();
        private final SharedPreferences prefs;
        private final LikeManager likeManager;

        MensaRemoteViewsFactory(final Context c) {
            this.c = c;
            this.prefs = PreferenceManager.getDefaultSharedPreferences(c);
            likeManager = new LikeManager(c);
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public void onDataSetChanged() {
            log("onDataSetChanged");
            final PlanManager manager = MensaDropdownAdapter.getManagerFromPreferences(c);

            boolean reload = false;
            if (prefs.getBoolean("reload", false)) {
                log("Requesting fresh data for widget list");
                prefs.edit().putBoolean("reload", false).apply();
                reload = true;
            }

            final Plan plan = manager.getPlan(Calendar.getInstance(), reload);

            final List<Object> newItems = new ArrayList<>();
            for (final Line line : plan) {
                newItems.add(line);
                for (final Meal meal : line) {
                    newItems.add(meal);
                }
            }

            log(newItems.size() + " items");

            this.items = newItems;
        }

        @Override
        public int getCount() {
            if (items.isEmpty()) {
                return 1;
            } else {
                return items.size();
            }
        }

        @Override
        public RemoteViews getViewAt(final int position) {
            log("Get widget view at " + position);
            final RemoteViews rv;

            if (items.isEmpty()) {
                rv = new RemoteViews(c.getPackageName(), android.R.layout.simple_list_item_1);
                rv.setTextViewText(android.R.id.text1, c.getString(R.string.no_plan_header));
            } else {

                final Object object = items.get(position);
                if (object instanceof Line) {
                    final Line line = (Line) object;

                    rv = new RemoteViews(c.getPackageName(), R.layout.widget_list_header);
                    rv.setTextViewText(R.id.list_header_title, line.getName());
                } else if (object instanceof Meal) {
                    final Meal meal = (Meal) object;

                    rv = new RemoteViews(c.getPackageName(), R.layout.widget_list_item);
                    rv.setTextViewText(R.id.textName, StringUtils.sanitize(meal.getMeal()));
                    final String dish = StringUtils.sanitize(meal.getDish());
                    if (!"".equals(dish)) {
                        rv.setTextViewText(R.id.textSubName, dish);
                        rv.setViewVisibility(R.id.textSubName, View.VISIBLE);
                    } else {
                        rv.setViewVisibility(R.id.textSubName, View.GONE);
                    }
                    rv.setTextViewText(R.id.textPrice, meal.getPrice());

                    rv.setViewVisibility(R.id.imageBio, meal.isBio() ? View.VISIBLE : View.GONE);
                    rv.setViewVisibility(R.id.imageFish, meal.isFish() ? View.VISIBLE : View.GONE);
                    rv.setViewVisibility(R.id.imagePork, meal.isPork() ? View.VISIBLE : View.GONE);
                    rv.setViewVisibility(R.id.imageCow, meal.isCow() ? View.VISIBLE : View.GONE);
                    rv.setViewVisibility(R.id.imageCow_aw,
                                         meal.isCow_aw() ? View.VISIBLE : View.GONE);
                    rv.setViewVisibility(R.id.imageVegan,
                                         meal.isVegan() ? View.VISIBLE : View.GONE);
                    rv.setViewVisibility(R.id.imageVeg, meal.isVeg() ? View.VISIBLE : View.GONE);
                    switch (likeManager.getLikeStatus(meal.getMeal())) {
                        case LikeStatus.LIKED: {
                            rv.setInt(R.id.LinearLayout1, "setBackgroundColor", ContextCompat.getColor(c, R.color.transparent_green));
                            break;
                        }
                        case LikeStatus.DISLIKED: {
                            rv.setInt(R.id.LinearLayout1, "setBackgroundColor", ContextCompat.getColor(c, R.color.transparent_red));
                            break;
                        }
                        case LikeStatus.NO_LIKE_INFO: {
                            rv.setInt(R.id.LinearLayout1, "setBackgroundColor", Color.TRANSPARENT);
                            break;
                        }
                        default:
                    }
                } else {
                    throw new AssertionError();
                }
            }

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        void log(final String msg) {
            if (BuildConfig.DEBUG) {
                Log.d(getClass().getSimpleName(), msg);
            }
        }

    }

}
