package com.idamobile.vpb.courier.network.test;

import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.model.OrderStatus;
import com.idamobile.vpb.courier.network.orders.GetOrdersResponse;
import com.idamobile.vpb.courier.network.orders.GetOrdersResponseMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.Random;

public class TestGetOrdersProcessor extends AbstractHttpRequestProcessor {

    private String[] firstNames = {
            "Ибгарим",
            "Яков",
            "Моисей",
            "Боб",
            "Жора",
            "Ипостас",
            "Емельян",
    };

    private String[] secondNames = {
            "Корешков",
            "Грибков",
            "Яйцев",
            "Кондрашкин",
            "Жиденнький",
            "Валенков",
            "Шарушкин",
    };

    private String[] middleNames = {
            "Семенович",
            null,
            "Александрович",
            "Константинович",
            "Дмитриевич",
            "Петров",
            null,
    };

    private String[] address = {
        "ул. Гостиничная, д. 9 оф. 215",
        "ул. Коненкова, д. 9 кв. 115",
        "ул. Первомайская, д. 30/7 кв. 115",
        "ул. Первомайская, д. 30 корп. 7 кв. 115",
    };

    private String[] subwayNames = {
            "Кропоткинская",
            "Библиотека им. Ленина",
            "Театральная",
            "Тверская",
            "Владыкино",
            "Комсомольская",
            "Новослободская",
    };

    private Random random = new Random();

    @Override
    public HttpResponse process(HttpUriRequest request) throws IOException {
        return makeResponse(new GetOrdersResponseMapper().mapToProto(createOrders(12)).toByteArray());
    }

    private GetOrdersResponse createOrders(int count) {
        GetOrdersResponse response = new GetOrdersResponse();
        for (int i = 0; i < count; i++) {
            response.getOrders().add(createOrder(i));
        }
        return response;
    }

    private Order createOrder(int id) {
        Order order = new Order();
        order.setClientFirstName(getRandomString(firstNames));
        order.setClientSecondName(getRandomString(secondNames));
        order.setClientMiddleName(getRandomString(middleNames));
        order.setClientAddress(getRandomString(address));
        order.setClientPhone("+7 912 372-43-12");
        order.setSubway(getRandomString(subwayNames));
        order.setOrderType("");
        order.setId(id);

        long startTime = System.currentTimeMillis() + (5 - random.nextInt(8)) * 60 * 60 * 1000;
        long endTime = startTime + 2 * 60 * 60 * 1000;
        order.setMeetTimeFrom(startTime);
        order.setMeetTimeTo(endTime);

        switch (random.nextInt(4)) {
            case 0:
                order.setStatus(OrderStatus.STATUS_DOCUMENTS_NOT_SUBMITTED);
                break;
            case 1:
                order.setStatus(OrderStatus.STATUS_DOCUMENTS_SUBMITTED);
                break;
            default:
                order.setStatus(OrderStatus.STATUS_NEW);
                break;
        }

        return order;
    }

    private String getRandomString(String[] source) {
        return source[random.nextInt(source.length)];
    }
}
