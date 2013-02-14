package com.idamobile.vpb.courier.model.cache;

public class DefaultFullNameMapper implements BroadcastActionMapper {

    @Override
    public String getBroadcatsFor(Class<?> clazz, String params) {
        return clazz.getName() + "-" + params;
    }
}
