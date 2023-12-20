package com.fluidattacks.agent.context;

public class CallChain {

	private String chainType;

	private Object returnObject;

	private Object[] argumentArray;

	private String javaClassName;

	private String javaMethodName;

	private String javaMethodDesc;

	private boolean isStatic;

	public StackTraceElement[] StackTraceElement;

	public String getChainType() {
		return chainType;
	}

	public void setChainType(String chainType) {
		this.chainType = chainType;
	}

	public Object getReturnObject() {
		return returnObject;
	}

	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

	public Object[] getArgumentArray() {
		return argumentArray;
	}

	public void setArgumentArray(Object[] argumentArray) {
		this.argumentArray = argumentArray;
	}

	public String getJavaClassName() {
		return javaClassName;
	}

	public void setJavaClassName(String javaClassName) {
		this.javaClassName = javaClassName;
	}

	public String getJavaMethodName() {
		return javaMethodName;
	}

	public void setJavaMethodName(String javaMethodName) {
		this.javaMethodName = javaMethodName;
	}

	public String getJavaMethodDesc() {
		return javaMethodDesc;
	}

	public void setJavaMethodDesc(String javaMethodDesc) {
		this.javaMethodDesc = javaMethodDesc;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean aStatic) {
		isStatic = aStatic;
	}

	public StackTraceElement[] getStackTraceElement() {
		return StackTraceElement;
	}

	public void setStackTraceElement(StackTraceElement[] setStackTraceElement) {
		this.StackTraceElement = setStackTraceElement;
	}
}
