package com.github.xzzpig.pigutils.event;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.github.xzzpig.pigutils.json.JSONObject;

public interface EventRunner<T extends Event> {

	/**
	 * {@link EventRunner#getLimits()}中默认包含的一个限制 判断传入事件e是否为T的子类
	 * 
	 * @param e
	 * @return
	 */
	public default boolean canRun(Event e) {
		Class<?> c = (Class<?>) getType();
		return c.isAssignableFrom(e.getClass());
	}

	/**
	 * @return 事件通道，与事件所在通道不同时将不会被执行
	 */
	public default EventTunnel getEventTunnel() {
		return EventTunnel.defaultTunnel;
	}

	/**
	 * @return EventRunner的可能的一些其他信息(默认为null)
	 */
	public default JSONObject getInfo() {
		return null;
	}

	/**
	 * 当每个元素test()下来都为true时this.run()才会被执行
	 * 
	 * @return 限制条件
	 */
	public default List<Predicate<Event>> getLimits() {
		return Arrays.asList(this::canRun);
	}

	/**
	 * 执行的次要优先级 当主要优先级相同时按此排序 执行顺序:由小到大
	 * 
	 * @return
	 */
	public default int getMinorRunLevel() {
		return 0;
	}

	/**
	 * 执行的主要优先级
	 * 
	 * @see EventRunLevel
	 */
	public default EventRunLevel getRunLevel() {
		return EventRunLevel.Normal;
	}

	/**
	 * @return T的类型
	 */
	public default Type getType() {
		for (Method m : this.getClass().getMethods()) {
			if (m.getName().equalsIgnoreCase("run")) {
				return m.getGenericParameterTypes()[0];
			}
		}
		return null;
	}

	/**
	 * 是否忽略 {@link Event}的cancel 若忽略则当 {@link Event#isCanceled()}==true时
	 * 本EventRunner仍将执行
	 * 
	 * @return 是否忽略
	 * @see Event#isCanceled()
	 */
	public default boolean ignoreCanceled() {
		return false;
	}

	public void run(T event);
}
