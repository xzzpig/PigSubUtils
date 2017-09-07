package com.github.xzzpig.pigutils.event;

import java.util.function.Predicate;

/**
 * 持有一个EventBus对象 可作为事件的接收体
 * 
 * @author xzzpig
 *
 */
public interface EventAdapter {
	/**
	 * 调用{@link EventAdapter#getEventBus()}的{@link EventBus#callEvent(Event)}
	 */
    default void callEvent(Event e) {
        getEventBus().callEvent(e);
	}

	/**
	 * 调用{@link EventAdapter#getEventBus()}的{@link EventBus#callEvent(Event, EventTunnel)}
	 */
    default void callEvent(Event e, EventTunnel tunnel) {
        getEventBus().callEvent(e, tunnel);
	}

	/**
	 * @return 持有的EventBus,作为该类其他方法的基础EventBus
	 */
    EventBus getEventBus();

	/**
	 * 调用{@link EventAdapter#getEventBus()}的{@link EventBus#regListener(Class)}
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
    default EventAdapter regListener(Class<Listener> c) throws InstantiationException, IllegalAccessException {
        getEventBus().regListener(c);
		return this;
	}

	/**
	 * 调用{@link EventAdapter#getEventBus()}的{@link EventBus#regListener(Listener)}
	 */
    default EventAdapter regListener(Listener listener) {
        getEventBus().regListener(listener);
		return this;
	}

	/**
	 * 调用{@link EventAdapter#getEventBus()}的{@link EventBus#regRunner(Class)
	 */
    default EventAdapter regRunner(Class<? extends EventRunner<?>> c)
            throws InstantiationException, IllegalAccessException {
		getEventBus().regRunner(c);
		return this;
	}

	/**
	 * 调用{@link EventAdapter#getEventBus()}的{@link EventBus#regRunner(EventRunner)}
	 */
    default EventAdapter regRunner(EventRunner<? extends Event> runner) {
        getEventBus().regRunner(runner);
		return this;
	}

	/**
	 * 调用{@link EventAdapter#getEventBus()}的{@link EventBus#unregListener(Class)}
	 */
    default EventAdapter unregListener(Class<Listener> c) {
        getEventBus().unregListener(c);
		return this;
	}

	/**
	 * 调用{@link EventAdapter#getEventBus()}的{@link EventBus#unregListener(Listener)}
	 */
    default EventAdapter unregListener(Listener listener) {
        getEventBus().unregListener(listener);
		return this;
	}

	/**
	 * 调用{@link EventAdapter#getEventBus()}的{@link EventBus#unregRunner(Class)}
	 */
    default EventAdapter unregRunner(Class<? extends EventRunner<?>> c) {
        getEventBus().unregRunner(c);
		return this;
	}

	/**
	 * 调用{@link EventAdapter#getEventBus()}的{@link EventBus#unregRunner(Predicate)}
	 */
    default EventAdapter unregRunner(Predicate<EventRunner<?>> p) {
        getEventBus().unregRunner(p);
		return this;
	}
}
