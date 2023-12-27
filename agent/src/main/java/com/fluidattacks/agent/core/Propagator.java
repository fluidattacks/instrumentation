package com.fluidattacks.agent.core;

import static com.fluidattacks.agent.core.Http.haveEnterHttp;

import com.fluidattacks.agent.context.CallChain;
import com.fluidattacks.agent.context.RequestContext;

public class Propagator {

	public static void enterPropagator(Object[] argumentArray,
			String javaClassName,
			String javaMethodName,
			String javaMethodDesc,
			boolean isStatic) {
		if (haveEnterHttp()) {
			CallChain callChain = new CallChain();
			callChain.setChainType("enterPropagator");
			callChain.setArgumentArray(argumentArray);
			callChain.setJavaClassName(javaClassName);
			callChain.setJavaMethodName(javaMethodName);
			callChain.setJavaMethodDesc(javaMethodDesc);
			callChain.setStatic(isStatic);

			RequestContext.getHttpRequestContextThreadLocal().addCallChain(callChain);
		}

	}

	public static void leavePropagator(Object returnObject,
			String javaClassName,
			String javaMethodName,
			String javaMethodDesc,
			boolean isStatic) {
		if (haveEnterHttp()) {
			CallChain callChain = new CallChain();
			callChain.setChainType("leavePropagator");
			callChain.setReturnObject(returnObject);
			callChain.setJavaClassName(javaClassName);
			callChain.setJavaMethodName(javaMethodName);
			callChain.setJavaMethodDesc(javaMethodDesc);
			callChain.setStatic(isStatic);
			RequestContext.getHttpRequestContextThreadLocal().addCallChain(callChain);

		}
	}
}
