package com.fluidattacks.agent;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.fluidattacks.agent.visitors.AgentClassVisitor;

public class ClassTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefine,
            ProtectionDomain protectionDomain, byte[] classFileBuffer) {
        String classNameTransformed = className.replace("/", ".");
        if (classNameTransformed.contains("org.owasp.webgoat.lessons.sqlinjection.introduction")
                || className.contains("com/applicationsec/Controller")) {
            System.out.println(String.format("Trying to transform: %s", className));
            ClassReader reader = new ClassReader(classFileBuffer);
            ClassWriter writer = new ClassWriter(new ClassReader(classFileBuffer), ClassWriter.COMPUTE_MAXS);

            reader.accept(new AgentClassVisitor(writer, className), ClassReader.EXPAND_FRAMES);
            byte[] enhanced = writer.toByteArray();
            return enhanced;
        }

        return null;
    }
}
