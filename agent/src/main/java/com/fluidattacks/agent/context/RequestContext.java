package com.fluidattacks.agent.context;

public class RequestContext {

	private static final ThreadLocal<HttpRequestContext> HTTP_REQUEST_CONTEXT_THREAD_LOCAL = new ThreadLocal<HttpRequestContext>();

	public static HttpRequestContext getHttpRequestContextThreadLocal() {
		return HTTP_REQUEST_CONTEXT_THREAD_LOCAL.get();
	}

	public static void setHttpRequestContextThreadLocal() {
		HTTP_REQUEST_CONTEXT_THREAD_LOCAL.set(new HttpRequestContext());
	}

}