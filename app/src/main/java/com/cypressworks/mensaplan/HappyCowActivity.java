package com.cypressworks.mensaplan;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.cypressworks.mensaplan.planmanager.PlanManager;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;

import androidx.annotation.NonNull;

/**
 * @author Kirill Rakhman
 */
public class HappyCowActivity extends Activity {

    public static final String FILE_NAME_COLLECTED = "collected_cows";

    @NonNull
    public static File getCollectedFile(final Context c) {
        return new File(c.getFilesDir(), FILE_NAME_COLLECTED);
    }

    public static final String EXTRA_MENSA_CLASS = "EXTRA_MENSA_CLASS";
    public static final String EXTRA_DAY = "EXTRA_DAY";
    public static final String EXTRA_ITEM = "EXTRA_ITEM";

    private static final Interpolator sDecelerator = new DecelerateInterpolator();

    private File collectedFile;
    private HashSet<CowHolder> collected;
    private Resources res;

    private TextView gratulations, total;
    private View layout;
    private View background;

    private String PACKAGE_NAME;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PACKAGE_NAME = getPackageName();
        res = getResources();

        setContentView(R.layout.happy_cows_layout);
        gratulations = Views.findViewById(this, R.id.textViewGratulations);
        total = Views.findViewById(this, R.id.textViewTotal);

        layout = findViewById(R.id.layoutHappyCowDialog);
        background = findViewById(R.id.layoutBackground);
        background.setOnClickListener(v -> animateOut());

        collectedFile = getCollectedFile(this);
        if (collectedFile.exists()) {
            try {
                collected = Tools.readObject(collectedFile);
            } catch (final Exception e) {
                collected = new HashSet<>();
            }
        } else {
            collected = new HashSet<>();
        }

        final Intent intent = getIntent();

        if (intent != null) {
            final Bundle extras = intent.getExtras();

            if (extras != null) {
                final Class<? extends PlanManager> planClass = (Class<? extends PlanManager>) extras.getSerializable(
                        EXTRA_MENSA_CLASS);
                final Calendar date = (Calendar) extras.getSerializable(EXTRA_DAY);
                final int item = extras.getInt(EXTRA_ITEM);

                if (planClass != null && date != null) {
                    checkCow(planClass, date, item);
                }

                if (savedInstanceState == null) {
                    animateIn(extras);
                }
            }
        }

        setCollectedCount();

    }

    private void animateIn(final Bundle extras) {
        final int thumbnailTop = extras.getInt(PACKAGE_NAME + ".top");
        final int thumbnailLeft = extras.getInt(PACKAGE_NAME + ".left");
        final int thumbnailWidth = extras.getInt(PACKAGE_NAME + ".width");
        final int thumbnailHeight = extras.getInt(PACKAGE_NAME + ".height");

        final ViewTreeObserver observer = layout.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                layout.getViewTreeObserver().removeOnPreDrawListener(this);

                final int[] screenLocation = new int[2];
                layout.getLocationOnScreen(screenLocation);
                final int mLeftDelta = thumbnailLeft - screenLocation[0];
                final int mTopDelta = thumbnailTop - screenLocation[1];

                // Scale factors to make the large version the same
                // size as the
                // thumbnail
                final float mWidthScale = (float) thumbnailWidth / layout.getWidth();
                final float mHeightScale = (float) thumbnailHeight / layout.getHeight();

                ViewHelper.setPivotX(layout, 0);
                ViewHelper.setPivotY(layout, 0);
                ViewHelper.setScaleX(layout, mWidthScale);
                ViewHelper.setScaleY(layout, mHeightScale);
                ViewHelper.setTranslationX(layout, mLeftDelta);
                ViewHelper.setTranslationY(layout, mTopDelta);

                // Animate scale and translation to go from
                // thumbnail to full
                // size
                ViewPropertyAnimator.animate(layout).scaleX(1).scaleY(1).translationX(
                        0).translationY(0).setInterpolator(sDecelerator).start();

                final TransitionDrawable backDrawable = new TransitionDrawable(
                        new Drawable[]{new ColorDrawable(
                                Color.parseColor("#00000000")), new ColorDrawable(
                                Color.parseColor("#99000000"))});
                background.setBackgroundDrawable(backDrawable);
                backDrawable.startTransition(300);

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        animateOut();
    }

    private void animateOut() {
        final ViewPropertyAnimator anim = ViewPropertyAnimator.animate(background).alpha(0f);
        anim.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                finish();
            }
        });
        anim.start();
    }

    @Override
    public void finish() {
        super.finish();

        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }

    private void setCollectedCount() {
        final int num = collected.size();
        final String text = res.getQuantityString(R.plurals.happy_cow_total, num, num);
        final String numString = String.valueOf(num);
        final int numberPos = text.indexOf(numString);

        final Spannable spanned = new SpannableString(text);
        spanned.setSpan(new ForegroundColorSpan(Color.parseColor("#cc0000")), numberPos,
                        numberPos + numString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        total.setText(spanned);
    }

    private void checkCow(
            final Class<? extends PlanManager> planClass, final Calendar date, final int item) {

        final CowHolder holder = new CowHolder();
        holder.mensa = PlanManager.getInstance(planClass, this).getProviderName();
        holder.day = date.get(Calendar.DAY_OF_YEAR);
        holder.year = date.get(Calendar.YEAR);
        holder.item = item;

        if (!collected.contains(holder)) {
            collected.add(holder);
            try {
                Tools.writeObject(collected, collectedFile);
                new BackupManager(this).dataChanged();
            } catch (final IOException e) {
                e.printStackTrace();
            }

            gratulations.setText(R.string.happy_cow_gratulations);
        } else {
            gratulations.setText(R.string.happy_cow_already_found);
        }

        gratulations.setVisibility(View.VISIBLE);
    }

    private static class CowHolder implements Serializable {
        private static final long serialVersionUID = 2210140636911080441L;

        String mensa;
        int year, day, item;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + day;
            result = prime * result + item;
            result = prime * result + ((mensa == null) ? 0 : mensa.hashCode());
            result = prime * result + year;
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof CowHolder)) {
                return false;
            }
            final CowHolder other = (CowHolder) obj;
            if (day != other.day) {
                return false;
            }
            if (item != other.item) {
                return false;
            }
            if (mensa == null) {
                if (other.mensa != null) {
                    return false;
                }
            } else if (!mensa.equals(other.mensa)) {
                return false;
            }
            return year == other.year;
        }

    }
}
