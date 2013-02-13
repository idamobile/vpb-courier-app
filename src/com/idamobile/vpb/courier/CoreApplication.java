package com.idamobile.vpb.courier;

import android.app.Application;
import com.idamobile.vpb.courier.util.Debug;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formKey = "dExIZ0ZNU2kxTWdmbk1xNDN5clJpV2c6MA",
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.acra_dialog_text,
        resToastText = R.string.acra_toast_text)
public class CoreApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (Debug.isEnabled(this)) {
            ACRA.init(this);
        }
    }
}
