package com.github.xzzpig.pigutils.duck;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.xzzpig.pigutils.annoiation.BaseOnClass;
import com.github.xzzpig.pigutils.annoiation.BaseOnPackage;
import com.github.xzzpig.pigutils.annoiation.Const;
import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;
import com.github.xzzpig.pigutils.data.DataUtils;
import com.github.xzzpig.pigutils.data.DataUtils.EachResult;
import com.github.xzzpig.pigutils.reflect.AnnotatedElementCheckEvent;
import com.github.xzzpig.pigutils.reflect.ClassUtils;
import com.github.xzzpig.pigutils.reflect.MethodUtils;

/**
 * 鸭子对象<br/>
 * 可调用封装的Object的方法(返回 {@link DuckObject})<br/>
 * 可获取/设置封装的Object的Field( {@link DuckObject}可自动转换)<br/>
 * 
 */
@BaseOnPackage("com.github.xzzpig.pigutils.reflect")
@Const(constField = true)
public final class DuckObject {

	static {
		AnnotatedElementCheckEvent.regAnnotatedElementChecker(DuckObject::isAnnounceMarched);
	}

	public static boolean isAnnounceMarched(AnnotatedElement element, Object obj) {
        return !(obj instanceof DuckObject) || ((DuckObject) obj).isAnnounceMarched(element);
    }

	@BaseOnClass(DataUtils.class)
	public static boolean isMethodAnnounceMarched(Method method, Object... args) {
		if (args == null) {
			if (method.getParameterCount() == 0)
				return true;
			args = new Object[] { null };
		}
		if (args.length != method.getParameterCount())
			return false;
		AtomicBoolean march = new AtomicBoolean(true);
		DataUtils.forEachWithIndex(args, (obj, i) -> {
			Parameter parameter = method.getParameters()[i];
			if (!parameter.getType().equals(DuckObject.class))
				return EachResult.CONTNUE;
			if (!((DuckObject) obj).isAnnounceMarched(parameter)) {
				march.set(false);
				return EachResult.BREAK;
			}
			return null;
		});
		return march.get();
	}

	private ClassUtils<?> cu;

	public final Object object;

	/**
	 * 创建对象并封装为DuckObject
	 * 
	 * @param clazz
	 *            创建对象的类型
	 * @param args
	 *            创建对象的构造函数的参数
	 */
	public DuckObject(@NotNull Class<?> clazz, @Nullable Object... args) {
		if (clazz == null)
			throw new IllegalArgumentException(new NullPointerException("clazz can not be Null"));
		cu = new ClassUtils<>(clazz);
		object = cu.newInstance(args);
		if (object == null)
			throw new IllegalArgumentException(new NullPointerException("obj can not be Null"));
	}

	/**
	 * @param obj
	 *            被封装的对象
	 */
	public DuckObject(@NotNull Object obj) {
		if (obj == null)
			throw new IllegalArgumentException(new NullPointerException("obj can not be Null"));
		this.object = obj;
		this.cu = new ClassUtils<>(obj.getClass());
	}

	public ClassUtils<?> getClassUtils() {
		return cu;
	}

	public <T> T getField(String fieldName, Class<T> clazz) {
		return cu.getFieldUtils(fieldName).get(object, clazz);
	}

	public DuckObject getField(String filedName) {
		return new DuckObject(getField(filedName, Object.class));
	}

	public Class<?> getType() {
		return object.getClass();
	}

