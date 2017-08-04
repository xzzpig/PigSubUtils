package com.github.xzzpig.pigutils.logger;

import java.util.HashSet;
import java.util.logging.Level;

import com.github.xzzpig.pigutils.annoiation.BaseOnPackage;
import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;
import com.github.xzzpig.pigutils.reflect.ClassUtils;

@BaseOnPackage("com.github.xzzpig.pigutils.annoiation")
public class LogLevel {

	private static HashSet<LogLevel> levels;

	public static final LogLevel ALL = new LogLevel("ALL", Integer.MIN_VALUE);
	public static final LogLevel DEBUG = new LogLevel("DEBUG", -100);
	public static final LogLevel INFO = new LogLevel("INFO", 0);
	public static final LogLevel WARN = new LogLevel("WARN", 0);
	public static final LogLevel ERROR = new LogLevel("ERROR", 200);
	public static final LogLevel FATAL = new LogLevel("FATAL", 300);
	public static final LogLevel OFF = new LogLevel("OFF", Integer.MAX_VALUE);

	public static void addLevel(String name, int level) {
		if (name == null || name.equals(""))
			return;
		new LogLevel(name, level);
	}
	public static LogLevel getLevel(String name) {
		try {
			return levels.stream().filter(l -> l.name.equalsIgnoreCase(name)).findFirst().get();
		} catch (Exception e) {
			return new LogLevel(name, 0);
		}
	}

	private String name;

	private int level;

	private LogLevel(@NotNull String name, @Nullable int level) {
		ClassUtils.checkThisConstructorArgs(name, level);
		this.name = name.toUpperCase();
		this.level = level;
		if (levels == null)
			levels = new HashSet<>();
		levels.add(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LogLevel) {
			Level l = (Level) obj;
			return l.getName().equalsIgnoreCase(name);
		} else
			return false;
	}

	public int getLevel() {
		return level;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "LogLevel[" + getName() + "]";
	}
}
