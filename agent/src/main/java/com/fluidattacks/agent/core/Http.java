package com.fluidattacks.agent.core;

import java.util.Arrays;

import com.fluidattacks.agent.context.HttpRequestContext;
import com.fluidattacks.agent.context.RequestContext;

public class Http {

	public static void leaveHttp() {
		RequestContext.getHttpRequestContextThreadLocal().getCallChain().forEach(item -> {
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
						.printf("Type: %s CALL Method Name: %s CALL Method Return: %s \n", item.getChainType(),
								item.getJavaClassName() +"#"+ item.getJavaMethodName(), returnData);
			} else {
				System.out
						.printf("Type: %s CALL Method Name: %s CALL Method Args: %s \n", item.getChainType(),
								item.getJavaClassName() +"#"+ item.getJavaMethodName(),
								Arrays.asList(item.getArgumentArray()));
			}

			if (item.getChainType().contains("Sink")) {
				int                 depth    = 1;
				StackTraceElement[] elements = item.getStackTraceElement();

				for (StackTraceElement element : elements) {
					if (element.getClassName().contains("com.fluidattacks.agent") ||
							element.getClassName().contains("java.lang.Thread")) {
						continue;
					}
					System.out.printf("%9s".replace("9", String.valueOf(depth)), "");
					System.out.println(element);
					depth++;
				}
			}
		});
	}

	public static boolean haveEnterHttp() {
		HttpRequestContext context = RequestContext.getHttpRequestContextThreadLocal();
		return context != null;
	}

	public static void enterHttp(Object[] objects) {
		System.out.println(String.format("The function length args is: %s", objects.length));
		if (!haveEnterHttp()) {
			RequestContext.setHttpRequestContextThreadLocal();
		}
	}
}
