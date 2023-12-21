package com.fluidattacks.agent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.fluidattacks.agent.visitors.handler.HttpClassVisitorHandler;

public class IASTClassVisitor extends ClassVisitor implements Opcodes {
    private final String className;
    private boolean isController;

    public IASTClassVisitor(String className, ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String description, final String signature,
            String[] exceptions) {
        String newDescription = description;
        boolean isRequestHandler = name.contains("invokeHandlerMethod")
        // in webgoat  jakarta/servlet/http/HttpServletRequest
        && description.contains("(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;Lorg/springframework/web/method/HandlerMethod;)Lorg/springframework/web/servlet/ModelAndView;")
        ;
        MethodVisitor mv = super.visitMethod(access, name, newDescription, signature, exceptions);
        if (isRequestHandler) {
            HttpClassVisitorHandler httpClassVisitorHandler = new HttpClassVisitorHandler();
            mv = httpClassVisitorHandler.ClassVisitorHandler(mv, className, access, name, newDescription, signature, exceptions);
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
