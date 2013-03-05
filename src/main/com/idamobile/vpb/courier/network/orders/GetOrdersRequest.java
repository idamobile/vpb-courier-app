package com.idamobile.vpb.courier.network.orders;

import com.idamobile.vpb.courier.network.core.AbstractProtoRequest;
import com.idamobile.vpb.courier.network.core.Hosts;
import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Mapper(protoClass = Services.GetOrdersRequestProtobufDTO.class)
@ToString
public class GetOrdersRequest extends AbstractProtoRequest<GetOrdersResponse> {
    @Field private @Getter @Setter int courierId;

    public GetOrdersRequest() {
        super(Hosts.ORDERS_URL);
    }

    @Override
    protected Class<GetOrdersResponse> getResultClass() {
        return GetOrdersResponse.class;
    }

    @Override
    protected Class<?> getRequestProtoClass() {
        return Services.GetOrdersRequestProtobufDTO.class;
    }

    @Override
    protected Class<?> getResultProtoClass() {
        return Services.GetOrdersResponseProtobufDTO.class;
    }

}
