package com.github.xzzpig.pigutils.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;
import com.github.xzzpig.pigutils.event.Event;

public class ClassUtils<T> {

	private Class<T> clazz;

	public ClassUtils(@NotNull Class<T> clazz) {
		this.clazz = clazz;
	}

	public void checkConstructorArgs(@Nullable Object... args) {
		Constructor<T> constructor;
		constructor = getConstructor(args);
		Parameter[] parameters = constructor.getParameters();
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

	@SuppressWarnings("unchecked")
	public Constructor<T> getConstructor(Object... args) {
		for (Constructor<?> cons : clazz.getDeclaredConstructors()) {
			if (isParameterMarch(cons.getParameters(), args))
				return (Constructor<T>) cons;
		}
		return null;
	}

	public Field getField(String name) {
		Field field;
		try {
			field = clazz.getDeclaredField(name);
		} catch (NoSuchFieldException | SecurityException e) {
			try {
				field = clazz.getField(name);
			} catch (NoSuchFieldException | SecurityException e1) {
				field = null;
			}
		}
		if (field != null || clazz == Object.class)
			return field;
		return new ClassUtils<>(clazz.getSuperclass()).getField(name);
	}

	public FieldUtils getFieldUtils(String name) {
		Field field = getField(name);
		return field == null ? null : new FieldUtils(field);
	}

	public Method getMethod(String name) {
		for (Method m : clazz.getDeclaredMethods()) {
			if (m.getName().equalsIgnoreCase(name))
				return m;
		}
		for (Method m : clazz.getMethods()) {
			if (m.getName().equalsIgnoreCase(name))
				return m;
		}
		return null;
	}

	public Method getMethod(String name, Class<?>... parameterTypes) {
		try {
			return clazz.getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			try {
				return clazz.getMethod(name, parameterTypes);
			} catch (NoSuchMethodException | SecurityException e1) {
				return null;
			}
		}
	}

	public Method getMethod(@NotNull String name, @Nullable Object... args) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getName().equalsIgnoreCase(name) && isParameterMarch(method.getParameters(), args))
				return method;
		}
		for (Method method : clazz.getMethods()) {
			if (method.getName().equalsIgnoreCase(name) && isParameterMarch(method.getParameters(), args))
				return method;
		}
		return null;
	}

	public MethodUtils getMethodUtils(String name) {
		Method m = getMethod(name);
		if (m == null)
			return null;
		return new MethodUtils(m);
	}

	public Object invokeMethod(String name, Object obj, Object... args) {
		return getMethodUtils(name, args).invoke(obj, args);
	}

	public MethodUtils getMethodUtils(String name, Class<?>... parameterTypes) {
		Method m = getMethod(name, parameterTypes);
		if (m == null)
			return null;
		return new MethodUtils(m);
	}

	public MethodUtils getMethodUtils(@NotNull String name, @Nullable Object... args) {
		Method m = getMethod(name, args);
		if (m == null)
			return null;
		return new MethodUtils(m);
	}

	public boolean hasField(String name) {
		return getField(name) != null;
	}

	public T newInstance() {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public T newInstance(@Nullable Object... args) {
		if (args == null) {
			T t = newInstance();
			if (t != null)
				return newInstance();
			else
				args = new Object[] { null };
		}
		Object[] args2Stream = args;
		try {
			Constructor<T> constructor = Stream
					.concat(Stream.of(clazz.getDeclaredConstructors()), Stream.of(clazz.getConstructors())).distinct()
					.map(cons -> (Constructor<T>) cons).filter(cons -> cons.getParameterCount() == args2Stream.length)
					.filter(cons -> {
						for (int i = 0; i < cons.getParameters().length; i++) {
							if (args2Stream[i] == null)
								continue;
							Parameter parameter = cons.getParameters()[i];
							if (!parameter.getType().isInstance(args2Stream[i])) {
								ClassUtils<?> argCU = new ClassUtils<>(args2Stream[i].getClass());
								if (!argCU.hasField("TYPE"))
									return false;
								if (!argCU.getFieldUtils("TYPE").get(args2Stream[i], Class.class)
										.isAssignableFrom(parameter.getType()))
									return false;
							}
						}
						return true;
					}).findFirst().get();
			checkConstructorArgs(args);
			boolean access = constructor.isAccessible();
			try {
				constructor.setAccessible(true);
				return constructor.newInstance(args);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				return null;
			} finally {
				constructor.setAccessible(access);
			}
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	public boolean set(String fieldName, Object obj, Object value) {
		Field field = getField(fieldName);
		if (field == null)
			return false;
		boolean access = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(obj, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return false;
		} finally {
			field.setAccessible(access);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static boolean isParameterMarch(@Nullable Parameter[] parameters, @Nullable Object[] objects) {
		if ((parameters == null || parameters.length == 0) && objects == null)
			return true;
		if (objects == null)
			objects = new Object[] { null };
		if (parameters.length != objects.length)
			return false;
		for (int i = 0; i < parameters.length; i++) {
			if (objects[i] == null)
				continue;
			Parameter parameter = parameters[i];
			if (!parameter.getType().isInstance(objects[i])) {
				ClassUtils<?> argCU = new ClassUtils<>(objects[i].getClass());
				if (!argCU.hasField("TYPE"))
					return false;
				if (!argCU.getFieldUtils("TYPE").get(objects[i], Class.class).isAssignableFrom(parameter.getType()))
					return false;
			}
		}
		return true;
	}

	public static void checkThisConstructorArgs(Object... args) {
		StackTraceElement stack = new Exception().getStackTrace()[1];
		Class<?> clazz;
		try {
			clazz = Class.forName(stack.getClassName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		ClassUtils<?> cu = new ClassUtils<>(clazz);
		cu.checkConstructorArgs(args);
	}

	public static Class<?> getStackClass(int i) {
		StackTraceElement stack = new Exception().getStackTrace()[i];
		try {
			return Class.forName(stack.getClassName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
