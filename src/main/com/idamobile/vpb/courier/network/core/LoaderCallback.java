package com.idamobile.vpb.courier.network.core;

import com.idamobile.vpb.courier.ApplicationMediator;

import java.io.Serializable;

/**
 * Callback called by NetworkManager when data ready from network. Used to update model or store data in database.
 * 
 * @author Sergey Royz
 * @since 05/23/2012
 */
public interface LoaderCallback<Q> extends Serializable {

    void onStartLoading(Request<Q> request, ApplicationMediator mediator);

    void onDataReady(Request<Q> request, ResponseDTO<Q> response, ApplicationMediator mediator);

}