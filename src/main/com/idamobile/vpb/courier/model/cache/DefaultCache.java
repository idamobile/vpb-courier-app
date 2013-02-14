package com.idamobile.vpb.courier.model.cache;

import android.content.Context;
import com.idamobile.vpb.courier.network.core.DataHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCache implements Cache {

    private Context context;
    private Map<String, DataHolder<?>> dataHolderMap;
    private BroadcastActionMapper broadcastActionMapper;

    public DefaultCache(Context appContext, BroadcastActionMapper mapper) {
        this.context = appContext;
        this.broadcastActionMapper = mapper;
        this.dataHolderMap = new ConcurrentHashMap<String, DataHolder<?>>();
    }

    @Override
    public <T> DataHolder<T> getHolder(Class<T> clazz) {
        return getHolder(clazz, null);
    }

    @Override
    public <T> DataHolder<T> getHolder(Class<T> clazz, String params) {
        return getHolder(broadcastActionMapper.getBroadcatsFor(clazz, params));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> DataHolder<T> getHolder(String broadcastAction) {
        DataHolder<T> holder = (DataHolder<T>) dataHolderMap.get(broadcastAction);
        if (holder == null) {
            holder = new DataHolder<T>(context, broadcastAction);
            dataHolderMap.put(broadcastAction, holder);
        }
        return holder;
    }

    @Override
    public void clear() {
        for (DataHolder<?> holder : dataHolderMap.values()) {
            holder.clear();
        }
        dataHolderMap.clear();
    }
}