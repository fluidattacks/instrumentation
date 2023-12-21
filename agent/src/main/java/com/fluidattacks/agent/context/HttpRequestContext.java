package com.fluidattacks.agent.context;

import java.util.LinkedList;

import com.fluidattacks.agent.http.IASTServletRequest;
import com.fluidattacks.agent.http.IASTServletResponse;

public class HttpRequestContext {
	private final IASTServletRequest servletRequest;
	private final IASTServletResponse servletResponse;
	private LinkedList<CallChain> callChain;

	public HttpRequestContext(IASTServletRequest servletRequest, IASTServletResponse servletResponse) {
		this.servletRequest = servletRequest;
		this.servletResponse = servletResponse;
		this.callChain = new LinkedList<>();
	}

	public IASTServletRequest getServletRequest() {
		return servletRequest;
	}

	public LinkedList<CallChain> getCallChain() {
		return callChain;
	}

	public void addCallChain(CallChain callChain) {
		this.callChain.add(callChain);
	}

}
