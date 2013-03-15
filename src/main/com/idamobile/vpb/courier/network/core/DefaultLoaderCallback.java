package com.idamobile.vpb.courier.network.core;

import android.text.TextUtils;
import com.idamobile.vpb.courier.ApplicationMediator;

/**
 * Callback called by NetworkManager when data ready from network. Used to update model or store data in database.
 *
 * @author Sergey Royz
 * @since 05/23/2012
 */
public class DefaultLoaderCallback<Q> implements LoaderCallback<Q> {

    private String broadcastAction;
    private Class<Q> qClass;

    public DefaultLoaderCallback(String broadcastAction) {
        this.broadcastAction = broadcastAction;
    }

    public DefaultLoaderCallback(Class<Q> qClass) {
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
            Q result = mergeResult(holder.get(), response.getData());
            holder.set(result);
            onSuccess(request, result, mediator);
        } else {
            onError(request, response, mediator);
        }
        holder.markLoaded(response.getResultCode());
        holder.endUpdate();
    }

    protected Q mergeResult(Q oldData, Q newData) {
        return newData;
    }

    protected void onError(Request<Q> request, ResponseDTO<Q> response, ApplicationMediator mediator) {
    }

    protected void onSuccess(Request<Q> request, Q data, ApplicationMediator mediator) {
    }
}
