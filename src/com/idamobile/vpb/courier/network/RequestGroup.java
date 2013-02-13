package com.idamobile.vpb.courier.network;

import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.util.Logger;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.Serializable;
import java.util.*;

public class RequestGroup implements Request<RequestGroup.ModelCollection> {

    private static final String TAG = RequestGroup.class.getSimpleName();

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
    }

    protected Set<Request<?>> requests;
    private final String uuid = UUID.randomUUID().toString();
    private boolean shouldStopOnFirstError = true;

    public RequestGroup() {
        requests = new HashSet<Request<?>>();
    }

    public RequestGroup addRequest(Request<?> request) {
        requests.add(request);
        return this;
    }

    public void setShouldStopOnFirstError(boolean shouldStopOnFirstError) {
        this.shouldStopOnFirstError = shouldStopOnFirstError;
    }

    @Override
    public ResponseDTO<ModelCollection> execute(HttpClient httpClient, HttpContext httpContext) {
        ResponseDTO<ModelCollection> result;
        ModelCollection modelCollection = new ModelCollection();

        Logger.debug(TAG, "executing " + toString());
        ResponseDTO<?> failedResp = null;
        for (Request<?> request : requests) {
            failedResp = executeRequest(request, httpClient, httpContext, modelCollection);
            if (failedResp != null && shouldStopOnFirstError) {
                break;
            }
        }

        if (failedResp == null || !shouldStopOnFirstError) {
            result = ResponseDTO.newSuccessfulResponse(modelCollection);
        } else {
            result = ResponseDTO.newFailureResponse(failedResp.getResultCode(), failedResp.getErrorMessage());
        }

        return result;
    }

    protected ResponseDTO<?> executeRequest(Request<?> request, HttpClient httpClient,
            HttpContext httpContext, ModelCollection modelCollection) {
        ResponseDTO<?> resp = request.execute(httpClient, httpContext);
        modelCollection.responseMap.put(request.getRequestUuid(), resp);

        if (!resp.isSuccess()) {
            return resp;
        } else {
            return null;
        }
    }

    @Override
    public LoaderCallback<ModelCollection> getUpdateModelCallback() {
        return new LoaderCallback<ModelCollection>(null) {
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
                    ResponseDTO responseForRequest = response.getData().getResponseForRequest(r);
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
    public void cancel() {
        for (Request<?> request : requests) {
            request.cancel();
        }
    }

    @Override
    public String toString() {
        return "RequestGroup [requests=" + requests + ", uuid=" + uuid + "]";
    }

}
