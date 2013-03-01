package com.idamobile.vpb.courier.network.test;

import com.idamobile.vpb.courier.model.OrderStatus;
import com.idamobile.vpb.courier.network.orders.CancelOrderRequest;
import com.idamobile.vpb.courier.network.orders.CancelOrderRequestMapper;
import com.idamobile.vpb.courier.network.orders.UpdateOrderResponse;
import com.idamobile.vpb.courier.network.orders.UpdateOrderResponseMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public class TestRejectOrderProcessor extends AbstractHttpRequestProcessor {

    @Override
    public HttpResponse process(HttpUriRequest request) throws IOException {
        HttpPost post = (HttpPost) request;
        CancelOrderRequestMapper mapper = new CancelOrderRequestMapper();
        CancelOrderRequest cancelOrderRequest = mapper.mapFromProto(post.getEntity().getContent());
        UpdateOrderResponse response = new UpdateOrderResponse();
        response.setOrderId(cancelOrderRequest.getOrderId());
        response.setNewStatus(OrderStatus.STATUS_DOCUMENTS_NOT_SUBMITTED);
        UpdateOrderResponseMapper responseMapper = new UpdateOrderResponseMapper();
        return makeResponse(responseMapper.mapToProto(response).toByteArray());
    }

}
