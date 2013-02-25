package com.idamobile.vpb.courier.navigation;


public interface NavigationController extends NavigationListener {

    NavigationMethod getLogin();

    NavigationMethod getOrdersList();

    NavigationMethod getOrderDetails();

    void processSuccessLogin();

    void processSignOut();

}