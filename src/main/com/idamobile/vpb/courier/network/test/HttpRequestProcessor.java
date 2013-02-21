package com.idamobile.vpb.courier.network.test;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public interface HttpRequestProcessor {

    HttpResponse process(HttpUriRequest request) throws IOException;

}
