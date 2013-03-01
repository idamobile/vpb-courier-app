package com.idamobile.vpb.courier.network.core;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessageLite;
import com.idamobile.vpb.courier.config.Config;
import com.idamobile.vpb.courier.util.HttpUtils;
import com.idamobile.vpb.courier.util.Logger;
import com.shaubert.protomapper.ProtoMappers;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@SuppressWarnings("serial")
public abstract class AbstractRequest<T> implements Request<T> {

    private static final ProtoMappers protoMappers = new ProtoMappers();

    public static enum HttpMethod {
        GET,
        POST,
        PUT,
        DELETE
    }

    private final String TAG = this.getClass().getSimpleName();

    private @Getter String url;
    private HttpMethod method;

    private final String uuid = UUID.randomUUID().toString();
    private @Getter boolean cancelled;

    private @Getter @Setter
    LoaderCallback<T> updateModelCallback;

    public AbstractRequest(String url) {
        this(url, HttpMethod.POST);
    }

    public AbstractRequest(String url, HttpMethod method) {
        this.url = url;
        this.method = method;
    }

    protected String formatFinalUrl(String url) {
        return url;
    }

    @Override
    public String getRequestUuid() {
        return uuid;
    }

    public HttpUriRequest createHttpRequest() {
        String finalUrl = formatFinalUrl(url);
        HttpRequestBase request = createRequest(finalUrl);
        if (request instanceof HttpEntityEnclosingRequestBase) {
            HttpEntityEnclosingRequestBase enclosingRequest = (HttpEntityEnclosingRequestBase) request;
            byte[] postEntity = createHttpPostOrPutEntity();
            if (postEntity != null) {
                enclosingRequest.setEntity(new ByteArrayEntity(postEntity));
            }
        }
        return request;
    }

    @Override
    public ResponseDTO<T> execute(HttpClient httpClient, HttpContext httpContext) {
        ResponseDTO<T> responseDTO;
        HttpResponse httpResponse;
        try {
            HttpUriRequest httpRequest = createHttpRequest();
            Logger.network(TAG, "Accessing: " + httpRequest.getURI() + " with request: " + this);
            CookieStore cookieStore = (CookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);
            Logger.network(TAG, "Cookies: " + (cookieStore != null ? cookieStore.toString() : "null"));

            //TODO: remove this debug trash
            if (Config.HOST.contains("89.221.54.169")) {
                httpRequest.addHeader("Host", "api.peter.vpb.su:8668");
            }

            httpResponse = httpClient.execute(httpRequest, httpContext);
            HttpStatusCodeProcessor codeProcessor = new HttpStatusCodeProcessor();
            ResponseDTO.ResultCode resultCode = codeProcessor.parse(httpResponse);
            if (resultCode == ResponseDTO.ResultCode.SUCCESS) {
                Logger.debug(TAG, "Content-Lenght: " + HttpUtils.getContentLength(httpResponse));
                responseDTO = parseResponse(httpResponse);
            } else {
                responseDTO = ResponseDTO.newFailureResponse(resultCode,
                        "Status code " + httpResponse.getStatusLine().getStatusCode());
            }
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                entity.consumeContent();
            }
            Logger.network(TAG, "Parsed response from " + getUrl() + ": " + responseDTO);
        } catch (Exception e) {
            Logger.error(TAG, "Network error", e);
            responseDTO = ResponseDTO.newFailureResponse(ResponseDTO.ResultCode.NETWORK_ERROR, e.getMessage());
        }
        return responseDTO;
    }

    private HttpRequestBase createRequest(String finalUrl) {
        switch (method) {
        case GET:
            return NetworkUtils.createGetRequest(finalUrl);
        case POST:
            return NetworkUtils.createPostRequest(finalUrl);
        case DELETE:
            return NetworkUtils.createDeleteRequest(finalUrl);
        case PUT:
            return NetworkUtils.createPutRequest(finalUrl);
        }
        throw new IllegalStateException("Unknown http method: " + method);
    }

    @SuppressWarnings("unchecked")
    protected byte[] createHttpPostOrPutEntity() {
        Class thisClass = getClass();
        Object protoClass = protoMappers.getMapper(thisClass,
                getRequestProtoClass()).mapToProto(this);
        if (protoClass instanceof GeneratedMessageLite) {
            return  ((GeneratedMessageLite) protoClass).toByteArray();
        } else if (protoClass instanceof GeneratedMessage) {
            return  ((GeneratedMessage) protoClass).toByteArray();
        } else {
            throw new IllegalStateException("Unknown protobuf class: " + protoClass);
        }
    }

    protected abstract Class<T> getResultClass();

    protected abstract Class<?> getRequestProtoClass();

    protected abstract Class<?> getResultProtoClass();

    protected T parseResponseEntity(InputStream inputStream) throws IOException {
        return protoMappers.getMapper(getResultClass(), getResultProtoClass()).mapFromProto(inputStream);
    }

    public ResponseDTO<T> parseResponse(HttpResponse httpResponse) {
        ResponseDTO<T> responseDTO;
        try {
            HttpEntity entity = httpResponse.getEntity();
            final T response;
            if (entity != null) {
                InputStream stream = entity.getContent();
                response = parseResponseEntity(stream);
            } else {
                response = parseResponseEntity(null);
            }
            responseDTO = ResponseDTO.newSuccessfulResponse(response);
        } catch (Exception e) {
            responseDTO = ResponseDTO.newFailureResponse(ResponseDTO.ResultCode.PROTOCOL_ERROR, e.getMessage());
            Logger.error(TAG, "Unable to parse response", e);
        }

        return responseDTO;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }
}
