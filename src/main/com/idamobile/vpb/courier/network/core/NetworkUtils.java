package com.idamobile.vpb.courier.network.core;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkUtils {

    public static HttpPost createPostRequest(String uri) {
        HttpPost post = new HttpPost(uri);
        post.setHeader("Content-Type", "application/x-protobuf");
        return post;
    }

    public static HttpGet createGetRequest(String uri) {
        return new HttpGet(uri);
    }

    public static HttpDelete createDeleteRequest(String uri) {
        return new HttpDelete(uri);
    }

    public static HttpPut createPutRequest(String uri) {
        HttpPut put = new HttpPut(uri);
        put.setHeader("Content-Type", "application/x-protobuf");
        return put;
    }

    public static boolean hasInternet(Context context) {
        ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
