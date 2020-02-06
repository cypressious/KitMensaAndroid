package com.cypressworks.mensaplan;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import com.cypressworks.mensaplan.food.likes.LikeManager;

/**
 * Created by Kirill on 30.05.2016.
 */
public class BackupAgent extends BackupAgentHelper {

    @Override
    public void onCreate() {
        addHelper("collected", new FileBackupHelper(this, HappyCowActivity.FILE_NAME_COLLECTED));
        addHelper("prefs",
                  new SharedPreferencesBackupHelper(this, getPackageName() + "_preferences"));
        addHelper("likes", new SharedPreferencesBackupHelper(this, LikeManager.LIKES_PREFS));
    }
}
