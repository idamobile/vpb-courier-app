package com.idamobile.vpb.courier;

import android.content.Context;
import com.idamobile.vpb.courier.controllers.LoginManager;
import com.idamobile.vpb.courier.model.cache.Cache;
import com.idamobile.vpb.courier.model.cache.DefaultCache;
import com.idamobile.vpb.courier.model.cache.DefaultFullNameMapper;
import com.idamobile.vpb.courier.network.core.NetworkManager;
import com.idamobile.vpb.courier.network.core.NotAuthorizedResponseProcessor;

public class ApplicationMediator {

    private Context context;
    private Cache cache;

    private NetworkManager networkManager;
    private LoginManager loginManager;

    public ApplicationMediator(Context ctx) {
        context = ctx;
        setupNetworkManager();
        loginManager = new LoginManager(this);

        cache = new DefaultCache(context, new DefaultFullNameMapper());
    }

    private void setupNetworkManager() {
        networkManager = new NetworkManager(this);
        networkManager.addProcessor(new NotAuthorizedResponseProcessor(getContext()));
    }

    public Context getContext() {
        return context;
    }

    public Cache getCache() {
        return cache;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public LoginManager getLoginManager() {
        return loginManager;
    }
}
