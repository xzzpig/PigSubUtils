package com.github.xzzpig.pigutils.event;

/**
 * {@link Event}执行的通道 {@link EventBus}只会执行相同通道的 {@link EventRunner}
 * 
 * @author xzzpig
 *
 */
public class EventTunnel {
	public static final EventTunnel defaultTunnel = new EventTunnel("default");

	private String tunnel;

	public EventTunnel(String tunnel) {
		this.tunnel = tunnel;
	}

	@Override
	public boolean equals(Object obj) {
		return this.toString().equalsIgnoreCase(obj.toString());
	}

	@Override
	public int hashCode() {
		return tunnel.hashCode();
	}

	@Override
	public String toString() {
		return tunnel;
	}
}
