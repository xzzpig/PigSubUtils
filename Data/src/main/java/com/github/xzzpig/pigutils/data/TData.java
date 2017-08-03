package com.github.xzzpig.pigutils.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class TData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HashMap<String, Boolean> boos = new HashMap<String, Boolean>();
	private HashMap<String, Integer> ints = new HashMap<String, Integer>();
	private HashMap<String, Object> obs = new HashMap<String, Object>();
	private HashMap<String, String> strs = new HashMap<String, String>();

	public TData() {
	}

	public TData(String souce) {
		for (String ele : souce.split("\n")) {
			if (!ele.contains("\t"))
				continue;
			String key = ele.split("\t")[0];
			String value = ele.split("\t")[1];
			try {
				ints.put(key, Integer.valueOf(value));
				continue;
			} catch (Exception e) {
			}
			if (value.equalsIgnoreCase("true"))
				boos.put(key, true);
			else if (value.equalsIgnoreCase("false"))
				boos.put(key, false);
			else
				strs.put(key, value);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TData clone() {
		TData cloned = new TData();
		cloned.strs = (HashMap<String, String>) this.strs.clone();
		cloned.ints = (HashMap<String, Integer>) this.ints.clone();
		cloned.boos = (HashMap<String, Boolean>) this.boos.clone();
		cloned.obs = (HashMap<String, Object>) this.obs.clone();
		return cloned;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TData))
			return false;
		return equals((TData) obj);
	}

	public boolean equals(TData data) {
		if (!data.strs.equals(this.strs))
			return false;
		if (!data.ints.equals(this.ints))
			return false;
		if (!data.boos.equals(this.boos))
			return false;
		if (!data.obs.equals(this.obs))
			return false;
		return true;
	}

	public boolean getBoolan(String key) {
		if (!this.boos.containsKey(key))
			return false;
		return this.boos.get(key);
	}

	public HashMap<String, Boolean> getBooleans() {
		return this.boos;
	}

	public int getInt(String key) {
		if (!this.ints.containsKey(key))
			return 0;
		return this.ints.get(key);
	}

	public HashMap<String, Integer> getInts() {
		return this.ints;
	}

	public Object getObject(String key) {
		if (!this.obs.containsKey(key))
			return null;
		return this.obs.get(key);
	}

	public HashMap<String, Object> getObjects() {
		return this.obs;
	}

	public String getString(String key) {
		if (!this.strs.containsKey(key))
			return null;
		return this.strs.get(key);
	}

	public HashMap<String, String> getStrings() {
		return this.strs;
	}

	public TData setBoolean(String key, boolean value) {
		boos.put(key, value);
		return this;
	}

	public TData setInt(String key, int value) {
		ints.put(key, value);
		return this;
	}

	public TData setObject(String key, Object value) {
		obs.put(key, value);
		return this;
	}

	public TData setString(String key, String value) {
		strs.put(key, value);
		return this;
	}

	public TData toSerializable() {
		Iterator<Entry<String, Object>> io = obs.entrySet().iterator();
		while (io.hasNext()) {
			Entry<String, Object> ioe = io.next();
			if (ioe.getValue() instanceof Serializable)
				continue;
			ioe.setValue(ioe.getValue().toString());
		}
		return this;
	}

	@Override
	public String toString() {
		List<String> ss = new ArrayList<String>();
		Iterator<Entry<String, String>> is = strs.entrySet().iterator();
		while (is.hasNext()) {
			Entry<String, String> ise = is.next();
			ss.add(ise.getKey() + "\t" + ise.getValue());
		}
		Iterator<Entry<String, Integer>> ii = ints.entrySet().iterator();
		while (ii.hasNext()) {
			Entry<String, Integer> iie = ii.next();
			ss.add(iie.getKey() + "\t" + iie.getValue());
		}
		Iterator<Entry<String, Boolean>> ib = boos.entrySet().iterator();
		while (ib.hasNext()) {
			Entry<String, Boolean> ibe = ib.next();
			ss.add(ibe.getKey() + "\t" + ibe.getValue());
		}
		Iterator<Entry<String, Object>> io = obs.entrySet().iterator();
		while (io.hasNext()) {
			Entry<String, Object> ioe = io.next();
			ss.add(ioe.getKey() + "\t" + ioe.getValue());
		}
		StringBuffer sb = new StringBuffer();
		for (String s : ss) {
			sb.append(s + "\n");
		}
		return sb.toString();
	}
}
