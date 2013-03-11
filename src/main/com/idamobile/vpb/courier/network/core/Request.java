package com.idamobile.vpb.courier.network.core;

import com.idamobile.vpb.courier.ApplicationMediator;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.Serializable;

public interface Request<T> extends Serializable {
	
	ResponseDTO<T> execute(ApplicationMediator mediator, HttpClient httpClient, HttpContext httpContext);
	
	LoaderCallback<T> getUpdateModelCallback();
	
	String getRequestUuid();
	
	void cancel(boolean interrupt);
	
}
