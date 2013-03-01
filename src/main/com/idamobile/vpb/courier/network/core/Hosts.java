package com.idamobile.vpb.courier.network.core;

import com.idamobile.vpb.courier.config.Config;

public class Hosts {

    public static final String LOGIN_URL = addHost("courier/login");

    public static final String ORDERS_URL = addHost("courier/orders");

    public static final String COMPLETE_ORDER_URL = addHost("courier/orders/complete");

    public static final String REJECT_ORDER_URL = addHost("courier/orders/reject");

    private static String addHost(String subUrl) {
        return Config.HOST + "/" + subUrl;
    }

}
