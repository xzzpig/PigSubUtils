package com.github.xzzpig.pigutils.crypt;

/**
 * 可加密
 */
public interface Cryptable {
	/**
	 * @return 加密结果
	 */
	String crypt();

	/**
	 * @param obj
	 * @return 是否匹配
	 */
	boolean match(Object obj);
}
