package com.cypressworks.mensaplan.food.likes;

import androidx.annotation.IntDef;
import com.cypressworks.mensaplan.food.Meal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * Represents the like state of a {@link Meal}.
 * Realised with integer states instead of an enum because enums
 * are discouraged in Android App Development
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({LikeStatus.LIKED, LikeStatus.DISLIKED, LikeStatus.NO_LIKE_INFO})
public @interface LikeStatus {
    public static final int LIKED = 1;
    public static final int DISLIKED = -1;
    public static final int NO_LIKE_INFO = 0;
}