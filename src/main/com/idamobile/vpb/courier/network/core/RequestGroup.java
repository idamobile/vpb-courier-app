package com.idamobile.vpb.courier.network.core;

import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.util.Logger;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.Serializable;
import java.util.*;

public class RequestGroup implements Request<RequestGroup.ModelCollection> {

    private static final String TAG = RequestGroup.class.getSimpleName();

    protected Set<Request<?>> requests;
    private final String uuid = UUID.randomUUID().toString();
    private ExecutionPolicy executionPolicy = new AbortOnFirstFailExecutionPolicy();

    public RequestGroup() {
        requests = new HashSet<Request<?>>();
    }

    public RequestGroup addRequest(Request<?> request) {
        requests.add(request);
        return this;
    }

    public void setExecutionPolicy(ExecutionPolicy executionPolicy) {
        this.executionPolicy = executionPolicy;
    }

    @Override
    public ResponseDTO<ModelCollection> execute(ApplicationMediator mediator, HttpClient httpClient, HttpContext httpContext) {
        ResponseDTO<ModelCollection> result;
        ModelCollection modelCollection = new ModelCollection();

        Logger.debug(TAG, "executing " + toString());
        ResponseDTO<?> lastResponse = null;
        boolean aborted = false;
        for (Request<?> request : requests) {
            lastResponse = executeRequest(mediator, request, httpClient, httpContext, modelCollection);
            aborted = !executionPolicy.shouldContinue(lastResponse);
            if (aborted) {
                break;
            }
        }

        if (aborted && !lastResponse.isSuccess()) {
            result = ResponseDTO.newFailureResponse(lastResponse.getResultCode(), lastResponse.getErrorMessage());
        } else {
            result = ResponseDTO.newSuccessfulResponse(modelCollection);
        }

        return result;
    }

    protected ResponseDTO<?> executeRequest(ApplicationMediator mediator, Request<?> request, HttpClient httpClient,
            HttpContext httpContext, ModelCollection modelCollection) {
        ResponseDTO<?> resp = request.execute(mediator, httpClient, httpContext);
        modelCollection.responseMap.put(request.getRequestUuid(), resp);
        return resp;
    }

    @Override
    public LoaderCallback<ModelCollection> getUpdateModelCallback() {
        return new DefaultLoaderCallback<ModelCollection>("") {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public void onStartLoading(Request<ModelCollection> request, ApplicationMediator mediator) {
                for (Request r : requests) {
                    LoaderCallback<?> callback = r.getUpdateModelCallback();
                    if (callback != null) {
                        callback.onStartLoading(r, mediator);
                    }
                }
            }

            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public void onDataReady(Request<ModelCollection> request, ResponseDTO<ModelCollection> response, ApplicationMediator mediator) {
                for (Request r : requests) {
                    LoaderCallback<?> callback = r.getUpdateModelCallback();
                    ResponseDTO responseForRequest = response.getData() != null
                            ? response.getData().getResponseForRequest(r) : response;
                    if (callback != null && responseForRequest != null) {
                        callback.onDataReady(r, responseForRequest, mediator);
                    }
                }
            }
        };
    }

    @Override
    public String getRequestUuid() {
        return uuid;
    }

    @Override
    public void cancel(boolean interrupt) {
        for (Request<?> request : requests) {
            request.cancel(interrupt);
        }
    }

    @Override
    public String toString() {
        return "RequestGroup [requests=" + requests + ", uuid=" + uuid + "]";
    }

    public static interface ExecutionPolicy extends Serializable {
        boolean shouldContinue(ResponseDTO responseDTO);
    }

    public static class AbortOnFirstFailExecutionPolicy implements ExecutionPolicy {
        @Override
        public boolean shouldContinue(ResponseDTO responseDTO) {
            return responseDTO.isSuccess();
        }
    }

    public static class ModelCollection implements Serializable {
        private Map<String, ResponseDTO<?>> responseMap = new HashMap<String, ResponseDTO<?>>();

        public boolean hasResponseForRequest(Request<?> request) {
            return hasResponseForUuid(request.getRequestUuid());
        }

        public boolean hasResponseForUuid(String uuid) {
            return responseMap.containsKey(uuid);
        }

        public <T> ResponseDTO<T> getResponseForRequest(Request<T> request) {
            return getResponseForUuid(request.getRequestUuid());
        }

        @SuppressWarnings("unchecked")
        public <T> ResponseDTO<T> getResponseForUuid(String uuid) {
            return (ResponseDTO<T>) responseMap.get(uuid);
        }

        public Map<String, ResponseDTO<?>> getResponseMap() {
            return responseMap;
        }
    }
}
