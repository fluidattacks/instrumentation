package com.fluidattacks.agent.visitors;

import org.objectweb.asm.MethodVisitor;

public interface Handler {

	MethodVisitor ClassVisitorHandler(MethodVisitor mv, final String className, int access, String name, String desc,
			String signature, String[] exceptions);
}
