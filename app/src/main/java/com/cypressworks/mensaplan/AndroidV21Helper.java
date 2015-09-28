package com.cypressworks.mensaplan;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.BitmapFactory;
import android.os.Build;

/**
 * Created by Kirill on 28.10.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AndroidV21Helper {

    public static void setTaskDescription(
            final Activity activity, final String label, final int icon, final int color) {
        activity.setTaskDescription(new ActivityManager.TaskDescription(label,
                                                                        BitmapFactory.decodeResource(
                                                                                activity.getResources(),
                                                                                icon), color));
    }
}
