package com.fluidattacks.agent;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.fluidattacks.agent.visitors.handler.HttpClassVisitorHandler;
import com.fluidattacks.agent.visitors.handler.PropagatorClassVisitorHandler;
import com.fluidattacks.agent.visitors.handler.SinkClassVisitorHandler;
import com.fluidattacks.agent.visitors.handler.SourceClassVisitorHandler;

public class IASTClassVisitor extends ClassVisitor implements Opcodes {
    private final String className;
    private boolean isController;

    public IASTClassVisitor(String className, ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String description,
            final String signature,
            String[] exceptions) {
        String newDescription = description;
        boolean isRequestHandler = name.equals("service")
                && this.className.contains("HttpServlet")
                // in webgoat jakarta/servlet/http/HttpServletRequest
                && description
                        .contains("(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)V");

        MethodVisitor mv = super.visitMethod(access, name, newDescription, signature, exceptions);

        if (isRequestHandler) {
            HttpClassVisitorHandler httpClassVisitorHandler = new HttpClassVisitorHandler();
            mv = httpClassVisitorHandler.ClassVisitorHandler(mv, className, access, name,
                    newDescription, signature,
                    exceptions);
        }

        if (name.contains("getParameterValues") &&
                className.contains("ServletWebRequest")) {
            // System.out.printf("%s#%s %s\n", className, name, description);
        }
        if (name.contains("resolveName") &&
                className.contains("RequestParamMethodArgumentResolver")) {
            SourceClassVisitorHandler httpClassVisitorHandler = new SourceClassVisitorHandler();
            mv = httpClassVisitorHandler.ClassVisitorHandler(mv, className, access, name,
                    newDescription, signature,
                    exceptions);
        }

        if (name.equals("execute") && description.contains("(Ljava/lang/String;)V")) {
            SinkClassVisitorHandler sinkClassVisitorHandler = new SinkClassVisitorHandler();
            mv = sinkClassVisitorHandler.ClassVisitorHandler(mv, className, access, name,
                    newDescription, signature,
                    exceptions);
        } else {
            PropagatorClassVisitorHandler propagatorClassVisitorHandler = new PropagatorClassVisitorHandler();
            mv = propagatorClassVisitorHandler.ClassVisitorHandler(mv, className, access,
                    name, description, signature,
                    exceptions);
        }
        return mv;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean vis) {
        if (desc.contains("Lorg/springframework/web/bind/annotation/RestController;")) {
            this.isController = true;
        }
        return cv.visitAnnotation(desc, vis);
    }

}
