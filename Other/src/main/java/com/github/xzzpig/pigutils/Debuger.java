package com.github.xzzpig.pigutils;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Debuger {
	public static boolean debug = false;

	private static HashMap<String, Boolean> debugMap = new HashMap<String, Boolean>();

	public static PrintStream out = System.err;

	public static long time;

	public static void print(Object s) {
		StackTraceElement stack[] = Thread.currentThread().getStackTrace();
		String callName = stack[2].getClassName();
		boolean isdebug;
		if (debugMap.containsKey(callName))
			isdebug = debugMap.get(callName);
		else
			isdebug = debug;
		if (isdebug == false)
			return;
		if (s instanceof Exception) {
			((Exception) s).printStackTrace();
			return;
		} else if (s instanceof List<?>) {
			out.println(Arrays.toString(((List<?>) s).toArray()));
			return;
		} else if (s.getClass().isArray()) {
			out.println(Arrays.toString((Object[]) s));
			return;
		}
		out.println(s);
	}

	public static void setIsDebug(Class<?> classname, boolean isdebug) {
		debugMap.put(classname.getName(), isdebug);
	}

	public static void timeStart() {
		time = System.nanoTime();
	}

	public static void timeStop(String s) {
		Debuger.print(s + (System.nanoTime() - time));
	}
}
