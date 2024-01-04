package com.fluidattacks.agent.context;

import java.util.concurrent.ConcurrentHashMap;

import com.fluidattacks.agent.http.IASTServletRequest;
import com.fluidattacks.agent.http.IASTServletResponse;

public class RequestContext {

	private static final ConcurrentHashMap<Integer, HttpRequestContext> HTTP_REQUEST_COLLECTION = new ConcurrentHashMap<>();

	public static HttpRequestContext getHttpRequestContextThreadLocal(Integer requestHash) {
		return HTTP_REQUEST_COLLECTION.get(requestHash);
	}

	public static void setHttpRequestContextThreadLocal(IASTServletRequest request, IASTServletResponse response) {
		IASTServletRequest  iastServletRequest  = request;
		IASTServletResponse iastServletResponse = response;

		HTTP_REQUEST_COLLECTION.put(iastServletRequest.getHashCode(), new HttpRequestContext(iastServletRequest, iastServletResponse));
	}

}