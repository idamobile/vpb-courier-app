package com.idamobile.vpb.courier.navigation;

import android.app.Activity;
import android.os.SystemClock;
import android.widget.Toast;
import com.idamobile.vpb.courier.R;

public class RootActivityBackButtonController {

    private static final long BACK_BUTTON_DOUBLE_CLICK_INTERVAL_MS = 2500L;

    private Activity activity;
    private long lastBackButtonClickTime;

    private Toast toast;

    public RootActivityBackButtonController(Activity activity) {
        this.activity = activity;
    }

    /**
     * @return false to call super.onBackPressed(), true otherwise
     */
    public boolean dispatchOnBackPressed() {
        if (isDoubleClick()) {
            hideToast();
            return false;
        } else {
            saveClickTime();
            showToast();
            return true;
        }
    }

    private void hideToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

    private void showToast() {
        hideToast();
        toast = Toast.makeText(activity, R.string.press_back_again_to_exit, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void saveClickTime() {
        lastBackButtonClickTime = getCurrentTime();
    }

    private boolean isDoubleClick() {
        long diff = getCurrentTime() - lastBackButtonClickTime;
        return diff > 0 && diff < BACK_BUTTON_DOUBLE_CLICK_INTERVAL_MS;
    }

    private long getCurrentTime() {
        return SystemClock.uptimeMillis();
    }

}