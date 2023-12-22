package com.fluidattacks.agent.visitors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class SinkClassVisitor extends ClassVisitor {

    public SinkClassVisitor(ClassVisitor visitor) {
        super(Opcodes.ASM7, visitor);
    }

    @Override
    public MethodVisitor visitMethod(int methodAccess, String methodName, String methodDesc, String signature,
            String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(methodAccess, methodName, methodDesc, signature, exceptions);
        if (methodName.equals("execute") && methodDesc.contains("(Ljava/lang/String;)V")) {
            return new SinkAdviceAdapter(Opcodes.ASM7, methodVisitor, methodAccess, methodName, methodDesc);
        }
        return methodVisitor;
    }

    public static class SinkAdviceAdapter extends AdviceAdapter {
        private final String methodName;

        protected SinkAdviceAdapter(int api, MethodVisitor methodVisitor, int access, String name, String methodDescriptor) {
            super(api, methodVisitor, access, name, methodDescriptor);
            this.methodName = name;
        }

        @Override
        protected void onMethodEnter() {
            if ((methodName.contains("execute") || methodName.contains("executeQuery")) && methodDesc.contains("(Ljava/lang/String;)V")) {
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESTATIC, "com/fluidattacks/agent/taint/Manager", "onSink",
                        "(Ljava/lang/String;)V", false);
            }
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            if (methodName.equals("execute") && methodDesc.contains("(Ljava/lang/String;)V")) {
                mv.visitMaxs(maxStack, maxLocals);
            }
        }
    }
}
