package com.idamobile.vpb.courier.network.core;

import android.text.TextUtils;
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
    private Class<Q> qClass;

    public LoaderCallback(String broadcastAction) {
        this.broadcastAction = broadcastAction;
    }

    public LoaderCallback(Class<Q> qClass) {
        this.qClass = qClass;
    }

    public void onStartLoading(Request<Q> request, ApplicationMediator mediator) {
        getHolder(mediator).markLoading();
    }

    protected DataHolder<Q> getHolder(ApplicationMediator mediator) {
        if (!TextUtils.isEmpty(broadcastAction)) {
            return mediator.getCache().getHolder(broadcastAction);
        } else {
            return mediator.getCache().getHolder(qClass);
        }
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
