package com.fluidattacks.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class AgentTransform implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className,
            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
            byte[] classfileBuffer) throws IllegalClassFormatException {

        if (className.contains("com/fluidattacks/agent")) {
            // System.out.println("Skip class: " + className);
            return classfileBuffer;
        }

        if (className.contains("java/lang/invoke")) {
            // System.out.println("Skip class: " + className);
            return classfileBuffer;
        }
        // System.out.println(String.format("The class %s is a REST controller", className));
        ClassReader classReader = new ClassReader(classfileBuffer);
        ClassWriter classWriter = new ClassWriter(new ClassReader(classfileBuffer), ClassWriter.COMPUTE_MAXS);
        ClassVisitor classVisitor = new IASTClassVisitor(className, classWriter);

        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        // byte[] enhanced = classWriter.toByteArray();

        // classfileBuffer = classWriter.toByteArray();
        // className = className.replace("/", ".");

        return classWriter.toByteArray();
    }
}
