package com.idamobile.vpb.courier.network.orders;

import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Mapper(protoClass = Services.GetOrdersResponseProtobufDTO.class)
@Data
public class GetOrdersResponse implements Serializable {
    @Field private List<Order> orders = new ArrayList<Order>();
}
