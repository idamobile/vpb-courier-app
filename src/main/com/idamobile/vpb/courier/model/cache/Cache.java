package com.idamobile.vpb.courier.model.cache;

import com.idamobile.vpb.courier.network.core.DataHolder;

public interface Cache {

    <T> DataHolder<T> getHolder(Class<T> clazz);

    <T> DataHolder<T> getHolder(Class<T> clazz, String params);

    <T> DataHolder<T> getHolder(String broadcastAction);

    void clear();
}
