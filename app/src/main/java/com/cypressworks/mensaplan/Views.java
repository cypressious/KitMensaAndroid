package com.cypressworks.mensaplan;

import android.app.Activity;
import android.view.View;

/**
 * @author Kirill Rakhman
 */
class Views {
    private Views() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(final View v, final int id) {
        return (T) v.findViewById(id);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(final Activity a, final int id) {
        return (T) a.findViewById(id);
    }
}
