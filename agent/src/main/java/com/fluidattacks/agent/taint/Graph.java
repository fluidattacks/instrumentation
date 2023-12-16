package com.fluidattacks.agent.taint;

import java.util.HashMap;

public class Graph {
    public static final HashMap<String, Node> taints = new HashMap<>();

    public static void add(Node taint) {
        taints.put(taint.value, taint);
        System.out.println(String.format("Agent: Adding as new tainted data to the pool: %s", taint.value));
    }

    public static Node get(String value) {
        return taints.get(value);
    }

    public static boolean has(String value) {
        return taints.containsKey(value);
    }
}
