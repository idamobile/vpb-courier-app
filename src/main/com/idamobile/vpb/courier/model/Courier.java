package com.idamobile.vpb.courier.model;

import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Data;

import java.io.Serializable;

@Mapper(protoClass = Services.CourierProtobufDTO.class)
public @Data class Courier implements Serializable {
    private @Field String firstName;
    private @Field String lastName;
    private @Field int id;
    private @Field int completedOrders;
}
