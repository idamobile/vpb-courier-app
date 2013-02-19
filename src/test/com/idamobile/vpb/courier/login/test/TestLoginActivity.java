package com.idamobile.vpb.courier.login.test;

import com.idamobile.vpb.courier.LoginActivity_;
import com.idamobile.vpb.courier.RobolectricWithCoreAppRunner;
import com.idamobile.vpb.courier.controllers.LoginManager;
import com.idamobile.vpb.courier.network.core.RequestWatcherCallbacks;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(RobolectricWithCoreAppRunner.class)
public class TestLoginActivity {
    @Test
    public void testEmptyLogin() throws Exception {
        LoginActivity_ activity = new LoginActivity_();
        activity.onCreate(null);

        LoginManager manager = spy(activity.getMediator().getLoginManager());
        activity.getLoginPresenter().startLogin(null, "1234");
        verify(manager, never()).login(any(RequestWatcherCallbacks.class), anyString(), anyString());
    }

    @Test
    public void testEmptyPassword() throws Exception {
        LoginActivity_ activity = new LoginActivity_();
        activity.onCreate(null);

        LoginManager manager = spy(activity.getMediator().getLoginManager());
        activity.getLoginPresenter().startLogin("login", null);
        verify(manager, never()).login(any(RequestWatcherCallbacks.class), anyString(), anyString());
    }
}
