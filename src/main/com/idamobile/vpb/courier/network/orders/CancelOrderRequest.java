package com.idamobile.vpb.courier.network.orders;

import com.idamobile.vpb.courier.model.CancellationReason;
import com.idamobile.vpb.courier.network.core.AbstractProtoRequest;
import com.idamobile.vpb.courier.network.core.Hosts;
import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Mapper(protoClass = Services.CancelOrderRequestProtobufDTO.class)
@ToString
public class CancelOrderRequest extends AbstractProtoRequest<UpdateOrderResponse> {
    @Field @Getter @Setter private int orderId;
    @Field @Getter @Setter CancellationReason reason;
    @Field(optional = true) @Getter @Setter Boolean metWithClient;
    @Field(optional = true) @Getter @Setter String comment;

    public CancelOrderRequest() {
        super(Hosts.REJECT_ORDER_URL);
    }

    @Override
    protected Class<UpdateOrderResponse> getResultClass() {
        return UpdateOrderResponse.class;
    }

    @Override
    protected Class<?> getRequestProtoClass() {
        return Services.CancelOrderRequestProtobufDTO.class;
    }

    @Override
    protected Class<?> getResultProtoClass() {
        return Services.UpdateOrderResponseProtobufDTO.class;
    }
}
