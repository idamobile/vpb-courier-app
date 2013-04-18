package com.idamobile.vpb.courier.network.core;

import com.idamobile.vpb.courier.config.Config;

public class Hosts {

    public static final String LOGIN_URL = addHost("courier/api/login");

    public static final String ORDERS_URL = addHost("courier/api/getorders");

    public static final String COMPLETE_ORDER_URL = addHost("courier/api/completeorder");

    public static final String REJECT_ORDER_URL = addHost("courier/api/cancelorder");

    public static final String ACTIVATE_CARD_URL = addHost("courier/api/activatecard");

    public static final String UPLOAD_IMAGE_URL_FORMAT = addHost("courier/api/uploadimage/courier_id/%d/request_id/%d/type_id/%d");

    public static String getUploadImageUrl(int courierId, int orderId, int imageTypeId) {
        return String.format(UPLOAD_IMAGE_URL_FORMAT, courierId, orderId, imageTypeId);
    }

    private static String addHost(String subUrl) {
        return Config.HOST + "/" + subUrl;
    }

}
