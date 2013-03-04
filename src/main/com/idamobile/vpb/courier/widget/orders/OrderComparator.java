package com.idamobile.vpb.courier.widget.orders;

import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.model.OrderStatus;
import com.idamobile.vpb.courier.util.PrimitivesUtils;

import java.util.Comparator;

public class OrderComparator implements Comparator<Order> {
    @Override
    public int compare(Order lhs, Order rhs) {
        int res = PrimitivesUtils.compare(
                getPriorityOfStatus(lhs.getStatus()),
                getPriorityOfStatus(rhs.getStatus()));
        if (res == 0) {
            res = PrimitivesUtils.compare(lhs.getMeetTimeTo(), rhs.getMeetTimeTo());
            if (res == 0) {
                res = PrimitivesUtils.compare(lhs.getMeetTimeFrom(), rhs.getMeetTimeFrom());
                if (res == 0) {
                    res = lhs.getClientSecondName().compareTo(rhs.getClientSecondName());
                }
            }
        }
        return res;
    }

    private int getPriorityOfStatus(OrderStatus status) {
        switch (status) {
            case STATUS_NEW:
                return 1;
            case STATUS_DOCUMENTS_SUBMITTED:
                return 2;
            case STATUS_ACTIVATED:
                return 3;
            case STATUS_DOCUMENTS_NOT_SUBMITTED:
                return 4;
            default:
                throw new IllegalArgumentException("Unknown status: " + status);
        }
    }
}
