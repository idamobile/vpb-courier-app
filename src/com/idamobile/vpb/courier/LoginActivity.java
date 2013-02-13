package com.idamobile.vpb.courier;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        findViewById(R.id.crashme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throw new RuntimeException("Test ACRA");
            }
        });
    }
}
