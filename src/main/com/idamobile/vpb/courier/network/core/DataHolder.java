package com.idamobile.vpb.courier.network.core;

import android.content.Context;
import android.content.Intent;

public class DataHolder<T> {

    private Context context;
    private String broadcastAction;

    private T data;

    private boolean loading;
    private long lastUpdateTime;
    private boolean lastUpdateWithErrors;
    private ResponseDTO.ResultCode lastErrorCode;

    private boolean notifyOnChange;

    public DataHolder(Context appContext, String broadcastAction) {
        this.context = appContext;
        this.broadcastAction = broadcastAction;
    }

    public String getBroadcastAction() {
        return broadcastAction;
    }

    public void beginUpdate() {
        notifyOnChange = true;
    }

    public void endUpdate() {
        notifyOnChange = false;
        notifyChanged();
    }

    public void set(T data) {
        this.data = data;
        notifyChanged();
    }

    public void markLoaded(ResponseDTO.ResultCode resultCode) {
        this.loading = false;
        this.lastUpdateTime = System.currentTimeMillis();
        if (resultCode == ResponseDTO.ResultCode.SUCCESS) {
            this.lastErrorCode = null;
            this.lastUpdateWithErrors = false;
        } else {
            this.lastErrorCode = resultCode;
            this.lastUpdateWithErrors = true;
        }
        notifyChanged();
    }

    public void markLoading() {
        this.loading = true;
        notifyChanged();
    }

    public void notifyChanged() {
        if (!notifyOnChange) {
            context.sendBroadcast(new Intent(broadcastAction));
        }
    }

    public T get() {
        return data;
    }

    public boolean isEmpty() {
        return data == null;
    }

    public boolean isLoading() {
        return loading;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public boolean isLastUpdateWithErrors() {
        return lastUpdateWithErrors;
    }

    public ResponseDTO.ResultCode getLastErrorCode() {
        return lastErrorCode;
    }

    public void clear() {
        data = null;
        loading = false;
        notifyChanged();
    }
}
