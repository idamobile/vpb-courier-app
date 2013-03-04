package com.idamobile.vpb.courier.network.test;

import com.idamobile.vpb.courier.model.OrderStatus;
import com.idamobile.vpb.courier.network.orders.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public class TestActivateCardProcessor extends AbstractHttpRequestProcessor {

    @Override
    public HttpResponse process(HttpUriRequest request) throws IOException {
        HttpPost post = (HttpPost) request;
        ActivateCardRequestMapper mapper = new ActivateCardRequestMapper();
        ActivateCardRequest activateCardRequest = mapper.mapFromProto(post.getEntity().getContent());
        UpdateOrderResponse response = new UpdateOrderResponse();
        response.setOrderId(activateCardRequest.getOrderId());
        response.setNewStatus(OrderStatus.STATUS_ACTIVATED);
        UpdateOrderResponseMapper responseMapper = new UpdateOrderResponseMapper();
        return makeResponse(responseMapper.mapToProto(response).toByteArray());
    }

}
