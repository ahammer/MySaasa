package com.mysaasa.messages.data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This is just a bundle of data
 *
 * Similar to the Intent and it's Extras()
 *
 * Created by administrator on 2014-05-19.
 */
public class Bundle implements Serializable {
	final private HashMap<String, Serializable> backing = new HashMap<String, Serializable>();

	public Bundle() {}

	public void put(String key, Serializable value) {
		if (value instanceof String || value instanceof Double || value instanceof Integer || value instanceof Boolean) {
			backing.put(key, value);
		} else {
			throw new IllegalArgumentException("Not of supported type: " + value.getClass());
		}
	}

	public void remove(String key) {
		if (backing.containsKey(key))
			backing.remove(key);
		throw new IllegalArgumentException("Key does not exist");
	}

	public String getString(String key) {
		Object obj = backing.get(key);
		if (obj == null) {
			throw new IllegalArgumentException("Key not found");
		}
		if (obj instanceof String) {
			return (String) obj;
		}
		throw new ClassCastException("Found " + obj.getClass() + " But Expected a String");
	}

	public Integer getInt(String key) {
		Object obj = backing.get(key);
		if (obj == null) {
			throw new IllegalArgumentException("Key not found");
		}
		if (obj instanceof Integer) {
			return (Integer) obj;
		}
		throw new ClassCastException("Found " + obj.getClass() + " But Expected a Integer");
	}

	public Double getDouble(String key) {
		Object obj = backing.get(key);
		if (obj == null) {
			throw new IllegalArgumentException("Key not found");
		}
		if (obj instanceof Double) {
			return (Double) obj;
		}
		throw new ClassCastException("Found " + obj.getClass() + " But Expected a Double");
	}

	public Boolean getBoolean(String key) {
		Object obj = backing.get(key);
		if (obj == null) {
			return false;
		}
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		}
		throw new ClassCastException("Found " + obj.getClass() + " But Expected a Boolean");
	}

	@Override
	public String toString() {
		return "Bundle{" + "backing=" + backing + '}';
	}

	public void addAll(Bundle extras) {
		for (String key : extras.backing.keySet()) {
			backing.put(key, extras.backing.get(key));
		}
	}
}
