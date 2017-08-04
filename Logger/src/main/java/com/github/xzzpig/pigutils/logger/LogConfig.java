package com.github.xzzpig.pigutils.logger;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

/**
 * 使用本注解初始化 {@link Logger#getLogger()}<br/>
 * 使用默认值表示使用父配置
 */
@Documented
@Retention(RUNTIME)
public @interface LogConfig {
	String formater() default "extened";

	String level() default "extended";

	String[] printer() default {};
}
