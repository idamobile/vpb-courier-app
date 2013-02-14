package com.idamobile.vpb.courier;

import android.content.Context;
import com.idamobile.vpb.courier.model.Cache;
import com.idamobile.vpb.courier.model.DefaultCache;
import com.idamobile.vpb.courier.model.DefaultFullNameMapper;
import com.idamobile.vpb.courier.network.NetworkManager;
import com.idamobile.vpb.courier.network.NotAuthorizedResponseProcessor;

public class ApplicationMediator {

    private Context context;
    private Cache cache;

    private NetworkManager networkManager;

    public ApplicationMediator(Context ctx) {
        context = ctx;
        setupNetworkManager();

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

}
