package com.idamobile.vpb.courier.network.core;

import org.apache.http.client.HttpClient;

public interface HttpClientFactory {

    public HttpClient createClient();

}
