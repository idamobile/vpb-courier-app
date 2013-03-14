package com.idamobile.vpb.courier.network.test;

import com.idamobile.vpb.courier.network.core.Hosts;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestHttpClient implements HttpClient {

    private Map<String, HttpRequestProcessor> processorMap = new HashMap<String, HttpRequestProcessor>();
    private TestUploadImageProcessor testUploadImageProcessor;

    public TestHttpClient() {
        processorMap.put(Hosts.LOGIN_URL, new TestLoginProcessor());
        processorMap.put(Hosts.ORDERS_URL, new TestGetOrdersProcessor());
        processorMap.put(Hosts.COMPLETE_ORDER_URL, new TestSetOrderCompleteProcessor());
        processorMap.put(Hosts.REJECT_ORDER_URL, new TestRejectOrderProcessor());
        processorMap.put(Hosts.ACTIVATE_CARD_URL, new TestActivateCardProcessor());

        testUploadImageProcessor = new TestUploadImageProcessor();
    }

    @Override
    public HttpParams getParams() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpResponse execute(HttpUriRequest httpUriRequest) throws IOException, ClientProtocolException {
        return execute(httpUriRequest, (HttpContext) null);
    }

    @Override
    public HttpResponse execute(HttpUriRequest httpUriRequest, HttpContext httpContext) throws IOException, ClientProtocolException {
        try {
            Thread.sleep(3000);

            String url = httpUriRequest.getURI().toString();
            HttpRequestProcessor processor = processorMap.get(url);
            if (processor == null && isUploadImageUrl(url)) {
                processor = testUploadImageProcessor;
            }

            if (processor != null) {
                return processor.process(httpUriRequest);
            } else {
                throw new IllegalArgumentException("Unable to process URI: " + httpUriRequest.getURI());
            }
        } catch (InterruptedException e) {
        }
        return null;
    }

    private boolean isUploadImageUrl(String url) {
        String uploadUrl = Hosts.UPLOAD_IMAGE_URL_FORMAT.substring(0, Hosts.UPLOAD_IMAGE_URL_FORMAT.indexOf("/%"));
        return url.startsWith(uploadUrl);
    }

    @Override
    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest) throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException();
    }
}
