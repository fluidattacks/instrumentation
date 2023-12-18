package com.fluidattacks.agent.visitors;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class AgentClassVisitor extends org.objectweb.asm.ClassVisitor {

    protected String className;

    public AgentClassVisitor(ClassVisitor visitor, String className) {
        super(Opcodes.ASM7, visitor);

        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int methodAccess, String methodName, String methodDesc, String signature,
            String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(methodAccess, methodName, methodDesc, signature, exceptions);
        return new AgentAdviceAdapter(Opcodes.ASM7, methodVisitor, methodAccess, methodName, methodDesc, className);
    }

    public static class AgentAdviceAdapter extends AdviceAdapter {
        private final String methodName;
        private final String className;
        private static final List<String> TAINT_ANNOTATIONS = List
                .of("Lorg/springframework/web/bind/annotation/RequestParam;");
        private final ArrayList<Integer> taintedParams = new ArrayList<>();

        protected AgentAdviceAdapter(int api, MethodVisitor methodVisitor, int methodAccess, String methodName,
                String methodDesc, String className) {
            super(api, methodVisitor, methodAccess, methodName, methodDesc);

            this.methodName = methodName;
            this.className = className;
        }

        @Override
        protected void onMethodEnter() {
            if (!taintedParams.isEmpty()) {
                for (Integer taintedParam : taintedParams) {
                    mv.visitTypeInsn(NEW, "com/fluidattacks/agent/taint/Node");
                    mv.visitInsn(DUP);
                    mv.visitVarInsn(ALOAD, taintedParam);
                    mv.visitLdcInsn(methodName);
                    mv.visitLdcInsn(className);
                    mv.visitInsn(ACONST_NULL);
                    mv.visitMethodInsn(INVOKESPECIAL, "com/fluidattacks/agent/taint/Node", "<init>",
                            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/fluidattacks/agent/taint/Node;)V",
                            false);
                    mv.visitVarInsn(ASTORE, 5);
                    mv.visitVarInsn(ALOAD, 5);
                    mv.visitMethodInsn(INVOKESTATIC, "com/fluidattacks/agent/taint/Graph", "add",
                            "(Lcom/fluidattacks/agent/taint/Node;)V", false);
                }
            }
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(final int parameter, final String descriptor,
                final boolean visible) {
            System.out.println(String.format("Instrumenting decorator: %s ", descriptor));
            if (TAINT_ANNOTATIONS.contains(descriptor)) {
                taintedParams.add(parameter + 1);
            }
            return super.visitParameterAnnotation(parameter, descriptor, visible);

        }
    }
}
