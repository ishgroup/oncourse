/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.util;

import java.util.*;

/**
 * A convenience class for populating Maps with keys and values. This is especially useful when you need a one liner, such as in a constructor's call to super
 * or an interface declaration.
 */
public final class Maps {

	private static final int TYPE_DEFAULT = 1;
	private static final int TYPE_LINKED = 2;
	private static final int TYPE_SORTED = 3;

	private Maps() {}

	/**
	 * @param key - the key
	 * @param value - a value for the key
	 * @return a map with the key value pair
	 */
	public static <K, V> Map<K, V> asMap(final K key, final V value) {
		HashMap<K, V> aMap = new HashMap<>();
		aMap.put(key, value);
		return aMap;
	}

	/**
	 * @param keys - an array of keys
	 * @param values - the values to set for those keys
	 * @return a map with the key value pairs provided
	 */
	public static <K, V> Map<K, V> asMap(final K[] keys, final V[] values) {
		return asMap(keys, values, TYPE_DEFAULT, null);
	}

	/**
	 * @param keyValues - an array in the form key1, value1, key2, value2, ...
	 * @return a map with the given key value pairs.
	 */
	public static <S> Map<S, S> asMap(final S[] keyValues) {
		Map<S, S> result;

		if (keyValues == null) {
			return new HashMap<>();
		} else if (keyValues.length % 2 != 0) {
			throw new IllegalStateException("keyValues must have an even number of elements.");
		}
		result = new HashMap<>();
		for (int i = 0, count = keyValues.length; i < count; i += 2) {
			if (keyValues[i] == null) {
				throw new IllegalStateException("Attempting to add a keyvalue pair with a null key.");
			}
			result.put(keyValues[i], keyValues[i + 1]);
		}
		return result;
	}

	/**
	 * @param keys - the list of keys
	 * @param keysAlsoAsvalues - whether to additionally map the keys as values.
	 * @return a map with the keys mapped to themselves or null.
	 */
	public static <K> Map<K, ?> asMap(final K[] keys, final boolean keysAlsoAsvalues) {
		if (keysAlsoAsvalues) {
			return asMap(keys, keys, TYPE_DEFAULT, null);
		} else if (keys == null) {
			return asMap(keys, null, TYPE_DEFAULT, null);
		}
		return asMap(keys, new Object[keys.length], TYPE_DEFAULT, null);
	}

	/**
	 * @param key - the key
	 * @param value - the value
	 * @return a linked map with the single key value pair
	 */
	public static <K, V> Map<K, V> asLinkedMap(K key, V value) {
		LinkedHashMap<K, V> aMap = new LinkedHashMap<>();
		aMap.put(key, value);
		return aMap;
	}

	/**
	 * @param keys - the list of keys
	 * @param values - the values for the given keys
	 * @return a linked map of the given key-value pairs
	 */
	public static <K, V> Map<K, V> asLinkedMap(final K[] keys, final V[] values) {
		return asMap(keys, values, TYPE_LINKED, null);
	}

	/**
	 * @param key - the key
	 * @param value - the value
	 * @return a sorted map initialised with the key value pair (with the default comparator).
	 */
	public static <K, V> SortedMap<K, V> asSortedMap(final K key, final V value) {
		SortedMap<K, V> aMap = new TreeMap<>();
		aMap.put(key, value);
		return aMap;
	}

	/**
	 * @param key - the key
	 * @param value - the value
	 * @param c - a comparator to sort by
	 * @return a sorted map with the key value pair (with the given comparator).
	 */
	public static <K, V> SortedMap<K, V> asSortedMap(final K key, final V value, final Comparator<? super K> c) {
		SortedMap<K, V> aMap = new TreeMap<>(c);
		aMap.put(key, value);
		return aMap;
	}

	/**
	 * @param keys
	 * @param values
	 * @return a sorted map with the key value pair (with the default comparator).
	 */
	public static <K, V> SortedMap<K, V> asSortedMap(final K[] keys, final V[] values) {
		return (SortedMap<K, V>) asMap(keys, values, TYPE_SORTED, null);
	}

	/**
	 * @param keys - the initial keys
	 * @param values - the values
	 * @param c - the comparator
	 * @return a sorted map with the key value pairs (with the given comparator).
	 */
	public static <K, V> SortedMap<K, V> asSortedMap(final K[] keys, final V[] values, final Comparator<? super K> c) {
		return (SortedMap<K, V>) asMap(keys, values, TYPE_SORTED, c);
	}

	/**
	 * @param keys - the keys
	 * @param values - the mapped values
	 * @param type - the type of map
	 * @param c - the comparator (if for sorted map)
	 * @return a map with the given key value pairs
	 */
	private static <K, V> Map<K, V> asMap(final K[] keys, final V[] values, final int type, final Comparator<? super K> c) {
		Map<K, V> aMap;

		switch (type) {
		case TYPE_LINKED:
			aMap = new LinkedHashMap<>();
			break;
		case TYPE_SORTED:
			aMap = c == null ? new TreeMap<K, V>() : new TreeMap<K, V>(c);
			break;
		default:
			aMap = new HashMap<>();
			break;
		}

		if (keys != null && values != null) {
			int count = Math.min(values.length, keys.length);
			for (int i = 0; i < count; i++) {
				aMap.put(keys[i], values[i]);
			}
		}
		return aMap;
	}

}
