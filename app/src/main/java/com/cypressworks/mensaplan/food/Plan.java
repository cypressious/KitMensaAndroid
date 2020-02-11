package com.cypressworks.mensaplan.food;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Klasse, die einen Speiseplan darstellt. Verwaltet Linien und ihre Gerichte.
 *
 * @author Kirill Rakhman
 */
public class Plan implements Serializable, Iterable<Line> {
    private static final long serialVersionUID = -1058026007324238622L;
    private final List<Line> lines;
    private int totalCount;
    private final long timestamp;

    /**
     * Erzeugt einen leeren Plan ohne Mahlzeiten
     */
    public Plan() {
        this(new ArrayList<>());
    }

    /**
     * Erzeugt einen neuen Plan aus den gegebenen Linien.
     */
    public Plan(final List<Line> lines) {
        this.lines = lines;

        for (final Line line : lines) {
            totalCount++;
            for (@SuppressWarnings("unused") final Meal meal : line) {
                totalCount++;
            }
        }

        timestamp = System.currentTimeMillis();
    }

    /**
     * Gibt die Linien zur�ck.
     *
     * @return die Linien als {@link List<Line>}
     */
    List<Line> getLines() {
        return lines;
    }

    /**
     * Gibt an, ob der Plan leer ist.
     *
     * @return true, falls er keine Linien enth�lt.
     */
    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public int getTotalItemsCount() {
        return totalCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getAgeInDays() {
        final long diff = System.currentTimeMillis() - timestamp;

        return (int) (diff / (1000L * 60 * 60 * 24));
    }

    @NonNull
    @Override
    public Iterator<Line> iterator() {
        return getLines().iterator();
    }
}
