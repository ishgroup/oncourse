package ish.oncourse.services.cache;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Value<V> {
	private V value;

	public V getValue() {
		return value;
	}

	public static <V> Value<V> valueOf(V value) {
		Value<V> result = new Value<>();
		result.value = value;
		return result;
	}
}
