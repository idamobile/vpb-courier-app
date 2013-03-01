package com.idamobile.vpb.courier.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.model.CancellationReason;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.network.core.DataHolder;
import com.idamobile.vpb.courier.network.core.RequestService;
import com.idamobile.vpb.courier.network.core.RequestWatcherCallbacks;
import com.idamobile.vpb.courier.network.orders.*;

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
        request.setUpdateModelCallback(new GetOrdersResponseCallback());
        if (callback != null) {
            callback.execute(request);
        } else {
            RequestService.execute(mediator.getContext(), request);
        }
    }

    public void requestSetOrderCompleted(int orderId, boolean resident, boolean hasMarks,
                                         RequestWatcherCallbacks<UpdateOrderResponse> callbacks) {
        SetOrderCompletedRequest request = new SetOrderCompletedRequest();
        request.setUpdateModelCallback(new UpdateOrderCallback());
        request.setHasCorrections(hasMarks);
        request.setResident(resident);
        request.setOrderId(orderId);
        if (callbacks != null) {
            callbacks.execute(request);
        } else {
            RequestService.execute(mediator.getContext(), request);
        }
    }

    public void requestSetOrderRejected(int orderId, boolean metWithClient, CancellationReason reason,
                                         RequestWatcherCallbacks<UpdateOrderResponse> callbacks) {
        CancelOrderRequest request = new CancelOrderRequest();
        request.setUpdateModelCallback(new UpdateOrderCallback());
        request.setReason(reason);
        request.setMetWithClient(metWithClient);
        request.setOrderId(orderId);
        if (callbacks != null) {
            callbacks.execute(request);
        } else {
            RequestService.execute(mediator.getContext(), request);
        }
    }

    public void registerForOrders(Context context, BroadcastReceiver receiver) {
        context.registerReceiver(receiver, new IntentFilter(getOrdersHolder().getBroadcastAction()));
    }

    public List<Order> getOrders() {
        List<Order> result = new ArrayList<Order>();
        DataHolder<GetOrdersResponse> holder = getOrdersHolder();
        if (!holder.isEmpty()) {
            result.addAll(holder.get().getOrders());
        }
        return result;
    }

    public Order getOrder(int id) {
        List<Order> orders = getOrders();
        for (Order order : orders) {
            if (order.getId() == id) {
                return order;
            }
        }
        return null;
    }

    public DataHolder<GetOrdersResponse> getOrdersHolder() {
        return mediator.getCache().getHolder(GetOrdersResponse.class);
    }

}
