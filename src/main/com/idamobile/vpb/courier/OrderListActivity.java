package com.idamobile.vpb.courier;

import android.view.View;
import android.widget.Button;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity
public class OrderListActivity extends BaseActivity {

    @ViewById(R.id.logout_button) Button logoutButton;

    @AfterViews
    void setup() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        getMediator().getLoginManager().logout();
        getNavigationController().processSignOut();
    }
}
