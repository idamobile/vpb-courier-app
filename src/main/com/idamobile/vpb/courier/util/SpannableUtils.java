package com.idamobile.vpb.courier.util;

import android.text.Spannable;
import android.text.SpannableStringBuilder;

public class SpannableUtils {

    public static SpannableStringBuilder append(SpannableStringBuilder builder, CharSequence text, Object... spans) {
        builder.append(text);
        for (Object span : spans) {
            builder.setSpan(span, builder.length() - text.length(), builder.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

}
