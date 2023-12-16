package com.fluidattacks.agent.taint;

public class Node {
    String value;
    String methodName;
    String className;
    Node parent;

    public Node(String value, String methodName, String className, Node parent) {
        this.value = value;
        this.methodName = methodName;
        this.parent = parent;
        this.className = className;
    }

    public boolean isSource() {
        return parent == null;
    }
}
