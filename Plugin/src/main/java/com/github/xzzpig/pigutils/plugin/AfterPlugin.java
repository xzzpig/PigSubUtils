package com.github.xzzpig.pigutils.plugin;

import java.lang.annotation.*;

/**
 * 表示该对象/方法在加载某插件后才可以使用
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface AfterPlugin {
    String[] value() default {"*"};
}
