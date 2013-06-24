package com.idamobile.vpb.courier.model;

import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;

@Mapper(protoClass = Services.OrderType.class, isEnum = true)
public enum OrderType {
    ORDER_TYPE_DELIVER_INSTABANK_CARD(1);

    @Field
    public final int code;

    private OrderType(int code) {
        this.code = code;
    }
}
