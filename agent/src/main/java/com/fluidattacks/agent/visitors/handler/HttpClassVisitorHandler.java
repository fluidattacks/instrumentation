package com.fluidattacks.agent.visitors.handler;

import java.lang.reflect.Modifier;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import com.fluidattacks.agent.visitors.Handler;

public class HttpClassVisitorHandler implements Handler {

    public MethodVisitor ClassVisitorHandler(MethodVisitor methodVisitor, final String className, int methodAccess,
            String methodName, String desc, String signature, String[] exceptions) {

        if (methodName.contains("service")
                &&
                desc.contains(
                        "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)V")) {
            System.out.println(
                    String.format(
                            "HTTP Process class name is: %s, the method name is: %s, the method descriptor is: %s, the signature is: %s, exceptions: %s",
                            className, methodName, desc, signature, exceptions));
            final boolean isStatic = Modifier.isStatic(methodAccess);
            final Type argsType = Type.getType(Object[].class);

            System.out.println(
                    String.format(
                            "HTTP Process class name is: %s, the method name is: %s, the method descriptor is: %s, the signature is: %s, exceptions: %s",
                            className, methodName, desc, signature, exceptions));

            return new AdviceAdapter(Opcodes.ASM5, methodVisitor, methodAccess, methodName, desc) {
                boolean isRequestHandler = false;

                @Override
                protected void onMethodEnter() {
                    if (true) {
                        System.out.println(String.format("Trying to modify the request handler %s", methodName));
                        loadArgArray();
                        int argsIndex = newLocal(argsType);
                        storeLocal(argsIndex, argsType);
                        loadLocal(argsIndex);

                        // if (isStatic) {
                        // push((Type) null);
                        // } else {
                        // loadThis();
                        // }

                        // loadLocal(argsIndex);

                        methodVisitor.visitMethodInsn(INVOKESTATIC, "com/fluidattacks/agent/core/Http",
                                "enterHttp",
                                "([Ljava/lang/Object;)V", false);
                    }

                }

                @Override
                protected void onMethodExit(int i) {
                    super.onMethodExit(i);
                    mv.visitMethodInsn(INVOKESTATIC, "com/fluidattacks/agent/core/Http",
                            "leaveHttp", "()V",
                            false);
                }

                @Override
                public AnnotationVisitor visitAnnotation(String desc, boolean vis) {
                    System.out.println(desc);
                    if (desc.contains("Lorg/springframework/web/bind/annotation/GetMapping;")) {
                        this.isRequestHandler = true;
                    }
                    return mv.visitAnnotation(desc, vis);
                }
            };
        }
        return methodVisitor;
    }

}
