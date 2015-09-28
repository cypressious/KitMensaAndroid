package com.cypressworks.mensaplan;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.MenuItem;

/**
 * @author Kirill Rakhman
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AndroidV11Helper {
    private AndroidV11Helper() {
    }

    public static void setActionView(final MenuItem item, final Integer layout) {
        if (item == null) {
            return;
        }

        if (layout == null) {
            item.setActionView(null);
        } else {
            item.setActionView(layout);
        }
    }
}
