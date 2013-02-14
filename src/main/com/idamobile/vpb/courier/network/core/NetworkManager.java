package com.idamobile.vpb.courier.network.core;

import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;
import android.os.AsyncTask;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.util.HttpUtils;
import com.idamobile.vpb.courier.util.Logger;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.util.HashMap;
import java.util.Map;

public class NetworkManager {
    private static final String TAG = NetworkManager.class.getSimpleName();

    public interface RequestListener<T> {
        void onFinished(Request<T> request, ResponseDTO<T> responseDTO);

        void onCancelled(Request<T> request);
    }

    private static final int HANDSHAKE_TIMEOUT = 30000;
    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int SO_TIMEOUT = 30000;

    private String bankId;

    private HttpContext httpContext;
    private HttpClient httpClient;
    private CookieStore cookieStore;
    private ApplicationMediator mediator;

    private Map<String, LoaderTask<?>> tasks = new HashMap<String, LoaderTask<?>>();

    private ResponsePreProcessorGroup preProcessorGroup;

    public NetworkManager(ApplicationMediator mediator) {
        this.mediator = mediator;
        this.preProcessorGroup = new ResponsePreProcessorGroup();

        initHttpClient();
    }

    public void addProcessor(ResponseDTOPreProcessor processor) {
        preProcessorGroup.addProcessor(processor);
    }

    public void removeProcessor(ResponseDTOPreProcessor processor) {
        preProcessorGroup.removeProcessor(processor);
    }

    private void initHttpClient() {
        httpClient = createHttpClient();
        HttpUtils.enableGzip(httpClient);
        cleanUpSession();
    }


    public void cleanUpSession() {
        cookieStore = new BasicCookieStore();
        httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    private HttpClient createHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpClientParams.setRedirecting(params, false);

        ConnManagerParams.setTimeout(params, CONNECTION_TIMEOUT);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);

        SSLSessionCache sessionCache = new SSLSessionCache(mediator.getContext());

        HttpProtocolParams.setUserAgent(params, "Android");

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        SSLSocketFactory sslSocketFactory = SSLCertificateSocketFactory.getHttpSocketFactory(HANDSHAKE_TIMEOUT, sessionCache);
        sslSocketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        schemeRegistry.register(new Scheme("https", sslSocketFactory, 8443));
//        schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
        return new DefaultHttpClient(manager, params);
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

    public <Q> void cancel(Request<Q> request, boolean mayInterruptIfRunning) {
        @SuppressWarnings("unchecked")
        LoaderTask<Q> task = (LoaderTask<Q>) tasks.get(request.getRequestUuid());
        if (task != null) {
            task.cancel(mayInterruptIfRunning);
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
            return request.execute(httpClient, httpContext);
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
