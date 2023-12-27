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
            return classfileBuffer;
        }

        if (className.contains("java/lang/invoke")) {
            return classfileBuffer;
        }
        ClassReader classReader;
        try {

            classReader = new ClassReader(classfileBuffer);
        } catch (Exception e) {
            return classfileBuffer;
        }
        ClassWriter classWriter = new ClassWriter(new ClassReader(classfileBuffer), ClassWriter.COMPUTE_MAXS);

        ClassVisitor classVisitor = new IASTClassVisitor(className, classWriter);
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        // if (className.contains("com/applicationsec/Controller")) {
        // PrintWriter printWriter = new PrintWriter(System.out);
        // TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, new
        // ASMifier(), printWriter);

        // classReader.accept(traceClassVisitor, 0);
        // printWriter.flush();
        // }

        return classWriter.toByteArray();
    }
}
