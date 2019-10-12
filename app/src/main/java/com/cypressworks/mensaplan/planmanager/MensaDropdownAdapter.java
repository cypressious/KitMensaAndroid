package com.cypressworks.mensaplan.planmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cypressworks.mensaplan.MainActivity;
import com.cypressworks.mensaplan.R;

/**
 * @author Kirill Rakhman
 */
public class MensaDropdownAdapter extends BaseAdapter implements
                                                      SharedPreferences.OnSharedPreferenceChangeListener {
    private final Context context;
    private int selectedMensa;
    private final int accentColor;

    public MensaDropdownAdapter(final Context context) {
        this.context = context;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);
        selectedMensa = prefs.getInt(MainActivity.PREF_MENSA_NUM, 0);

        accentColor = context.getResources().getColor(R.color.accent);
    }

    @SuppressWarnings("rawtypes") private static final Class[] classes = {//
            AdenauerPlanManager.class,//
            MoltkePlanManager.class,//
            ErzbergerPlanManager.class,//
            GottesauePlanManager.class, //
            PforzheimPlanManager.class, //
            HolzgartenstrPlanManager.class};

    private static final String[] names = {//
            AdenauerPlanManager.fullProviderName,//
            MoltkePlanManager.fullProviderName,//
            ErzbergerPlanManager.fullProviderName,//
            GottesauePlanManager.fullProviderName, //
            PforzheimPlanManager.fullProviderName, //
            HolzgartenstrPlanManager.fullProviderName};

    @SuppressWarnings("unchecked")
    public static PlanManager getManagerFromPreferences(final Context c) {
        final int index = PreferenceManager.getDefaultSharedPreferences(c).getInt(
                MainActivity.PREF_MENSA_NUM, 0);

        return PlanManager.getInstance(classes[index], c);
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends PlanManager> getItem(final int position) {
        return classes[position];
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        return makeLayout(position, convertView, parent, R.layout.textview_drawer);
    }

    private View makeLayout(
            final int position, final View convertView, final ViewGroup parent, final int layout) {
        TextView tv;
        if (convertView != null) {
            tv = (TextView) convertView;
        } else {
            tv = (TextView) LayoutInflater.from(context).inflate(layout, parent, false);
        }

        tv.setText(getName(position));
        tv.setTextColor(position == selectedMensa ? accentColor : Color.BLACK);

        return tv;
    }

    public String getName(final int position) {
        return names[position];
    }

    @Override
    public void onSharedPreferenceChanged(
            final SharedPreferences prefs, final String key) {
        if (key.equals(MainActivity.PREF_MENSA_NUM)) {
            selectedMensa = prefs.getInt(MainActivity.PREF_MENSA_NUM, 0);
            notifyDataSetChanged();
        }
    }
}