	/**
	 * 调用封装Object的某无参数方法
	 * 
	 * @param methodName
	 *            方法名称
	 * @return new {@link DuckObject}(方法返回值)
	 */
	public DuckObject invoke(@NotNull String methodName) {
		return invoke(methodName, new Object[0]);
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(Class<T> clazz) {
		return (T) object;
	}

	/**
	 * 调用封装Object的某方法
	 * 
	 * @param methodName
	 *            方法名称
	 * @param parameters
	 *            方法参数
	 * @return new {@link DuckObject}(方法返回值)
	 */
	public DuckObject invoke(@NotNull String methodName, @Nullable Object... parameters) {
		MethodUtils mu = cu.getMethodUtils(methodName, parameters);
		if (mu == null) {
			Object objs[] = Arrays.copyOf(parameters, parameters.length);
			mu = findMethod(methodName, cu, objs, 0);
			parameters = objs;
		}
		if (mu == null)
			return null;
		Object obj = cu.getMethodUtils(methodName).invoke(object, parameters);
		if (obj == null)
			return null;
		else
			return new DuckObject(obj);
	}

	public static MethodUtils findMethod(String name, ClassUtils<?> cu, Object[] objs, int i) {
		MethodUtils mu;
		if (i == objs.length - 1)
			mu = cu.getMethodUtils(name, objs);
		else
			mu = findMethod(name, cu, objs, i + 1);
		if (mu != null)
			return mu;
		if (objs[i] instanceof DuckObject)
			objs[i] = ((DuckObject) objs[i]).object;
		else
			objs[i] = new DuckObject(objs[i]);
		if (i == objs.length - 1)
			mu = cu.getMethodUtils(name, objs);
		else
			mu = findMethod(name, cu, objs, i + 1);
		return mu;
	}

	/**
	 * 判断Announce是否匹配
	 * 
	 * @see HasField
	 * @see HasMethod
	 * @see LikeClass
	 * 
	 * @param element
	 */
	public boolean isAnnounceMarched(AnnotatedElement element) {
		if (element.isAnnotationPresent(HasField.class)) {
			HasField hasField = element.getDeclaredAnnotation(HasField.class);
			if (hasField.value() != null)
				for (String fieldName : hasField.value())
					if (cu.getField(fieldName) == null)
						return false;
		}
		if (element.isAnnotationPresent(HasMethod.class)) {
			HasMethod hasMethod = element.getDeclaredAnnotation(HasMethod.class);
			if (hasMethod.value() != null)
				for (String methodName : hasMethod.value())
					if (cu.getMethod(methodName) == null)
						return false;
		}
		if (element.isAnnotationPresent(LikeClass.class)) {
			LikeClass likeClass = element.getAnnotation(LikeClass.class);
			if (likeClass.value() != null)
				for (Class<?> clazz : likeClass.value())
					if (!isLike(clazz, likeClass.checkField(), likeClass.checkMethod()))
						return false;

		}
		return true;
	}

	/**
	 * 判断DuckObject是否与clazz类似<br/>
	 * Field判断名称和类型<br/>
	 * Method判断名称和参数列表
	 */
	public boolean isLike(@NotNull Class<?> clazz, @Nullable boolean checkField, @Nullable boolean checkMethod) {
		if (checkField) {
			for (Field field : clazz.getFields()) {
				Field objField = cu.getField(field.getName());
				if (objField == null)
					return false;
				if (!field.getType().isAssignableFrom(objField.getType())) {
					return false;
				}
			}
		}
		if (checkMethod) {
			for (Method method : clazz.getMethods()) {
				Method objMethod = cu.getMethod(method.getName(), method.getParameterTypes());
				if (objMethod == null)
					return false;
				if (!method.getReturnType().isAssignableFrom(objMethod.getReturnType())
						&& method.getReturnType() != Void.TYPE)
					return false;
			}
		}
		return true;
	}

	public DuckObject setField(String fieldName, Object value) {
		if (cu.getFieldUtils(fieldName).getField().getType() != DuckObject.class && value instanceof DuckObject) {
			return setField(fieldName, ((DuckObject) value).object);
		}
		if (!cu.getFieldUtils(fieldName).set(this.object, value)) {
			throw new RuntimeException(new NoSuchFieldException(fieldName + " not Found"));
		}
		return this;
	}

	@Override
	public boolean equals(Object obj) {
        return obj instanceof DuckObject && ((DuckObject) obj).object.equals(object);
    }

	@Override
	public String toString() {
		return object.toString();
	}

	@Override
	public int hashCode() {
		return object.hashCode();
	}

	/**
	 * 将this使用
	 * {@link Proxy#newProxyInstance(ClassLoader, Class[], InvocationHandler)}
	 * 转换成对应Object
	 * 
	 * @param mainInterface
	 * @param otherInterface
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T cast(@NotNull Class<T> mainInterface, @NotNull Class<?>... otherInterface) {
		Class<?>[] clazzs = Arrays.copyOf(otherInterface, otherInterface.length + 1);
		clazzs[otherInterface.length] = mainInterface;
        Object obj = Proxy.newProxyInstance(getClass().getClassLoader(), clazzs, (proxy, method, args) -> {
            DuckObject duckObj = DuckObject.this.invoke(method.getName(), args);
            if (duckObj == null)
                return null;
            else
                return duckObj.object;
        });
        return (T) obj;
	}

}
