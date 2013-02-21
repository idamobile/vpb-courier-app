package com.idamobile.vpb.courier.network.test;

import com.idamobile.vpb.courier.network.core.HttpClientFactory;
import org.apache.http.client.HttpClient;

public class TestHttpClientFactory implements HttpClientFactory {

    @Override
    public HttpClient createClient() {
        return new TestHttpClient();
    }

}
