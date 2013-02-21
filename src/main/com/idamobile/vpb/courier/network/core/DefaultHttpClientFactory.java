package com.idamobile.vpb.courier.network.core;

import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class DefaultHttpClientFactory implements HttpClientFactory {

    private static final int HANDSHAKE_TIMEOUT = 30000;
    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int SO_TIMEOUT = 30000;

    private Context context;

    public DefaultHttpClientFactory(Context context) {
        this.context = context;
    }

    @Override
    public HttpClient createClient() {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpClientParams.setRedirecting(params, false);

        ConnManagerParams.setTimeout(params, CONNECTION_TIMEOUT);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);

        SSLSessionCache sessionCache = new SSLSessionCache(context);

        HttpProtocolParams.setUserAgent(params, "Android");

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        SSLSocketFactory sslSocketFactory = SSLCertificateSocketFactory.getHttpSocketFactory(HANDSHAKE_TIMEOUT, sessionCache);
        if (sslSocketFactory == null) {
            sslSocketFactory = SSLSocketFactory.getSocketFactory();
        }
        sslSocketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));

        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
        return new DefaultHttpClient(manager, params);
    }

}
