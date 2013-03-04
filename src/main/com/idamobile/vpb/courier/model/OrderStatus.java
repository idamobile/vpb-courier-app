package com.idamobile.vpb.courier.model;

import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;

@Mapper(protoClass = Services.OrderStatus.class, isEnum = true)
public enum OrderStatus {
    STATUS_NEW(1),
    STATUS_DOCUMENTS_SUBMITTED(2),
    STATUS_DOCUMENTS_NOT_SUBMITTED(3),
    STATUS_ACTIVATED(4);

    @Field
    public final int code;

    private OrderStatus(int code) {
        this.code = code;
    }
}
