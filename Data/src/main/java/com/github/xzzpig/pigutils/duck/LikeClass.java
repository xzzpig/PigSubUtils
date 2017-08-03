package com.github.xzzpig.pigutils.duck;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import com.github.xzzpig.pigutils.reflect.MethodUtils;

/**
 * 标识DuckObject的封装Object需要像某Class<br/>
 * 可用 {@link MethodUtils#checkArgs(Object...)} 或
 * {@link DuckObject#isLike(Class, boolean, boolean)}检验
 */
@Retention(RUNTIME)
public @interface LikeClass {
	boolean checkField() default true;

	boolean checkMethod() default true;

	Class<?>[] value();
}
