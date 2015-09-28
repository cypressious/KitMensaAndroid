package com.cypressworks.mensaplan.util;

import android.text.SpannableStringBuilder;

/**
 * Created by Kirill on 07.11.2014.
 */
public class SpannableStringBuilderHelper {
    private SpannableStringBuilder sb = new SpannableStringBuilder();

    public SpannableStringBuilder getSpannableStringBuilder() {
        return sb;
    }

    public SpannableStringBuilderHelper append(
            final CharSequence text, final Object what, final int flags) {
        final int start = sb.length();
        sb.append(text);
        sb.setSpan(what, start, sb.length(), flags);
        return this;
    }

    public SpannableStringBuilder append(final CharSequence text) {
        return sb.append(text);
    }
}
