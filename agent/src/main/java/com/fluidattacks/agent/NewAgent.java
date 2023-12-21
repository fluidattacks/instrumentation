package com.fluidattacks.agent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Base64;

public class NewAgent {
    public static void premain(String agentArgs, Instrumentation instrumentation) throws UnmodifiableClassException {
        System.out.println("Hello World from the new agent");
        instrumentation.addTransformer(new AgentTransform(), true);
        instrumentation.retransformClasses(Runtime.class);
        instrumentation.retransformClasses(Base64.class);
    }
}
