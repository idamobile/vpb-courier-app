package com.idamobile.vpb.courier.network.core;

import com.idamobile.vpb.courier.util.Logger;
import com.idamobile.vpb.courier.util.Streams;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class HttpUtils {

    private static final String TAG = HttpUtils.class.getSimpleName();

    public static class GzipDecompressingEntity extends HttpEntityWrapper {

        public GzipDecompressingEntity(final HttpEntity entity) {
            super(entity);
        }

        @Override
        public InputStream getContent()
                throws IOException, IllegalStateException {

            // the wrapped entity's getContent() decides about repeatability
            InputStream wrappedin = wrappedEntity.getContent();

            return new GZIPInputStream(new Streams.InputStreamWithTotalBytesCounter(wrappedin));
        }

        @Override
        public long getContentLength() {
            // length of ungzipped content not known in advance
            return -1;
        }

    }

    public static long getContentLength(HttpResponse response) {
        Header header = response.getFirstHeader("Content-Length");
        if (header != null) {
            try {
                return Long.parseLong(header.getValue());
            } catch (Exception ex) {
                Logger.warn(TAG, "error parsing content-length", ex);
            }
        }
        return -1;
    }

    public static void enableGzip(HttpClient httpClient) {
        if (httpClient instanceof AbstractHttpClient) {
            AbstractHttpClient abstractHttpClient = (AbstractHttpClient) httpClient;
            abstractHttpClient.addRequestInterceptor(new HttpRequestInterceptor() {
                @Override
                public void process(
                        final HttpRequest request,
                        final HttpContext context) throws HttpException, IOException {
                    if (!request.containsHeader("Accept-Encoding")) {
                        request.addHeader("Accept-Encoding", "gzip");
                    }
                }
            });

            abstractHttpClient.addResponseInterceptor(new HttpResponseInterceptor() {
                @Override
                public void process(
                        final HttpResponse response,
                        final HttpContext context) throws HttpException, IOException {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        Header ceheader = entity.getContentEncoding();
                        if (ceheader != null) {
                            HeaderElement[] codecs = ceheader.getElements();
                            for (int i = 0; i < codecs.length; i++) {
                                if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                                    Logger.debug(TAG, "content is gzip encoded");
                                    response.setEntity(
                                            new GzipDecompressingEntity(response.getEntity()));
                                    return;
                                }
                            }
                        }
                    }
                }

            });
        }
    }
}
