package com.github.xzzpig.pigutils.annoiation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 标记非空<br />
 * 对于方法参数可用 {@link com.github.xzzpig.pigutils.reflect.MethodUtils#checkThisArgs(Object...)}检查
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {

}
