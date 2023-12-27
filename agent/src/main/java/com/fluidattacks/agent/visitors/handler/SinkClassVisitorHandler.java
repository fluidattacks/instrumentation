package com.fluidattacks.agent.visitors.handler;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import com.fluidattacks.agent.visitors.Handler;

import java.lang.reflect.Modifier;

/**
 * @author iiusky - 03sec.com
 */
public class SinkClassVisitorHandler implements Handler {

	private static final String METHOD_DESC = "()Ljava/lang/Process;";

	@Override
	public MethodVisitor ClassVisitorHandler(MethodVisitor mv, final String className, int access,
			final String name, final String desc, String signature, String[] exceptions) {
		if ((name.contains("execute") && desc.contains("(Ljava/lang/String;)V")) || (("start".equals(name) && METHOD_DESC.equals(desc)) )) {
			final boolean isStatic = Modifier.isStatic(access);
			final Type argsType = Type.getType(Object[].class);

			System.out.printf("Sink Process class: %s, method name: %s, description: %s\n", className, name, desc);
			return new AdviceAdapter(Opcodes.ASM5, mv, access, name, desc) {
				@Override
				protected void onMethodEnter() {
					loadArgArray();
					int argsIndex = newLocal(argsType);
					storeLocal(argsIndex, argsType);
					// loadThis();
					loadLocal(argsIndex);

					push(className);
					push(name);
					push(desc);
					push(isStatic);

					mv.visitMethodInsn(INVOKESTATIC, "com/fluidattacks/agent/core/Sink", "enterSink",
							"([Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V",
							false);
					super.onMethodEnter();
				}
			};
		}
		return mv;
	}
}
