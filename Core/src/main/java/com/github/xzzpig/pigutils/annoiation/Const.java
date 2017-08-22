package com.github.xzzpig.pigutils.annoiation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

/**
 * 标识对象为不可变(不会变) <br/>
 * 表示建议,无实际作用
 */
@Documented
@Retention(RUNTIME)
public @interface Const {
	/**
	 * 表示对象成员不变
	 */
	boolean constField() default false;

	/**
	 * 表示对象引用不变
	 */
	boolean constReference() default false;
}
