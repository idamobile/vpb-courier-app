package com.idamobile.vpb.courier.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.network.core.DataHolder;
import com.idamobile.vpb.courier.network.core.LoaderCallback;
import com.idamobile.vpb.courier.network.core.RequestService;
import com.idamobile.vpb.courier.network.core.RequestWatcherCallbacks;
import com.idamobile.vpb.courier.network.orders.GetOrdersRequest;
import com.idamobile.vpb.courier.network.orders.GetOrdersResponse;

import java.util.ArrayList;
import java.util.List;

public class OrdersManager {

    private ApplicationMediator mediator;

    public OrdersManager(ApplicationMediator mediator) {
        this.mediator = mediator;
    }

    public void requestOrders(int courierId) {
        requestOrders(courierId, null);
    }

    public void requestOrders(int courierId,
                              RequestWatcherCallbacks<GetOrdersResponse> callback) {
        GetOrdersRequest request = new GetOrdersRequest();
        request.setCourierId(courierId);
        request.setUpdateModelCallback(
                new LoaderCallback<GetOrdersResponse>(GetOrdersResponse.class));
        if (callback != null) {
            callback.execute(request);
        } else {
            RequestService.execute(mediator.getContext(), request);
        }
    }

    public void registerForOrders(Context context, BroadcastReceiver receiver) {
        context.registerReceiver(receiver, new IntentFilter(
                mediator.getCache().getHolder(GetOrdersResponse.class).getBroadcastAction()));
    }

    public List<Order> getOrders() {
        List<Order> result = new ArrayList<Order>();
        DataHolder<GetOrdersResponse> holder = mediator.getCache().getHolder(GetOrdersResponse.class);
        if (!holder.isEmpty()) {
            result.addAll(holder.get().getOrders());
        }
        return result;
    }

}