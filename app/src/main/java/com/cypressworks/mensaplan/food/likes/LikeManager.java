package com.cypressworks.mensaplan.food.likes;

import android.content.Context;
import android.content.SharedPreferences;

public class LikeManager {

    public static final String LIKES_PREFS = "mealLikes";
    private final SharedPreferences prefs;

    public LikeManager(Context context) {
        this.prefs = context.getSharedPreferences(LIKES_PREFS, Context.MODE_PRIVATE);
    }

    public void like(String meal) {
        putLikeState(meal, LikeStatus.LIKED);
    }

    public void dislike(String meal) {
        putLikeState(meal, LikeStatus.DISLIKED);
    }

    public void reset(String meal) {
        putLikeState(meal, LikeStatus.NO_LIKE_INFO);
    }

    public void toggle(String meal) {
        switch (prefs.getInt(meal, LikeStatus.NO_LIKE_INFO)) {
            case LikeStatus.DISLIKED: reset(meal); break;
            case LikeStatus.NO_LIKE_INFO: like(meal); break;
            case LikeStatus.LIKED: dislike(meal); break;
            default:
        }
    }

    public @LikeStatus int getLikeStatus(String meal) {
        return prefs.getInt(meal, LikeStatus.NO_LIKE_INFO);
    }

    private void putLikeState(String meal, @LikeStatus int likeStatus) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(meal, likeStatus);
        editor.apply();
    }
}
