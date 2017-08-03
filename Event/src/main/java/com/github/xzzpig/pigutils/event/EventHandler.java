package com.github.xzzpig.pigutils.event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记{@link Listener}中可被解析为{@link EventRunner} 的方法
 * 
 * @author xzzpig
 *
 */
@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

	/**
	 * 对应 {@link EventRunner#ignoreCanceled()}
	 * 
	 * @see EventRunner#ignoreCanceled()
	 */
	public boolean ignoreCanceled() default false;

	/**
	 * 对应 {@link EventRunner#getRunLevel()}
	 * 
	 * @see EventRunner#getRunLevel()
	 */
	public EventRunLevel mainLevel() default EventRunLevel.Normal;

	/**
	 * 对应 {@link EventRunner#getMinorRunLevel()}
	 * 
	 * @see EventRunner#getMinorRunLevel()
	 */
	public int minorLevel() default 0;

	/**
	 * 对应 {@link EventRunner#getEventTunnel()}
	 * 
	 * @return 将会被转换成对应的{@link EventTunnel}
	 * @see EventRunner#getEventTunnel()
	 */
	public String tunnel() default "default";
}
