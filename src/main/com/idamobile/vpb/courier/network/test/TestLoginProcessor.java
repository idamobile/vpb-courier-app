package com.idamobile.vpb.courier.network.test;

import com.idamobile.vpb.courier.model.Courier;
import com.idamobile.vpb.courier.network.login.*;
import com.idamobile.vpb.courier.security.crypto.Hashs;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.Random;

public class TestLoginProcessor extends AbstractHttpRequestProcessor {

    private Random random = new Random();

    @Override
    public HttpResponse process(HttpUriRequest request) throws IOException {
        HttpPost post = (HttpPost) request;
        LoginRequest loginRequest = new LoginRequestMapper().mapFromProto(post.getEntity().getContent());
        if ("test".equals(loginRequest.getLogin())) {
            LoginResponse response = new LoginResponse();
            response.setCourierInfo(createInfo());
            response.setLoginResult(LoginResult.OK);
            response.setKeyHash(Hashs.getSHA1("test"));
            return makeResponse(response);
        } else if ("blocked".equals(loginRequest.getLogin())) {
            LoginResponse response = new LoginResponse();
            response.setLoginResult(LoginResult.BLOCKED_ACCOUNT);
            return makeResponse(response);
        } else {
            LoginResponse response = new LoginResponse();
            response.setLoginResult(LoginResult.WRONG_CREDENTIALS);
            return makeResponse(response);
        }
    }

    private Courier createInfo() {
        Courier courier = new Courier();
        courier.setFirstName("Яков");
        courier.setLastName("Подшабашник");
        courier.setId(1);
        courier.setCompletedOrders(random.nextInt(1000));
        return courier;
    }

    private HttpResponse makeResponse(LoginResponse response) {
        LoginResponseMapper mapper = new LoginResponseMapper();
        return makeResponse(mapper.mapToProto(response).toByteArray());
    }
}
