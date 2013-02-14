package com.idamobile.vpb.courier.network;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.Serializable;

public interface Request<T> extends Serializable {
	
	ResponseDTO<T> execute(HttpClient httpClient, HttpContext httpContext);
	
	LoaderCallback<T> getUpdateModelCallback();
	
	String getRequestUuid();
	
	void cancel();
	
}
