package com.idamobile.vpb.courier.widget.orders.images;

import android.view.View;
import android.view.ViewGroup;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.model.ImageType;
import com.idamobile.vpb.courier.model.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImagesGrid {

    private ViewGroup imagesGroup;
    private ApplicationMediator mediator;

    private Order order;

    private OrderImageView.OrderImageImageCallbacks imageCallbacks;
    private OrderImageView.OrderImageImageCallbacks callbacksJoiner = new OrderImageView.OrderImageImageCallbacks() {
        @Override
        public void onTakeImageClicked(Order order, ImageType image) {
            if (imageCallbacks != null) {
                imageCallbacks.onTakeImageClicked(order, image);
            }
        }

        @Override
        public void onRemoveImageClicked(Order order, ImageType image) {
            if (imageCallbacks != null) {
                imageCallbacks.onRemoveImageClicked(order, image);
            }
        }
    };

    private Map<Integer, OrderImageView> imageViewMap = new HashMap<Integer, OrderImageView>();

    public ImagesGrid(ViewGroup imagesGroup, ApplicationMediator mediator) {
        this.imagesGroup = imagesGroup;
        this.mediator = mediator;
    }

    public void setImageCallbacks(OrderImageView.OrderImageImageCallbacks imageCallbacks) {
        this.imageCallbacks = imageCallbacks;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
        refreshContent();
    }

    public void refreshContent() {
        if (order == null || order.getImageTypes().isEmpty()) {
            imageViewMap.clear();
            imagesGroup.removeAllViews();
            imagesGroup.setVisibility( View.GONE);
        } else {
            imagesGroup.setVisibility(View.VISIBLE);
            List<Integer> oldTypeIds = new ArrayList<Integer>(imageViewMap.keySet());
            for (Integer typeId : oldTypeIds) {
                if (!order.hasImageType(typeId)) {
                    imagesGroup.removeView(imageViewMap.get(typeId).getView());
                    imageViewMap.remove(typeId);
                }
            }

            for (ImageType type : order.getImageTypes()) {
                OrderImageView orderImageView = imageViewMap.get(type.getId());
                if (orderImageView == null) {
                    orderImageView = new OrderImageView(imagesGroup, mediator);
                    imageViewMap.put(type.getId(), orderImageView);
                    imagesGroup.addView(orderImageView.getView());
                    orderImageView.setImageCallbacks(callbacksJoiner);
                }
                orderImageView.setImage(order, type);
            }
        }
    }


}