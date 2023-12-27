package com.fluidattacks.agent.visitors.handler;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import com.fluidattacks.agent.visitors.Handler;

public class PropagatorClassVisitorHandler implements Handler {

	@Override
	public MethodVisitor ClassVisitorHandler(MethodVisitor mv, final String className, int access,
			final String methodName, final String methodDesc, String signature, String[] exceptions) {

		return new AdviceAdapter(Opcodes.ASM5, mv, access, methodName, methodDesc) {

			@Override
			public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle,
					Object... bootstrapMethodArguments) {
				Type argsType = Type.getType(Object[].class);
				boolean isValidMethod = name.contains("makeConcat");
				if (isValidMethod) {
					loadArgArray();
					int argsIndex = newLocal(argsType);
					storeLocal(argsIndex, argsType);
					loadLocal(argsIndex);
					push(className);
					push(name);
					push(methodDesc);
					push(false);

					mv.visitMethodInsn(INVOKESTATIC, "com/fluidattacks/agent/core/Propagator",
							"enterPropagator",
							"([Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V",
							false);
				}

				super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);

				if (isValidMethod) {
					Type returnType = Type.getReturnType(descriptor);
					if (returnType == null || Type.VOID_TYPE.equals(returnType)) {
						push((Type) null);
					} else {
						mv.visitInsn(Opcodes.DUP);
					}
					push(className);
					push(name);
					push(descriptor);
					push(false);
					mv.visitMethodInsn(INVOKESTATIC, "com/fluidattacks/agent/core/Propagator",
							"leavePropagator",
							"(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V",
							false);
				}

			}

			@Override
			protected void onMethodExit(int opcode) {
				super.onMethodExit(opcode);
			}
		};
	}
}
