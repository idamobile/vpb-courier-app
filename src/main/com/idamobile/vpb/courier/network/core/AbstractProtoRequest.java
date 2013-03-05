package com.idamobile.vpb.courier.network.core;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessageLite;
import com.shaubert.protomapper.ProtoMappers;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractProtoRequest<T> extends AbstractRequest<T> {

    private static final ProtoMappers protoMappers = new ProtoMappers();

    public AbstractProtoRequest(String url) {
        super(url);
    }

    public AbstractProtoRequest(String url, HttpMethod method) {
        super(url, method);
    }

    @SuppressWarnings("unchecked")
    protected HttpEntity createHttpPostOrPutEntity() {
        Class thisClass = getClass();
        Object protoClass = protoMappers.getMapper(thisClass, getRequestProtoClass()).mapToProto(this);
        if (protoClass instanceof GeneratedMessageLite) {
            return new ByteArrayEntity(((GeneratedMessageLite) protoClass).toByteArray());
        } else if (protoClass instanceof GeneratedMessage) {
            return new ByteArrayEntity(((GeneratedMessage) protoClass).toByteArray());
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
}
