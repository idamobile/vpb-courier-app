package com.idamobile.vpb.courier.controllers;

import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.network.core.DataHolder;
import com.idamobile.vpb.courier.network.images.OrderImages;

public class ImageManager {

    private ApplicationMediator mediator;

    public ImageManager(ApplicationMediator mediator) {
        this.mediator = mediator;
    }

    public OrderImages getImages(Order order) {
        DataHolder<OrderImages> holder =
                mediator.getCache().getHolder(OrderImages.class, String.valueOf(order.getId()));
        return holder.get();
    }

}
