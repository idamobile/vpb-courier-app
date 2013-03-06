package com.idamobile.vpb.courier.network.orders;

import com.idamobile.vpb.courier.network.core.AbstractProtoRequest;
import com.idamobile.vpb.courier.network.core.Hosts;
import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Mapper(protoClass = Services.CompleteOrderProtobufDTO.class)
@ToString
public class CompleteOrderRequest extends AbstractProtoRequest<UpdateOrderResponse> {
    @Field @Getter @Setter private int orderId;
    @Field(optional = true) @Getter @Setter private Boolean resident;
    @Field(optional = true) @Getter @Setter private Boolean hasCorrections;

    public CompleteOrderRequest() {
        super(Hosts.COMPLETE_ORDER_URL);
    }

    @Override
    protected Class<UpdateOrderResponse> getResultClass() {
        return UpdateOrderResponse.class;
    }

    @Override
    protected Class<?> getRequestProtoClass() {
        return Services.CompleteOrderProtobufDTO.class;
    }

    @Override
    protected Class<?> getResultProtoClass() {
        return Services.UpdateOrderResponseProtobufDTO.class;
    }
}
