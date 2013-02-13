package com.idamobile.vpb.courier.model;

import com.idamobile.vpb.courier.network.DataHolder;

public interface Cache {

    <T> DataHolder<T> getHolder(Class<T> clazz);

    <T> DataHolder<T> getHolder(Class<T> clazz, String params);

    <T> DataHolder<T> getHolder(String broadcastAction);

    void clear();
}
