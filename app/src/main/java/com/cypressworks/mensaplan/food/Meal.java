package com.cypressworks.mensaplan.food;

import java.io.Serializable;

/**
 * Klasse, die ein Gericht darstellt.
 *
 * @author Kirill Rakhman
 */
public class Meal implements Serializable {

    public static final String NO_MEAL = "Linie geschlossen";

    private static final long serialVersionUID = -6729670284326867259L;
    private final String meal;
    private final String dish;
    private final String price;

    private boolean bio = false;
    private boolean fish = false;
    private boolean pork = false;
    private boolean cow = false;
    private boolean cow_aw = false;
    private boolean vegan = false;
    private boolean veg = false;
    private boolean likeable = true;

    public Meal(final String meal, final String dish, final String price) {
        super();
        this.meal = meal;
        this.dish = dish;
        this.price = price;
    }

    public String getMeal() {
        return meal;
    }

    public String getDish() {
        return dish;
    }

    public String getName() {
        if (meal != null || dish != null) {
            return (meal + " " + dish).trim();
        } else {
            return "-";
        }
    }

    /**
     * Gibt den Preis zur�ck.
     *
     * @return der Preis als {@link String}
     */
    public String getPrice() {
        return price;
    }

    public boolean isBio() {
        return bio;
    }

    public void setBio(final boolean bio) {
        this.bio = bio;
    }

    public boolean isFish() {
        return fish;
    }

    public void setFish(final boolean fish) {
        this.fish = fish;
    }

    public boolean isPork() {
        return pork;
    }

    public void setPork(final boolean pork) {
        this.pork = pork;
    }

    public boolean isCow() {
        return cow;
    }

    public void setCow(final boolean cow) {
        this.cow = cow;
    }

    public boolean isCow_aw() {
        return cow_aw;
    }

    public void setCow_aw(final boolean cow_aw) {
        this.cow_aw = cow_aw;
    }

    public boolean isVegan() {
        return vegan;
    }

    public void setVegan(final boolean vegan) {
        this.vegan = vegan;
    }

    public boolean isVeg() {
        return veg;
    }

    public void setVeg(final boolean veg) {
        this.veg = veg;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public void setLikeable(final boolean likeable) {
        this.likeable = likeable;
    }

    public boolean isLikeable() {
        return likeable;
    }
}
