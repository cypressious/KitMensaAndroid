package com.cypressworks.mensaplan.util;

/**
 * Created by Kirill on 05.12.2014.
 */
public class StringUtils {

    public static String sanitize(final String string) {
        if (string == null) {
            return "";
        } else {
            return string.trim();
        }
    }
}
