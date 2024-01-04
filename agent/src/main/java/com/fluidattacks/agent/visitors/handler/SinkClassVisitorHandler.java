package com.fluidattacks.agent.visitors.handler;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import com.fluidattacks.agent.visitors.Handler;

import java.lang.reflect.Modifier;

public class SinkClassVisitorHandler implements Handler {

	private static final String METHOD_DESC = "()Ljava/lang/Process;";

	@Override
	public MethodVisitor ClassVisitorHandler(MethodVisitor mv, final String className, int access,
			final String name, final String desc, String signature, String[] exceptions) {
		if (name.contains("executeQuery") && desc.contains("(Ljava/lang/String;)")) {
			System.out.printf("Sink Process class: %s, method name: %s, description: %s\n", className, name, desc);
			final boolean isStatic = Modifier.isStatic(access);
			final Type argsType = Type.getType(Object[].class);

			return new AdviceAdapter(Opcodes.ASM9, mv, access, name, desc) {
				@Override
				protected void onMethodEnter() {
					super.onMethodEnter();

					loadArgArray();
					int argsIndex = newLocal(argsType);
					storeLocal(argsIndex, argsType);

					Label startLabel = new Label();
					Label endLabel = new Label();
					Label endIfLabel = new Label();

					mv.visitLabel(startLabel);

					// ServletRequestAttributes servletRequestAttributes =
					// (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
					mv.visitMethodInsn(INVOKESTATIC, "org/springframework/web/context/request/RequestContextHolder",
							"getRequestAttributes", "()Lorg/springframework/web/context/request/RequestAttributes;",
							false);
					mv.visitTypeInsn(CHECKCAST, "org/springframework/web/context/request/ServletRequestAttributes");
					int servletRequestAttributesIndex = newLocal(
							Type.getType("Lorg/springframework/web/context/request/ServletRequestAttributes;"));
					mv.visitVarInsn(ASTORE, servletRequestAttributesIndex);

					// if (servletRequestAttributes != null)
					mv.visitVarInsn(ALOAD, servletRequestAttributesIndex);
					mv.visitJumpInsn(IFNULL, endIfLabel);

					// HttpServletRequest request = servletRequestAttributes.getRequest();
					mv.visitVarInsn(ALOAD, servletRequestAttributesIndex);
					mv.visitMethodInsn(INVOKEVIRTUAL,
							"org/springframework/web/context/request/ServletRequestAttributes", "getRequest",
							"()Ljakarta/servlet/http/HttpServletRequest;", false);
					int requestIndex = newLocal(Type.getType("Ljakarta/servlet/http/HttpServletRequest;"));
					mv.visitVarInsn(ASTORE, requestIndex);

					// int hashCode = request.hashCode();
					mv.visitVarInsn(ALOAD, requestIndex);
					mv.visitMethodInsn(INVOKEINTERFACE, "jakarta/servlet/http/HttpServletRequest", "hashCode", "()I",
							true);
					int hashCodeIndex = newLocal(Type.INT_TYPE);
					mv.visitVarInsn(ISTORE, hashCodeIndex);

					// com.fluidattacks.agent.core.Sink.enterSink;
					mv.visitVarInsn(ILOAD, hashCodeIndex);
					loadLocal(argsIndex);
					push(className);
					push(name);
					push(desc);
					push(isStatic);

					mv.visitMethodInsn(INVOKESTATIC, "com/fluidattacks/agent/core/Sink", "enterSink",
							"(I[Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V",
							false);

					mv.visitJumpInsn(GOTO, endLabel);

					// End of if
					mv.visitLabel(endIfLabel);
					mv.visitFrame(F_SAME, 0, null, 0, null);

					// End label
					mv.visitLabel(endLabel);
					mv.visitFrame(F_SAME, 0, null, 0, null);
				}
			};
		}
		return mv;
	}
}
