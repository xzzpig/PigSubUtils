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
	 * 表示对象成员不变<br/>
	 * 注解在Method上表示该方法不改变对象的Field<br/>
	 * 注解在Field上表示<b>不希望</b>调用会改变该对象状态的Method<br/>
	 * 注解在Class上表示该类为不可变类<br/>
	 * 注解在Arguement上表示该Method不会改变这参数的对象状态
	 */
	boolean constField() default false;

	/**
	 * 表示对象引用不变<br/>
	 * 注解在Field上表示<b>不希望</b>外部更改该Field的引用(类似final,懒的写getter) <br/>
	 * 注解在Method上表示<b>不希望</b>改变该方法返回值的对象状态
	 */
	boolean constReference() default false;
}
