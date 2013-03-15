package com.idamobile.vpb.courier.network.core;

import android.os.AsyncTask;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.util.Logger;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.util.HashMap;
import java.util.Map;

public class NetworkManager {
    private static final String TAG = NetworkManager.class.getSimpleName();

    public interface RequestListener<T> {
        void onFinished(Request<T> request, ResponseDTO<T> responseDTO);

        void onCancelled(Request<T> request);
    }

    private HttpContext httpContext;
    private HttpClient httpClient;
    private CookieStore cookieStore;
    private ApplicationMediator mediator;

    private Map<String, LoaderTask<?>> tasks = new HashMap<String, LoaderTask<?>>();

    private ResponsePreProcessorGroup preProcessorGroup;

    public NetworkManager(ApplicationMediator mediator, HttpClientFactory httpClientFactory) {
        this.mediator = mediator;
        this.preProcessorGroup = new ResponsePreProcessorGroup();

        initHttpClient(httpClientFactory);
    }

    public void addProcessor(ResponseDTOPreProcessor processor) {
        preProcessorGroup.addProcessor(processor);
    }

    public void removeProcessor(ResponseDTOPreProcessor processor) {
        preProcessorGroup.removeProcessor(processor);
    }

    private void initHttpClient(HttpClientFactory httpClientFactory) {
        httpClient = httpClientFactory.createClient();
        HttpUtils.enableGzip(httpClient);
        cleanUpSession();
    }

    public void cleanUpSession() {
        cookieStore = new BasicCookieStore();
        httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    public void addCookie(Cookie cookie) {
        cookieStore.addCookie(cookie);
    }

    public void clearCookies() {
        cookieStore.clear();
    }

    public <Q> boolean execute(Request<Q> request, RequestListener<Q> requestListener) {
        @SuppressWarnings("unchecked")
        LoaderTask<Q> task = (LoaderTask<Q>) tasks.get(request.getRequestUuid());
        if (task != null) {
            if (task.getStatus() != AsyncTask.Status.FINISHED) {
                Logger.debug(TAG, String.format("Task for request %s is already running", request), null);
                return false;
            }
        }
        task = new LoaderTask<Q>(request, requestListener);
        tasks.put(request.getRequestUuid(), task);

        task.execute((Void[]) null);
        return true;
    }

    public <Q> boolean cancel(Request<Q> request, boolean mayInterruptIfRunning) {
        @SuppressWarnings("unchecked")
        LoaderTask<Q> task = (LoaderTask<Q>) tasks.get(request.getRequestUuid());
        if (task != null) {
            task.cancel(mayInterruptIfRunning);
            return true;
        } else {
            return false;
        }
    }

    class LoaderTask<T> extends AsyncTask<Void, Void, ResponseDTO<T>> {

        private Request<T> request;
        private RequestListener<T> requestListener;

        public LoaderTask(Request<T> request, RequestListener<T> requestListener) {
            this.request = request;
            this.requestListener = requestListener;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        protected void onPreExecute() {
            LoaderCallback updateModelCallback = request.getUpdateModelCallback();
            if (updateModelCallback != null) {
                updateModelCallback.onStartLoading(request, mediator);
            }
        }

        @Override
        protected ResponseDTO<T> doInBackground(Void... params) {
            return request.execute(mediator, httpClient, httpContext);
        }

        @Override
        protected void onPostExecute(ResponseDTO<T> result) {
            LoaderCallback<T> updateModelCallback = request.getUpdateModelCallback();
            if (result != null) {
                preProcessorGroup.processResponse(result);
                if (updateModelCallback != null) {
                    updateModelCallback.onDataReady(request, result, mediator);
                }
            }
            tasks.remove(request.getRequestUuid());
            requestListener.onFinished(request, result);
        }

        @Override
        protected void onCancelled() {
            tasks.remove(request.getRequestUuid());
            requestListener.onCancelled(request);
            super.onCancelled();
        }
    }

}
