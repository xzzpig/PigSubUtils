package com.github.xzzpig.pigutils.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import com.github.xzzpig.pigutils.annoiation.AnnotatedElementCheckEvent;
import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.event.Event;

public class MethodUtils {
	public static String getStackMethodName(int i) {
		return new Exception().getStackTrace()[i].getMethodName();
	}

	public static Method getStackMethod(int i) {
		StackTraceElement stack = new Exception().getStackTrace()[i];
		Class<?> clazz;
		try {
			clazz = Class.forName(stack.getClassName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		ClassUtils<?> cu = new ClassUtils<>(clazz);
		return cu.getMethod(stack.getMethodName());
	}

	public static Method getStackMethod(int i, Object... args) {
		StackTraceElement stack = new Exception().getStackTrace()[i];
		Class<?> clazz;
		try {
			clazz = Class.forName(stack.getClassName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		ClassUtils<?> cu = new ClassUtils<>(clazz);
		return cu.getMethod(stack.getMethodName(), args);
	}

	public static void checkArgs(Class<?> clazz, String methodName, Object... args) {
		new ClassUtils<>(clazz).getMethodUtils(methodName).checkArgs(args);
	}

	public static void checkThisArgs(Object... args) {
		new MethodUtils(getStackMethod(2, args)).checkArgs(args);
	}

	private Method method;

	public MethodUtils(Method method) {
		this.method = method;
	}

	/**
	 * 检验本方法的参数<br/>
	 * 本方法会触发 {@link AnnotatedElementCheckEvent}
	 * 
	 * @param args
	 */
	public void checkArgs(Object... args) {
		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			Object arg = args.length <= i ? null : args[i];
			if (parameter.isAnnotationPresent(NotNull.class) && arg == null) {
				throw new IllegalArgumentException(
						new NullPointerException("parameter " + parameter.getName() + " can not be Null"));

			}
			AnnotatedElementCheckEvent event = new AnnotatedElementCheckEvent(parameter, arg);
			Event.callEvent(event);
			if (event.isFailed())
				throw new IllegalArgumentException(parameter.getName() + " check failed");
		}
	}

	public Method getMethod() {
		return method;
	}

	public Class<?>[] getParmameters() {
		return method.getParameterTypes();
	}

	public Object invoke(Object obj, Object... parameters) {
		if (parameters == null) {
			parameters = new Object[0];
		}
		if (parameters.length != getParmameters().length) {
			parameters = Arrays.copyOf(parameters, getParmameters().length);
		}
		checkArgs(parameters);
		boolean access = method.isAccessible();
		method.setAccessible(true);
		Object result = null;
		try {
			result = method.invoke(obj, parameters);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		}
		method.setAccessible(access);
		return result;
	}
}
