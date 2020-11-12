/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.util;

import ish.oncourse.cayenne.PersistentObjectI;

import java.util.Map;

/**
 * Contains utility methods to deal with general operations of Maps
 */
public final class MapsUtil {

	private MapsUtil(){}


	/**
	 * A convenience method to allow to get a key mapped to a value in a Map. <br/>
	 * Its not safe to use if there are multiple keys with the same value, it will return the first one.
	 *
	 * @param value
	 * @param map
	 * @return the related key
	 */
	public static Object getKeyForValue(Object value, Map<?, ?> map) {
		if (map != null) {
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				if (value == null) {
					if (entry.getValue() == null) {
						return entry.getKey();
					}
				} else if (entry.getValue() != null) {
					if (value instanceof PersistentObjectI && entry.getValue() instanceof PersistentObjectI) {
						PersistentObjectI o1;
						PersistentObjectI o2;

						o1 = (PersistentObjectI) value;
						o2 = (PersistentObjectI) entry.getValue();
						if (o1.getObjectId().equals(o2.getObjectId())) {
							return entry.getKey();
						}
					} else if (value.equals(entry.getValue())) {
						return entry.getKey();
					}
				}
			}
		}
		return null;
	}
}
