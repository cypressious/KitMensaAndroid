package com.cypressworks.mensaplan.food;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.cypressworks.mensaplan.BuildConfig;
import com.cypressworks.mensaplan.HappyCowActivity;
import com.cypressworks.mensaplan.R;
import com.cypressworks.mensaplan.ScrollListener;
import com.cypressworks.mensaplan.planmanager.PlanManager;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

/**
 * @author Kirill Rakhman
 */
public class PlanFragment extends ListFragment implements OnItemClickListener {

    private boolean isVisibleToUser;

    public static PlanFragment getInstance(
            final Calendar cal, final Class<? extends PlanManager> managerClass) {
        final PlanFragment planFragment = new PlanFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable("cal", cal);
        bundle.putSerializable("managerClass", managerClass);

        planFragment.setArguments(bundle);
        return planFragment;
    }

    private static Fragment contextMenuFrag;
    private Class<? extends PlanManager> managerClass;
    private Calendar date;
    private PlanManager planManager;
    private MenuItem menuReload;

    private ListView listView;
    private ScrollListener scrollListener;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            @NonNull final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);

        if (VERSION.SDK_INT < 21) {
            inflater.inflate(R.layout.view_shadow, view);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.optionsmenu_frag, menu);
        menuReload = menu.findItem(R.id.reload);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.reload) {
            new PopulateListTask(getActivity(), date, true).execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(@NonNull final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Context c = requireActivity();

        listView = getListView();
        //		listView.setSelector(R.drawable.list_selector);
        listView.setDivider(null);
        listView.setDividerHeight(0);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view, final int scrollState) {
            }

            @Override
            public void onScroll(
                    final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
                    final int totalItemCount) {
                if (scrollListener != null) {
                    notifyScrollListener();
                }
            }
        });

        setEmptyText(c.getString(R.string.no_plan_header));

        registerForContextMenu(listView);

        final Bundle args = getArguments();
        date = (Calendar) args.getSerializable("cal");
        managerClass = (Class<? extends PlanManager>) args.getSerializable("managerClass");
        planManager = PlanManager.getInstance(managerClass, c);

        getListView().setOnItemClickListener(this);
        new PopulateListTask(c, date, false).execute();
    }

    @Override
    public void onCreateContextMenu(
            @NonNull final ContextMenu menu, @NonNull final View v, final ContextMenuInfo menuInfo) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        // Gericht, auf das geklickt wurde
        final Object itemAtPosition = getListView().getItemAtPosition(info.position);

        if (!(itemAtPosition instanceof Meal)) {
            return;
        }

        final Meal m = (Meal) itemAtPosition;

        // Contextmenü aus xml
        final android.view.MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.mealcontextmenu, menu);

        // Titel des Menüs ist Name des Gerichts
        menu.setHeaderTitle(m.getName());

        contextMenuFrag = this;
    }

    @Override
    public boolean onContextItemSelected(@NonNull final android.view.MenuItem item) {
        if (this != contextMenuFrag) {
            return true;
        }

        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String url;

        switch (item.getItemId()) {
            case R.id.goolemeal:
                url = "http://www.google.com/m/search?q=%q%";
                break;
            case R.id.googlemailpic:
                url = "http://www.google.com/m/search?q=%q%&tbm=isch";
                break;
            default:
                return true;

        }
        try {
            // Gericht, auf das geklickt wurde
            final Meal m = (Meal) getListView().getItemAtPosition(info.position);

            url = url.replace("%q%", m.getName());

            final Intent i = new Intent("android.intent.action.VIEW", Uri.parse(url));

            startActivity(i);

            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        this.isVisibleToUser = isVisibleToUser;
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        scrollListener = (ScrollListener) getActivity();

        if (scrollListener != null) {
            notifyScrollListener();
        }
    }

    @Override
    public void onPause() {
        scrollListener = null;
        super.onPause();
    }

    private void notifyScrollListener() {
        if (!isVisibleToUser || listView.getChildCount() == 0) {
            return;
        }

        int firstVisiblePosition = listView.getFirstVisiblePosition();

        View firstChild = listView.getChildAt(0);
        if (firstVisiblePosition == 0) {
            scrollListener.onScrolled(-firstChild.getTop());
        } else {
            scrollListener.onScrolled(
                    -firstChild.getTop() + firstChild.getHeight() * firstVisiblePosition);
        }
    }

    private final class PopulateListTask extends AsyncTask<Boolean, Void, ListAdapter> {
        private final Calendar calendar;
        private final Context c;
        private final boolean reload;

        private PopulateListTask(final Context c, final Calendar calendar, final boolean reload) {
            this.c = c;
            this.calendar = calendar;
            this.reload = reload;
        }

        @Override
        protected void onPreExecute() {
            if (menuReload != null) {
                menuReload.setActionView(R.layout.actionitem_progress);
            }

            final Plan cachedPlan = planManager.getPlanFromCache(date);

            if (!reload && cachedPlan != null && !cachedPlan.isEmpty()) {
                setListAdapter(new PlanAdapter(c, cachedPlan));
            }
        }

        @Override
        protected ListAdapter doInBackground(final Boolean... params) {

            final Plan plan = planManager.getPlan(calendar, reload);

            return new PlanAdapter(c, plan);
        }

        @Override
        protected void onPostExecute(final ListAdapter result) {
            setListAdapter(result);

            if (menuReload != null) {
                menuReload.setActionView(null);
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void log(final Object msg) {
        if (BuildConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), String.valueOf(msg));
        }
    }

    @Override
    public void onItemClick(
            final AdapterView<?> parent, final View view, final int position, final long id) {
        final Object item = parent.getItemAtPosition(position);
        if (item instanceof Meal) {
            final Meal meal = (Meal) item;

            if (meal.isCow_aw()) {
                final Activity activity = requireActivity();

                final Intent i = new Intent(activity, HappyCowActivity.class);
                i.putExtra(HappyCowActivity.EXTRA_MENSA_CLASS, managerClass);
                i.putExtra(HappyCowActivity.EXTRA_DAY, date);
                i.putExtra(HappyCowActivity.EXTRA_ITEM, position);

                final int[] screenLocation = new int[2];

                final View cowView = view.findViewById(R.id.imageCow_aw);
                cowView.getLocationOnScreen(screenLocation);
                final int orientation = getResources().getConfiguration().orientation;

                final String PACKAGE = activity.getPackageName();

                i.putExtra(PACKAGE + ".orientation", orientation).putExtra(PACKAGE + ".left",
                                                                           screenLocation[0]).putExtra(
                        PACKAGE + ".top", screenLocation[1]).putExtra(PACKAGE + ".width",
                                                                      cowView.getWidth()).putExtra(
                        PACKAGE + ".height", cowView.getHeight());

                startActivity(i);

                activity.overridePendingTransition(0, 0);

                // animate cow
                View viewParent = cowView;
                do {
                    viewParent = (View) viewParent.getParent();
                    ((ViewGroup) viewParent).setClipChildren(false);
                    ((ViewGroup) viewParent).setClipToPadding(false);
                } while (viewParent != view);

                ObjectAnimator.ofFloat(cowView, "scaleX", 1f, 1.5f, 1f).setDuration(1000).start();
                ObjectAnimator.ofFloat(cowView, "scaleY", 1f, 1.5f, 1f).setDuration(1000).start();
                ObjectAnimator.ofFloat(cowView, "translationY", 0f,
                                       -getResources().getDimension(R.dimen.cow_jump),
                                       0f).setDuration(1000).start();
                final ViewPropertyAnimator rotation = ViewPropertyAnimator.animate(
                        cowView).rotationBy(360).setDuration(1000);
                rotation.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(final Animator animation) {
                        View viewParent = cowView;
                        do {
                            viewParent = (View) viewParent.getParent();
                            ((ViewGroup) viewParent).setClipChildren(true);
                            ((ViewGroup) viewParent).setClipToPadding(false);
                        } while (viewParent != view);
                    }
                });
                rotation.start();
            }

        }
    }
}
