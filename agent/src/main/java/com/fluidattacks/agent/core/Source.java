package com.fluidattacks.agent.core;

import com.fluidattacks.agent.context.CallChain;
import com.fluidattacks.agent.context.RequestContext;

public class Source {
	public static void enterSource(Object[] argumentArray,
	                               String javaClassName,
	                               String javaMethodName,
	                               String javaMethodDesc,
	                               boolean isStatic) {
		if (Http.haveEnterHttp()) {
			CallChain callChain = new CallChain();
			callChain.setChainType("enterSource");
			callChain.setArgumentArray(argumentArray);
			callChain.setJavaClassName(javaClassName);
			callChain.setJavaMethodName(javaMethodName);
			callChain.setJavaMethodDesc(javaMethodDesc);
			callChain.setStatic(isStatic);
			RequestContext.getHttpRequestContextThreadLocal().addCallChain(callChain);
		}
	}

	public static void leaveSource(Object returnObject,
	                               String javaClassName,
	                               String javaMethodName,
	                               String javaMethodDesc,
	                               boolean isStatic) {
		if (Http.haveEnterHttp()) {
			CallChain callChain = new CallChain();
			callChain.setChainType("leaveSource");
			callChain.setReturnObject(returnObject);
			callChain.setJavaClassName(javaClassName);
			callChain.setJavaMethodName(javaMethodName);
			callChain.setJavaMethodDesc(javaMethodDesc);
			callChain.setStatic(isStatic);
			RequestContext.getHttpRequestContextThreadLocal().addCallChain(callChain);
		}
	}
}
