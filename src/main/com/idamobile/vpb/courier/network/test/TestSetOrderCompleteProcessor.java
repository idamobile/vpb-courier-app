package com.idamobile.vpb.courier.network.test;

import com.idamobile.vpb.courier.model.ImageType;
import com.idamobile.vpb.courier.model.OrderStatus;
import com.idamobile.vpb.courier.network.orders.CompleteOrderRequestMapper;
import com.idamobile.vpb.courier.network.orders.CompleteOrderRequest;
import com.idamobile.vpb.courier.network.orders.UpdateOrderResponse;
import com.idamobile.vpb.courier.network.orders.UpdateOrderResponseMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.Random;

public class TestSetOrderCompleteProcessor extends AbstractHttpRequestProcessor {

    private Random random = new Random();

    @Override
    public HttpResponse process(HttpUriRequest request) throws IOException {
        HttpPost post = (HttpPost) request;
        CompleteOrderRequestMapper mapper = new CompleteOrderRequestMapper();
        CompleteOrderRequest completedRequest = mapper.mapFromProto(post.getEntity().getContent());
        UpdateOrderResponse response = new UpdateOrderResponse();
        response.setOrderId(completedRequest.getOrderId());
        response.setNewStatus(OrderStatus.STATUS_DOCUMENTS_SUBMITTED);
        int count = 1 + random.nextInt(5);
        for (int i = 0; i < count; i++) {
            ImageType imageType = new ImageType();
            imageType.setId(i);
            imageType.setDescription("Описание фотографии " + (i + 1));
            imageType.setRequiredImg(random.nextBoolean());
            response.getImageTypes().add(imageType);
        }
        UpdateOrderResponseMapper responseMapper = new UpdateOrderResponseMapper();
        return makeResponse(responseMapper.mapToProto(response).toByteArray());
    }

}
