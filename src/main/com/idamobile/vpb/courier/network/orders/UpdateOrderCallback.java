package com.idamobile.vpb.courier.network.orders;

import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.network.core.DataHolder;
import com.idamobile.vpb.courier.network.core.LoaderCallback;
import com.idamobile.vpb.courier.network.core.Request;
import com.idamobile.vpb.courier.network.core.ResponseDTO;

public class UpdateOrderCallback implements LoaderCallback<UpdateOrderResponse> {

    @Override
    public void onStartLoading(Request<UpdateOrderResponse> request, ApplicationMediator mediator) {
    }

    @Override
    public void onDataReady(Request<UpdateOrderResponse> request, ResponseDTO<UpdateOrderResponse> response, ApplicationMediator mediator) {
        if (response.isSuccess()) {
            UpdateOrderResponse responseData = response.getData();
            Order order = mediator.getOrdersManager().getOrder(responseData.getOrderId());
            if (order != null) {
                order.setStatus(responseData.getNewStatus());
                order.setStatusUpdateTime(System.currentTimeMillis());
                if (!responseData.getImageTypes().isEmpty()) {
                    order.setImageTypes(responseData.getImageTypes());
                    mediator.getImageManager().refreshImages();
                }
                getOrdersHolder(mediator).notifyChanged();
            }
        }
    }

    private DataHolder<GetOrdersResponse> getOrdersHolder(ApplicationMediator mediator) {
        return mediator.getOrdersManager().getOrdersHolder();
    }
}
