package com.idamobile.vpb.courier.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

public class ListHeaders {

    public static View createEmptyHeaderRes(Context context, int dimenRes) {
        return createEmptyHeaderPx(context, context.getResources().getDimensionPixelSize(dimenRes));
    }

    public static View createEmptyHeaderPx(Context context, int px) {
        FrameLayout layout = new FrameLayout(context);
        View view = new View(context);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, px));
        layout.addView(view);
        return layout;
    }

}
