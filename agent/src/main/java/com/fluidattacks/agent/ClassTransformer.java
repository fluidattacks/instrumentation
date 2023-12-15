package com.fluidattacks.agent;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class ClassTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefine,
            ProtectionDomain protectionDomain, byte[] classFileBuffer) {
        if (className.replace("/", ".").contains("org.owasp.webgoat")) {
            System.out.println(String.format("Trying to transform: %s", className));

            ClassReader reader = new ClassReader(classFileBuffer);
            ClassWriter writer = new ClassWriter(new ClassReader(classFileBuffer), ClassWriter.COMPUTE_MAXS);
        }
        return classFileBuffer;
    }
}
