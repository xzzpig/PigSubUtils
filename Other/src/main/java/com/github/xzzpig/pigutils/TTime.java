package com.github.xzzpig.pigutils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TTime {
	public static int getDate() {
		return Integer.valueOf(new SimpleDateFormat("dd").format(new Date()));
	}

	public static int getHour() {
		return Integer.valueOf(new SimpleDateFormat("HH").format(new Date()));
	}

	public static int getMinute() {
		return Integer.valueOf(new SimpleDateFormat("mm").format(new Date()));
	}

	public static int getMonth() {
		return Integer.valueOf(new SimpleDateFormat("MM").format(new Date()));
	}

	public static int getSecond() {
		return Integer.valueOf(new SimpleDateFormat("ss").format(new Date()));
	}

	public static int getYear() {
		return Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date()));
	}

}
