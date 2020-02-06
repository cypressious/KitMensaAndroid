package com.cypressworks.mensaplan.food;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypressworks.mensaplan.R;
import com.cypressworks.mensaplan.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter f�r Darstellung von Eintr�gen vom Typ {@link Meal}.
 *
 * @author Kirill Rakhman
 */
class PlanAdapter extends BaseAdapter {

    private final Context c;

    private final List<Object> items;
    private final int[] types;

    private static final int TYPE_LINE = 0, TYPE_MEAL = 1;

    public PlanAdapter(final Context context, final Plan plan) {
        this.c = context;

        items = new ArrayList<>();
        types = new int[plan.getTotalItemsCount()];
        int index = 0;
        for (final Line line : plan) {
            items.add(line);
            types[index++] = TYPE_LINE;
            for (final Meal meal : line) {
                items.add(meal);
                types[index++] = TYPE_MEAL;
            }
        }

    }

    @Override
    public View getView(final int position, View v, final ViewGroup parent) {

        if (types[position] == TYPE_LINE) {
            // Header
            final HeaderHolder tag;
            if (v == null) {
                final LayoutInflater vi = LayoutInflater.from(c);
                v = vi.inflate(R.layout.list_header, parent, false);
                tag = new HeaderHolder(v);
                v.setTag(tag);
            } else {
                tag = (HeaderHolder) v.getTag();
            }
            final Line line = (Line) items.get(position);
            tag.name.setText(line.getName());
        } else {
            // Item
            final ItemHolder tag;
            if (v == null) {
                final LayoutInflater vi = LayoutInflater.from(c);
                v = vi.inflate(R.layout.list_item, parent, false);
                tag = new ItemHolder(v);
                v.setTag(tag);
            } else {
                tag = (ItemHolder) v.getTag();
            }
            final Meal meal = (Meal) items.get(position);
            tag.name.setText(StringUtils.sanitize(meal.getMeal()));

            final String dish = StringUtils.sanitize(meal.getDish());
            tag.subName.setText(dish);
            tag.subName.setVisibility("".equals(dish) ? View.GONE : View.VISIBLE);

            tag.price.setText(meal.getPrice());

            tag.bio.setVisibility(meal.isBio() ? View.VISIBLE : View.GONE);
            tag.fish.setVisibility(meal.isFish() ? View.VISIBLE : View.GONE);
            tag.pork.setVisibility(meal.isPork() ? View.VISIBLE : View.GONE);
            tag.cow.setVisibility(meal.isCow() ? View.VISIBLE : View.GONE);
            tag.cow_aw.setVisibility(meal.isCow_aw() ? View.VISIBLE : View.GONE);
            tag.vegan.setVisibility(meal.isVegan() ? View.VISIBLE : View.GONE);
            tag.veg.setVisibility(meal.isVeg() ? View.VISIBLE : View.GONE);

            //            if (position % 2 == 1) {
            //                tag.layout.setBackgroundColor(Color.LTGRAY);
            //            } else {
            //                tag.layout.setBackgroundColor(Color.TRANSPARENT);
            //            }
        }

        return v;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(final int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getItemViewType(final int position) {
        return types[position];
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(final int position) {
        return types[position] == TYPE_MEAL;
    }

    private static class HeaderHolder {
        final TextView name;

        HeaderHolder(final View v) {
            name = v.findViewById(R.id.list_header_title);
        }
    }

    private static class ItemHolder {
        final TextView name;
        final TextView subName;
        final TextView price;
        final ImageView bio, fish, pork, cow, cow_aw, vegan, veg;
        final View layout;

        ItemHolder(final View v) {
            layout = v;
            name = v.findViewById(R.id.textName);
            subName = v.findViewById(R.id.textSubName);
            price = v.findViewById(R.id.textPrice);

            bio = v.findViewById(R.id.imageBio);
            fish = v.findViewById(R.id.imageFish);
            pork = v.findViewById(R.id.imagePork);
            cow = v.findViewById(R.id.imageCow);
            cow_aw = v.findViewById(R.id.imageCow_aw);
            vegan = v.findViewById(R.id.imageVegan);
            veg = v.findViewById(R.id.imageVeg);
        }
    }
}
