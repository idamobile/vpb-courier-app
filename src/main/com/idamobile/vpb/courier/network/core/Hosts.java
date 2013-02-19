package com.idamobile.vpb.courier.network.core;

import com.idamobile.vpb.courier.config.Config;

public class Hosts {

    public static final String LOGIN_URL = addHost("login");

    public static final String ORDERS_URL = addHost("orders");

    private static String addHost(String subUrl) {
        return Config.HOST + "/" + subUrl;
    }

}
