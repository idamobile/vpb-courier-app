package com.idamobile.vpb.courier.network.login;

import com.idamobile.vpb.courier.network.AbstractRequest;

import java.io.IOException;
import java.io.InputStream;

public class LoginRequest extends AbstractRequest{

    public LoginRequest(String url) {
        super(url);
    }

    @Override
    protected byte[] createHttpPostOrPutEntity() {
        return new byte[0];
    }

    @Override
    protected Object parseResponseEntity(InputStream inputStream) throws IOException {
        return null;
    }
}
