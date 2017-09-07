package com.github.xzzpig.pigutils.annoiation;

import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

/**
 * 提供API方面的注解
 */
@Documented
@Retention(CLASS)
public @interface API {

    enum ComplexValue {
        /**
		 * O(1)
		 */
		O_1,
		/**
		 * O(N)
		 */
		O_N,
		/**
		 * O(N^2)
		 */
		O_N2,
		/**
		 * O(log(n))
		 */
		O_LgN,
		/**
		 * O(n*log(n))
		 */
		O_NLgN,
		/**
		 * O(2^n)
		 */
		O_2N, FAST, SLOW, VERY_SLOW, MAYBLOCK
	}

	/**
	 * 标记是否为API<br/>
	 * false:表示被注解的对象虽然可用被访问到，但并不希望被<mark>外部</mark>使用
	 */
	boolean value() default true;

	/**
	 * 算法复杂度
	 */
	String[] complex_s() default {};

	/**
	 * 算法复杂度
	 */
	ComplexValue[] complex() default {};

}
