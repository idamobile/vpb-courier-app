package com.idamobile.vpb.courier.network.test;

import android.os.Environment;
import com.idamobile.vpb.courier.util.Files;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestUploadImageProcessor extends AbstractHttpRequestProcessor {

    @Override
    public HttpResponse process(HttpUriRequest request) throws IOException {
        HttpPost post = (HttpPost) request;
        InputStream content = post.getEntity().getContent();
        FileOutputStream outputStream = null;
        try {
            File output = new File(Environment.getExternalStorageDirectory(),
                    String.valueOf(System.currentTimeMillis()) + ".png");
            outputStream = new FileOutputStream(output);
            Files.copy(content, outputStream);
            return makeResponse(200);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
            try {
                post.getEntity().consumeContent();
            } catch (IOException ignored) {
            }
        }
    }

}
