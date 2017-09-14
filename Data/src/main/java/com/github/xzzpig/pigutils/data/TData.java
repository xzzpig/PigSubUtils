package com.github.xzzpig.pigutils.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class TData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private HashMap<String, Boolean> boos = new HashMap<>();
    private HashMap<String, Integer> ints = new HashMap<>();
    private HashMap<String, Object> obs = new HashMap<>();
    private HashMap<String, String> strs = new HashMap<>();

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
        return obj instanceof TData && equals((TData) obj);
    }

	public boolean equals(TData data) {
		if (!data.strs.equals(this.strs))
			return false;
		if (!data.ints.equals(this.ints))
			return false;
        return data.boos.equals(this.boos) && data.obs.equals(this.obs);
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
        for (Entry<String, Object> ioe : obs.entrySet()) {
            if (ioe.getValue() instanceof Serializable)
				continue;
			ioe.setValue(ioe.getValue().toString());
		}
		return this;
	}

	@Override
	public String toString() {
        List<String> ss = new ArrayList<>();
        for (Entry<String, String> ise : strs.entrySet()) {
            ss.add(ise.getKey() + "\t" + ise.getValue());
		}
        for (Entry<String, Integer> iie : ints.entrySet()) {
            ss.add(iie.getKey() + "\t" + iie.getValue());
		}
        for (Entry<String, Boolean> ibe : boos.entrySet()) {
            ss.add(ibe.getKey() + "\t" + ibe.getValue());
		}
        for (Entry<String, Object> ioe : obs.entrySet()) {
            ss.add(ioe.getKey() + "\t" + ioe.getValue());
		}
        StringBuilder sb = new StringBuilder();
        for (String s : ss) {
            sb.append(s).append("\n");
        }
		return sb.toString();
	}
}
