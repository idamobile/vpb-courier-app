package com.idamobile.vpb.courier.navigation;

import android.content.Context;
import com.idamobile.vpb.courier.LoginActivity_;
import com.idamobile.vpb.courier.OrderDetailsActivity_;
import com.idamobile.vpb.courier.OrderListActivity_;

public abstract class AbstractNavigationController implements NavigationController {

    private Context context;
    private NavigationMethodFactory factory;

    public AbstractNavigationController(Context context, NavigationMethodFactory factory) {
        this.context = context;
        this.factory = factory;
    }

    public Context getContext() {
        return context;
    }

    public NavigationMethodFactory getFactory() {
        return factory;
    }

    @Override
    public NavigationMethod getLogin() {
        return factory.createNavigationMethod(context, LoginActivity_.class);
    }

    @Override
    public NavigationMethod getOrdersList() {
        return factory.createNavigationMethod(context, OrderListActivity_.class);
    }

    @Override
    public NavigationMethod getOrderDetails() {
        return factory.createNavigationMethod(context, OrderDetailsActivity_.class);
    }
}