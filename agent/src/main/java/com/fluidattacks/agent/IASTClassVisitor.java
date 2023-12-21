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

    private String addParameter(String originalSignature, String newParameterType) {
        // Regular expression to match the method signature pattern and find the
        // position before the closing parenthesis
        Pattern pattern = Pattern.compile("^(.*)(\\)L.*)$");
        Matcher matcher = pattern.matcher(originalSignature);

        if (matcher.find()) {
            // Insert the new parameter type before the closing parenthesis
            return matcher.group(1) + newParameterType + matcher.group(2);
        } else {
            // Return the original signature if the pattern does not match
            return originalSignature;
        }
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String description, final String signature,
            String[] exceptions) {
        String newDescription = description;
        if (this.isController) {
            newDescription = description;
            // newDescription = addParameter(description, "Ljavax/servlet/http/HttpServletRequest;");
        }
        MethodVisitor mv = super.visitMethod(access, name, newDescription, signature, exceptions);
        if (this.isController) {
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
