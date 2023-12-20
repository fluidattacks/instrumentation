package com.fluidattacks.agent.context;

import java.util.LinkedList;

public class HttpRequestContext {

	private LinkedList<CallChain> callChain;

	public HttpRequestContext() {
		this.callChain = new LinkedList<>();
	}

	public LinkedList<CallChain> getCallChain() {
		return callChain;
	}

	public void addCallChain(CallChain callChain) {
		this.callChain.add(callChain);
	}

}
