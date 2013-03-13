package com.idamobile.vpb.courier.controllers;

import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.model.CancellationReason;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.model.OrderStatus;
import com.idamobile.vpb.courier.widget.orders.awards.AwardMessages;
import com.idamobile.vpb.courier.widget.orders.awards.AwardToast;

import java.util.List;

public class AwardManager {

    private ApplicationMediator mediator;
    private AwardMessages awardMessages;

    public AwardManager(ApplicationMediator mediator) {
        this.mediator = mediator;
        this.awardMessages = new AwardMessages(mediator.getContext());
    }

    public void onOrderCompleted() {
        if (isAllOrdersCompleted()) {
            showToast(awardMessages.getMessageForLastOrder());
        } else {
            showToast(awardMessages.getPositiveMessage());
        }
    }

    private boolean isAllOrdersCompleted() {
        List<Order> orders = mediator.getOrdersManager().getOrders();
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.STATUS_NEW) {
                return false;
            }
        }
        return true;
    }

    public void onOrderCancelled(CancellationReason reason, boolean metWithClient) {
        showToast(awardMessages.getNegativeMessage(reason));
    }

    private void showToast(AwardMessages.Message message) {
        AwardToast.show(mediator.getContext(), message.getImageResId(), message.getMessage());
    }

}
