package com.fluidattacks.agent.core;

import static com.fluidattacks.agent.core.Http.haveEnterHttp;

import com.fluidattacks.agent.context.CallChain;
import com.fluidattacks.agent.context.RequestContext;

public class Sink {
	public static void enterSink(Object[] argumentArray,
			String javaClassName,
			String javaMethodName,
			String javaMethodDesc,
			boolean isStatic) {
		if (haveEnterHttp()) {
			CallChain callChain = new CallChain();
			callChain.setChainType("enterSink");
			callChain.setArgumentArray(argumentArray);
			callChain.setJavaClassName(javaClassName);
			callChain.setJavaMethodName(javaMethodName);
			callChain.setJavaMethodDesc(javaMethodDesc);
			callChain.setStatic(isStatic);
			callChain.setStackTraceElement(Thread.currentThread().getStackTrace());
			RequestContext.getHttpRequestContextThreadLocal().addCallChain(callChain);
		}
	}
}
