package com.idamobile.vpb.courier.network.orders;

import com.idamobile.vpb.courier.model.ImageType;
import com.idamobile.vpb.courier.model.OrderStatus;
import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Mapper(protoClass = Services.UpdateOrderResponseProtobufDTO.class)
@Data
public class UpdateOrderResponse implements Serializable {
    @Field private int orderId;
    @Field private OrderStatus newStatus;
    @Field private List<ImageType> imageTypes = new ArrayList<ImageType>();
}