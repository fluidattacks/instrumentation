package com.fluidattacks.agent;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import br.usp.each.saeg.asm.defuse.DefUseAnalyzer;
import br.usp.each.saeg.asm.defuse.DefUseChain;
import br.usp.each.saeg.asm.defuse.DefUseFrame;
import br.usp.each.saeg.asm.defuse.DefUseInterpreter;
import br.usp.each.saeg.asm.defuse.DepthFirstDefUseChainSearch;
import br.usp.each.saeg.asm.defuse.FlowAnalyzer;
import br.usp.each.saeg.asm.defuse.Value;
import br.usp.each.saeg.asm.defuse.Variable;

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

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        boolean isRestController = classNode.visibleAnnotations.stream().anyMatch(x -> {
            return x.desc.contains("Lorg/springframework/web/bind/annotation/RestController;");
        });
        if (isRestController) {
            try {
                Stream<MethodNode> methodNodes = classNode.methods.stream()
                        .filter(x -> {
                            if (x.visibleAnnotations == null) {
                                return false;
                            }
                            return x.visibleAnnotations.stream().anyMatch(annotation -> {
                                String description = annotation.desc;
                                return (description.contains("Lorg/springframework/web/bind/annotation/GetMapping;")
                                        || description.contains(
                                                "Lorg/springframework/web/bind/annotation/PostMapping;"));
                            });
                        });
                methodNodes.forEach(method -> {
                    List<Integer> paramIndexes = new ArrayList<Integer>();
                    if (method.visibleParameterAnnotations != null) {
                        int index = 0;
                        final boolean isStatic = Modifier.isStatic(method.access);
                        if (isStatic){
                            index = 0;
                        } else {
                            index = 1;
                        }
                        for (List<AnnotationNode> visibleParameterAnnotations : method.visibleParameterAnnotations) {
                            if (visibleParameterAnnotations != null) {
                                if (visibleParameterAnnotations.stream().anyMatch(x -> x.desc
                                        .contains("Lorg/springframework/web/bind/annotation/RequestParam;"))) {
                                    paramIndexes.add(index);
                                }
                            }
                            index = index + 1;
                        }
                    }

                    MethodNode mn = method;
                    DefUseInterpreter interpreter = new DefUseInterpreter();
                    FlowAnalyzer<Value> flowAnalyzer = new FlowAnalyzer<Value>(interpreter);
                    DefUseAnalyzer analyzer = new DefUseAnalyzer(flowAnalyzer, interpreter);
                    try {
                        analyzer.analyze(className, mn);
                    } catch (AnalyzerException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Variable[] variables = analyzer.getVariables();
                    DefUseFrame[] frames = analyzer.getDefUseFrames();
                    DefUseChain[] chains = new DepthFirstDefUseChainSearch().search(
                            analyzer.getDefUseFrames(),
                            analyzer.getVariables(),
                            flowAnalyzer.getSuccessors(),
                            flowAnalyzer.getPredecessors());
                            System.out.println(String.format("The chain has %s frames", frames.length));

                    System.out.println("This method contains " + chains.length + " Definition-Use Chains");
                    for (int i = 0; i < chains.length; i++) {
                        DefUseChain chain = chains[i];
                        
                        variables[chain.var].insns.forEach(y -> {
                            y.visibleTypeAnnotations.forEach(z -> {
                                System.out.println(z);
                            });
                        });
                        if (true) {
                            System.out.println("Instruction " + chain.def + " define variable " + variables[chain.var]);
                            System.out.println("Instruction " + chain.use + " uses variable " + variables[chain.var]);
                            System.out.println(variables[chain.var].type);
                            System.out.println("---------------");
                        }
                        // There is a path between chain.def and chain.use that not redefine chain.var
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            // requestHandlers.forEach(item -> {
            // System.out.printf("RequestHandler -> %s\n", item.name);
            // });
        }

        // if (className.contains("SqlInjectionLesson8")) {
        // System.out.println(className);
        // PrintWriter printWriter = new PrintWriter(System.out);
        // TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, new
        // ASMifier(), printWriter);

        // classReader.accept(traceClassVisitor, 0);
        // printWriter.flush();
        // }

        return classfileBuffer;
    }
}
