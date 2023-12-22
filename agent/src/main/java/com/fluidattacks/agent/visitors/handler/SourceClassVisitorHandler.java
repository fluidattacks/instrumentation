package com.fluidattacks.agent.visitors.handler;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import com.fluidattacks.agent.visitors.Handler;

import java.lang.reflect.Modifier;

public class SourceClassVisitorHandler implements Handler {

	private static final String METHOD_DESC = "(Ljava/lang/String;Lorg/springframework/core/MethodParameter;Lorg/springframework/web/context/request/NativeWebRequest;)Ljava/lang/Object;";

	public MethodVisitor ClassVisitorHandler(MethodVisitor mv, final String className, int access, final String name,
			final String desc, String signature, String[] exceptions) {
		if (name.contains("resolveName") && desc.equals(METHOD_DESC)) {
			final boolean isStatic = Modifier.isStatic(access);

			System.out.printf(
					"Source Process the class name: %s, method: %s, descriptor: %s, signature: %s, exceptions: %s\n",
					className, name, desc, signature, exceptions);
			return new AdviceAdapter(Opcodes.ASM5, mv, access, name, desc) {
				@Override
				protected void onMethodEnter() {
					loadArgArray();
					int argsIndex = newLocal(Type.getType(Object[].class));
					storeLocal(argsIndex, Type.getType(Object[].class));
					loadLocal(argsIndex);
					push(className);
					push(name);
					push(desc);
					push(isStatic);

					mv.visitMethodInsn(INVOKESTATIC, "com/fluidattacks/agent/core/Source", "enterSource",
							"([Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V", false);
					super.onMethodEnter();
				}

				@Override
				protected void onMethodExit(int opcode) {
					Type returnType = Type.getReturnType(desc);
					if (returnType == null || Type.VOID_TYPE.equals(returnType)) {
						push((Type) null);
					} else {
						mv.visitInsn(Opcodes.DUP);
					}
					push(className);
					push(name);
					push(desc);
					push(isStatic);
					mv.visitMethodInsn(INVOKESTATIC, "com/fluidattacks/agent/core/Source", "leaveSource",
							"(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V", false);
					super.onMethodExit(opcode);
				}
			};

		}
		return mv;
	}
}
