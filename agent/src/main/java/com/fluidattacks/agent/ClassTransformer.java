package com.fluidattacks.agent;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.fluidattacks.agent.visitors.AgentClassVisitor;
import com.fluidattacks.agent.visitors.SinkClassVisitor;

public class ClassTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefine,
            ProtectionDomain protectionDomain, byte[] classFileBuffer) {
        if (className.contains("com/applicationsec/Controller")) {
            ClassReader reader = new ClassReader(classFileBuffer);
            PrintWriter printWriter = new PrintWriter(System.out);
            TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, new ASMifier(), printWriter);
            reader.accept(traceClassVisitor, 0);
            printWriter.flush();
        }
        if ((className.contains("org/owasp/webgoat/lessons/sqlinjection/introduction/SqlInjection")
                || className.contains("com/applicationsec/Controller"))) {

            System.out.println(String.format("Trying to transform: %s", className));
            ClassReader reader = new ClassReader(classFileBuffer);
            ClassWriter writer = new ClassWriter(new ClassReader(classFileBuffer), ClassWriter.COMPUTE_MAXS);

            reader.accept(new AgentClassVisitor(writer, className), ClassReader.EXPAND_FRAMES);

            byte[] enhanced = writer.toByteArray();
            return enhanced;

        } else if (className.equals("org/springframework/jdbc/core/JdbcTemplate")) {
            ClassReader reader = new ClassReader(classFileBuffer);
            ClassWriter writer = new ClassWriter(new ClassReader(classFileBuffer), ClassWriter.COMPUTE_MAXS);
            reader.accept(new SinkClassVisitor(writer), ClassReader.EXPAND_FRAMES);
            byte[] enhanced = writer.toByteArray();
            return enhanced;
        }

        return null;
    }
}
