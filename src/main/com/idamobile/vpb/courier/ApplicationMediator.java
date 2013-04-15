package com.idamobile.vpb.courier;

import android.content.Context;
import com.idamobile.vpb.courier.controllers.AwardManager;
import com.idamobile.vpb.courier.controllers.ImageManager;
import com.idamobile.vpb.courier.controllers.LoginManager;
import com.idamobile.vpb.courier.controllers.OrdersManager;
import com.idamobile.vpb.courier.model.cache.Cache;
import com.idamobile.vpb.courier.model.cache.DefaultCache;
import com.idamobile.vpb.courier.model.cache.DefaultFullNameMapper;
import com.idamobile.vpb.courier.network.core.DefaultHttpClientFactory;
import com.idamobile.vpb.courier.network.core.NetworkManager;
import com.idamobile.vpb.courier.network.login.NotAuthorizedResponseProcessor;
import com.idamobile.vpb.courier.widget.orders.images.ImagesUploadProgressNotifier;

public class ApplicationMediator {

    private Context context;
    private Cache cache;

    private NetworkManager networkManager;
    private LoginManager loginManager;
    private OrdersManager ordersManager;
    private ImageManager imageManager;
    private ImagesUploadProgressNotifier progressNotifier;
    private AwardManager awardManager;

    public ApplicationMediator(Context ctx) {
        context = ctx;
        setupNetworkManager();
        loginManager = new LoginManager(this);
        ordersManager = new OrdersManager(this);
        imageManager = new ImageManager(this);
        progressNotifier = new ImagesUploadProgressNotifier(this);
        awardManager = new AwardManager(this);

        cache = new DefaultCache(context, new DefaultFullNameMapper());
    }

    private void setupNetworkManager() {
        networkManager = new NetworkManager(this, new DefaultHttpClientFactory(context));
//        networkManager = new NetworkManager(this, new TestHttpClientFactory());
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

    public OrdersManager getOrdersManager() {
        return ordersManager;
    }

    public ImageManager getImageManager() {
        return imageManager;
    }

    public ImagesUploadProgressNotifier getProgressNotifier() {
        return progressNotifier;
    }

    public AwardManager getAwardManager() {
        return awardManager;
    }
}
