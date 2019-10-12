package com.cypressworks.mensaplan.food;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Klasse, die eine Linie in einer Mensa darstellt. Verwaltet die Speisen und
 * ihre Preise.
 *
 * @author Kirill Rakhman
 */
public class Line implements Serializable, Iterable<Meal> {
    private static final long serialVersionUID = 4830649237876805212L;
    private final List<Meal> meals;
    private final String name;

    /**
     * Erzeugt ein Objekt der Klasse {@link Line} ohne Eintr�ge.
     *
     * @param name Der Name der Linie.
     */
    public Line(final String name) {
        this(new ArrayList<>(), name);
    }

    /**
     * Erzeugt ein Objekt der Klasse {@link Line}.
     *
     * @param meals Die Speisen der Linie.
     * @param name  Der Name der Linie.
     */
    private Line(final List<Meal> meals, final String name) {
        this.name = name;
        this.meals = meals;
    }

    /**
     * Gibt die Speisen zur�ck.
     *
     * @return die Speisen als {@link List<Meal>}.
     */
    List<Meal> getMeals() {
        return meals;
    }

    public void addMeal(final Meal meal) {
        meals.add(meal);
    }

    /**
     * Gibt den Namen der Linie zur�ck.
     *
     * @return der Name als {@link String}
     */
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((meals == null) ? 0 : meals.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        if (!(obj instanceof Line)) {
            return false;
        }
        final Line other = (Line) obj;
        if (meals == null) {
            if (other.meals != null) {
                return false;
            }
        } else if (!meals.equals(other.meals)) {
            return false;
        }
        if (name == null) {
            return other.name == null;
        } else
            return name.equals(other.name);
    }

    @NonNull
    @Override
    public Iterator<Meal> iterator() {
        return getMeals().iterator();
    }

    public int size() {
        return meals.size();
    }

}
