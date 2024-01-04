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

                return new AdviceAdapter(Opcodes.ASM5, methodVisitor, methodAccess, methodName, desc) {
                        boolean isRequestHandler = false;

                        @Override
                        protected void onMethodEnter() {
                                if (this.isRequestHandler) {
                                        try {
                                                // ServletRequestAttributes servletRequestAttributes =
                                                // (ServletRequestAttributes)
                                                // RequestContextHolder.getRequestAttributes();
                                                mv.visitMethodInsn(INVOKESTATIC,
                                                                "org/springframework/web/context/request/RequestContextHolder",
                                                                "getRequestAttributes",
                                                                "()Lorg/springframework/web/context/request/RequestAttributes;",
                                                                false);
                                                int servletRequestAttributesIndex = newLocal(
                                                                Type.getType("Lorg/springframework/web/context/request/RequestAttributes;"));
                                                mv.visitTypeInsn(CHECKCAST,
                                                                "org/springframework/web/context/request/ServletRequestAttributes");
                                                storeLocal(servletRequestAttributesIndex); // Storing the value in the
                                                                                           // allocated local
                                                                                           // variable

                                                // HttpServletRequest request = servletRequestAttributes.getRequest();
                                                loadLocal(servletRequestAttributesIndex);
                                                mv.visitMethodInsn(INVOKEVIRTUAL,
                                                                "org/springframework/web/context/request/ServletRequestAttributes",
                                                                "getRequest",
                                                                "()Ljakarta/servlet/http/HttpServletRequest;", false);
                                                int requestIndex = newLocal(
                                                                Type.getType("Ljakarta/servlet/http/HttpServletRequest;"));
                                                storeLocal(requestIndex);

                                                // HttpServletRequest response = servletRequestAttributes.getResponse();
                                                loadLocal(servletRequestAttributesIndex);
                                                mv.visitMethodInsn(INVOKEVIRTUAL,
                                                                "org/springframework/web/context/request/ServletRequestAttributes",
                                                                "getResponse",
                                                                "()Ljakarta/servlet/http/HttpServletResponse;", false);
                                                int responseIndex = newLocal(
                                                                Type.getType("Ljakarta/servlet/http/HttpServletResponse;"));
                                                storeLocal(responseIndex);

                                                loadLocal(requestIndex);
                                                loadLocal(responseIndex);
                                                mv.visitMethodInsn(INVOKESTATIC, "com/fluidattacks/agent/core/Http",
                                                                "enterHttp", "(Ljava/lang/Object;Ljava/lang/Object;)V",
                                                                false);

                                                final boolean isStatic = Modifier.isStatic(methodAccess);
                                                loadLocal(requestIndex);
                                                // hashCode() - Here we need to use INVOKEINTERFACE because
                                                // HttpServletRequest
                                                // is an interface
                                                mv.visitMethodInsn(INVOKEINTERFACE,
                                                                "jakarta/servlet/http/HttpServletRequest", "hashCode",
                                                                "()I",
                                                                true);
                                                loadArgArray();
                                                int argsIndex = newLocal(Type.getType(Object[].class));
                                                storeLocal(argsIndex, Type.getType(Object[].class));
                                                loadLocal(argsIndex);
                                                push(className);
                                                push(methodName);
                                                push(desc);
                                                push(isStatic);

                                                mv.visitMethodInsn(INVOKESTATIC, "com/fluidattacks/agent/core/Source",
                                                                "enterSource",
                                                                "(I[Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V",
                                                                false);
                                                super.onMethodEnter();
                                        } catch (Exception e) {
                                                e.printStackTrace();
                                        }
                                }
                        }

                        @Override
                        protected void onMethodExit(int i) {
                                super.onMethodExit(i);
                                if (this.isRequestHandler) {
                                        // RequestContextHolder.getRequestAttributes()
                                        mv.visitMethodInsn(INVOKESTATIC,
                                                        "org/springframework/web/context/request/RequestContextHolder",
                                                        "getRequestAttributes",
                                                        "()Lorg/springframework/web/context/request/RequestAttributes;",
                                                        false);

                                        // Cast to ServletRequestAttributes
                                        mv.visitTypeInsn(CHECKCAST,
                                                        "org/springframework/web/context/request/ServletRequestAttributes");

                                        // getRequest()
                                        mv.visitMethodInsn(INVOKEVIRTUAL,
                                                        "org/springframework/web/context/request/ServletRequestAttributes",
                                                        "getRequest",
                                                        "()Ljakarta/servlet/http/HttpServletRequest;", false);

                                        // hashCode() - Here we need to use INVOKEINTERFACE because HttpServletRequest
                                        // is an interface
                                        mv.visitMethodInsn(INVOKEINTERFACE, "jakarta/servlet/http/HttpServletRequest",
                                                        "hashCode", "()I",
                                                        true);

                                        // Http.leaveHttp(request.hashCode());
                                        mv.visitMethodInsn(INVOKESTATIC, "com/fluidattacks/agent/core/Http",
                                                        "leaveHttp", "(I)V", false);
                                }
                        }

                        @Override
                        public AnnotationVisitor visitAnnotation(String desc, boolean vis) {
                                if (desc.contains("Lorg/springframework/web/bind/annotation/GetMapping;")
                                                || desc.contains(
                                                                "Lorg/springframework/web/bind/annotation/PostMapping;")) {
                                        this.isRequestHandler = true;
                                        System.out.printf("The method %s#%s is a request handler\n", className,
                                                        methodName);
                                }
                                return mv.visitAnnotation(desc, vis);
                        }
                };
        }

}
