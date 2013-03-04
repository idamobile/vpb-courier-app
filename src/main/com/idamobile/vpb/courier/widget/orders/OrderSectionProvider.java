package com.idamobile.vpb.courier.widget.orders;

import android.content.Context;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.model.Order;

public class OrderSectionProvider {

    private Context context;

    public OrderSectionProvider(Context context) {
        this.context = context;
    }

    public String getOrderSection(Order order) {
        switch (order.getStatus()) {
            case STATUS_NEW:
                return context.getString(R.string.active_orders_section);
            case STATUS_DOCUMENTS_SUBMITTED:
                return context.getString(R.string.success_orders_section);
            case STATUS_ACTIVATED:
                return context.getString(R.string.activated_orders_section);
            case STATUS_DOCUMENTS_NOT_SUBMITTED:
                return context.getString(R.string.failed_orders_section);

            default:
                throw new IllegalArgumentException("Unknown order status: " + order.getStatus());
        }
    }
}
