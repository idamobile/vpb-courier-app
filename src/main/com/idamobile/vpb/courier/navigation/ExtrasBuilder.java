package com.idamobile.vpb.courier.navigation;

import android.os.Bundle;

public class ExtrasBuilder {

    public static final String EXTRA_ORDER_ID = "order-id";

    public static Bundle orderDetailsBundle(int orderId) {
        Bundle extras = new Bundle();
        extras.putInt(EXTRA_ORDER_ID, orderId);
        return extras;
    }

}