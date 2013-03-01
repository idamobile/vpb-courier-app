package com.idamobile.vpb.courier.network.orders;

import com.idamobile.vpb.courier.network.core.AbstractRequest;
import com.idamobile.vpb.courier.network.core.Hosts;
import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Mapper(protoClass = Services.SetOrderCompletedProtobufDTO.class)
@ToString
public class SetOrderCompletedRequest extends AbstractRequest<UpdateOrderResponse>{
    @Field @Getter @Setter private int orderId;
    @Field(optional = true) @Getter @Setter private Boolean resident;
    @Field(optional = true) @Getter @Setter private Boolean hasCorrections;

    public SetOrderCompletedRequest() {
        super(Hosts.COMPLETE_ORDER_URL);
    }

    @Override
    protected Class<UpdateOrderResponse> getResultClass() {
        return UpdateOrderResponse.class;
    }

    @Override
    protected Class<?> getRequestProtoClass() {
        return Services.SetOrderCompletedProtobufDTO.class;
    }

    @Override
    protected Class<?> getResultProtoClass() {
        return Services.UpdateOrderResponseProtobufDTO.class;
    }
}
