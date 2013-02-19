package com.idamobile.vpb.courier.navigation;


public interface NavigationController extends NavigationListener {

    NavigationMethod getLogin();

    NavigationMethod getOrdersList();

    void processSuccessLogin();

    void processSignOut();

}