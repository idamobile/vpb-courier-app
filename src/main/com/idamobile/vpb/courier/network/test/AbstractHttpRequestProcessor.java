package com.idamobile.vpb.courier.network.test;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

public abstract class AbstractHttpRequestProcessor implements HttpRequestProcessor {

    protected HttpResponse makeResponse(int code) {
        StatusLine statusLine = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), code, "blah");
        return new BasicHttpResponse(statusLine);
    }

    protected HttpResponse makeResponse(byte[] data) {
        StatusLine statusLine = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "blah");
        HttpResponse response = new BasicHttpResponse(statusLine);
        ByteArrayEntity entity = new ByteArrayEntity(data);
        response.setEntity(entity);
        return response;
    }

}
