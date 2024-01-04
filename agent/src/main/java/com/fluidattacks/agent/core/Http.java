package com.fluidattacks.agent.core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Predicate;

import com.fluidattacks.agent.context.CallChain;
import com.fluidattacks.agent.context.HttpRequestContext;
import com.fluidattacks.agent.context.RequestContext;
import com.fluidattacks.agent.http.IASTServletRequest;
import com.fluidattacks.agent.http.IASTServletResponse;

public class Http {

	public static void leaveHttp(int requestHashCode) {
		IASTServletRequest request = RequestContext.getHttpRequestContextThreadLocal(Integer.valueOf(requestHashCode))
				.getServletRequest();
		System.out.printf("URL            : %s \n", request.getRequestURL().toString());
		System.out.printf("URI            : %s \n", request.getRequestURI().toString());
		System.out.printf("QueryString    : %s \n", request.getQueryString());
		System.out.printf("HTTP Method    : %s \n", request.getMethod());
		LinkedList<CallChain> callChain = RequestContext.getHttpRequestContextThreadLocal(Integer.valueOf(requestHashCode)).getCallChain();
		Optional<CallChain> source = callChain.stream()
				.filter(x -> x.getChainType().contains("Source")).findFirst();
		Optional<CallChain> sink = callChain.stream()
				.filter(x -> x.getChainType().contains("Sink")).findFirst();
		// if (!(request.getRequestURI().toString().contains("attack8")
		// 		|| request.getRequestURL().toString().contains("attack8"))) {
		// 	return;
		// }
		System.out.println(String.format("Call chian length : %s",
				callChain.toArray().length));

		RequestContext.getHttpRequestContextThreadLocal(Integer.valueOf(requestHashCode)).getCallChain().forEach(item -> {
			if (item.getChainType().contains("leave")) {
				String returnData = null;
				if (item.getReturnObject().getClass().equals(byte[].class)) {
					returnData = new String((byte[]) item.getReturnObject());
				} else if (item.getReturnObject().getClass().equals(char[].class)) {
					returnData = new String((char[]) item.getReturnObject());
				} else {
					returnData = item.getReturnObject().toString();
				}

				System.out
						.printf("Type: %s CALL Method Name: %s#%s CALL Method Return: %s \n", item.getChainType(),
								item.getJavaClassName(), item.getJavaMethodName(), returnData);
			} else {
				System.out
						.printf("Type: %s CALL Method Name: %s#%s CALL Method Args: %s \n", item.getChainType(),
								item.getJavaClassName(), item.getJavaMethodName(),
								Arrays.asList(item.getArgumentArray()));
			}

			if (item.getChainType().contains("Sink")) {
				int depth = 1;
				StackTraceElement[] elements = item.getStackTraceElement();

				// for (StackTraceElement element : elements) {
				// if (element.getClassName().contains("com.fluidattacks.agent") ||
				// element.getClassName().contains("java.lang.Thread")) {
				// continue;
				// }
				// System.out.printf("%9s".replace("9", String.valueOf(depth)), "");
				// System.out.println(element);
				// depth++;
				// }
			}
		});
	}

	public static boolean haveEnterHttp(int requestHash) {
		HttpRequestContext context = RequestContext.getHttpRequestContextThreadLocal(Integer.valueOf(requestHash));
		return context != null;
	}

	public static void enterHttp(Object requestObject, Object responseObject ) {
		IASTServletRequest request = new IASTServletRequest(requestObject);
		IASTServletResponse response = new IASTServletResponse(responseObject);

		if (!haveEnterHttp(request.getHashCode())) {
			RequestContext.setHttpRequestContextThreadLocal(request, response);
		}
	}
}
