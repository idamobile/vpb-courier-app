package com.idamobile.vpb.courier.network.orders;

import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.network.core.DefaultLoaderCallback;
import com.idamobile.vpb.courier.network.core.Request;

import java.util.HashMap;
import java.util.Map;

public class GetOrdersResponseCallback extends DefaultLoaderCallback<GetOrdersResponse> {

    public GetOrdersResponseCallback() {
        super(GetOrdersResponse.class);
    }

    @Override
    protected GetOrdersResponse mergeResult(GetOrdersResponse oldData, GetOrdersResponse newData) {
        if (oldData != null) {
            Map<Integer, Order> orderMap = new HashMap<Integer, Order>(newData.getOrders().size());
            for (Order order : newData.getOrders()) {
                orderMap.put(order.getId(), order);
            }
            for (Order order : oldData.getOrders()) {
                Order newOrder = orderMap.get(order.getId());
                if (newOrder != null) {
                    newOrder.setStatusUpdateTime(order.getStatusUpdateTime());
                }
            }
        }
        return newData;
    }

    @Override
    protected void onSuccess(Request<GetOrdersResponse> request, GetOrdersResponse data, ApplicationMediator mediator) {
        super.onSuccess(request, data, mediator);
        mediator.getImageManager().refreshImages();
    }
}
