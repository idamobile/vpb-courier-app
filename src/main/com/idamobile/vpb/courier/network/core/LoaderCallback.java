package com.idamobile.vpb.courier.network.core;

import com.idamobile.vpb.courier.ApplicationMediator;

import java.io.Serializable;

/**
 * Callback called by NetworkManager when data ready from network. Used to update model or store data in database.
 * 
 * @author Sergey Royz
 * @since 05/23/2012
 */
public class LoaderCallback<Q> implements Serializable {

    private String broadcastAction;

    protected LoaderCallback(String broadcastAction) {
        this.broadcastAction = broadcastAction;
    }

    public void onStartLoading(Request<Q> request, ApplicationMediator mediator) {
        getHolder(mediator).markLoading();
    }

    protected DataHolder<Q> getHolder(ApplicationMediator mediator) {
        return mediator.getCache().getHolder(broadcastAction);
    }

    public void onDataReady(Request<Q> request, ResponseDTO<Q> response, ApplicationMediator mediator) {
        DataHolder<Q> holder = getHolder(mediator);
        holder.beginUpdate();
        if (response.isSuccess()) {
            holder.set(response.getData());
        }
        holder.markLoaded(!response.isSuccess());
        holder.endUpdate();
    }
}
