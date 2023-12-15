package com.fluidattacks.agent;

import java.lang.instrument.Instrumentation;

public final class Agent {
    private Agent() {
    }

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("Hello World from the agent");
    }
}
