package com.idamobile.vpb.courier.network.orders;

import com.idamobile.vpb.courier.network.core.AbstractRequest;
import com.idamobile.vpb.courier.network.core.Hosts;
import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Mapper(protoClass = Services.ActivateCardRequestProtobufDTO.class)
public class ActivateCardRequest extends AbstractRequest<UpdateOrderResponse> {
    private @Field @Getter @Setter int orderId;

    public ActivateCardRequest() {
        super(Hosts.ACTiVATE_CARD_URL);
    }

    @Override
    protected Class<UpdateOrderResponse> getResultClass() {
        return UpdateOrderResponse.class;
    }

    @Override
    protected Class<?> getRequestProtoClass() {
        return Services.ActivateCardRequestProtobufDTO.class;
    }

    @Override
    protected Class<?> getResultProtoClass() {
        return Services.UpdateOrderResponseProtobufDTO.class;
    }
}
