package com.github.xzzpig.pigutils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;

import com.github.xzzpig.pigutils.json.JSONArray;
import com.github.xzzpig.pigutils.json.JSONObject;

public class TString {
	/**
	 * 将args转换成JSONObject 单个arg格式: -key:value
	 * key作为JSONObject的key，value作为JSONObject的value，
	 * 
	 * @param args
	 * @return 转换成的JSONObject
	 */
	public static JSONObject formatArgs(String[] args) {
		JSONObject r = new JSONObject();
		JSONArray arr = new JSONArray();
		for (String arg : args) {
			if (!arg.startsWith("-")) {
				arr.put(arg);
				continue;
			} else if (!arg.contains(":")) {
				r.put(arg, true);
			}
			arg = arg.replaceFirst("-", "");
			String[] kv = arg.split(":", 2);
			r.put(kv[0], kv[1]);
		}
		r.put("other", arr);
		return r;
	}

	public static String getRandomCH(int len) {
		String ret = "";
		for (int i = 0; i < len; i++) {
			String str = null;
			int hightPos, lowPos; // 定义高低位
			Random random = new Random();
			hightPos = (176 + Math.abs(random.nextInt(39))); // 获取高位值
			lowPos = (161 + Math.abs(random.nextInt(93))); // 获取低位值
			byte[] b = new byte[2];
			b[0] = (new Integer(hightPos).byteValue());
			b[1] = (new Integer(lowPos).byteValue());
			try {
				str = new String(b, "GBk"); // 转成中文
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			}
			ret += str;
		}
		return ret;
	}

	public static String sub(String source, String pre, String suf) {
		int f = source.indexOf(pre);
		int e = source.indexOf(suf, f);
		return source.substring(f + pre.length(), e);
	}

	public static String toString(Object object) {
		StringBuffer sb = new StringBuffer();
		if (object instanceof List<?>) {
			List<?> list = (List<?>) object;
			sb.append('[');
			for (Object object2 : list) {
				sb.append(toString(object2)).append(',');
			}
			if (sb.toString().equalsIgnoreCase("["))
				sb.append(',');
			sb.replace(sb.length() - 1, sb.length(), "]");
		} else if (object instanceof byte[]) {
			sb.append(new String((byte[]) object));
		} else {
			sb.append(object);
		}
		return sb.toString();
	}

	public static String toUnicodeString(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				sb.append("\\u" + Integer.toHexString(c));
			}
		}
		return sb.toString();
	}
}
