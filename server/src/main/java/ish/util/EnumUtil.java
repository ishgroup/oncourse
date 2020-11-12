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

import ish.common.util.DisplayableExtendedEnumeration;
import org.apache.cayenne.ExtendedEnumeration;

import java.util.Arrays;

/**
 * Util class for operations on @see org.apache.cayenne.ExtendedEnumeration
 *
 */
public class EnumUtil {

	public static ExtendedEnumeration enumForDatabaseValue(Class<? extends ExtendedEnumeration> enumClass, Object value) {
		ExtendedEnumeration[] values = enumClass.getEnumConstants();

		for (ExtendedEnumeration type : values) {
			if (type.getDatabaseValue().equals(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Enumeration key doesn't exist for value:" + value + ", " + enumClass);
	}

	public static <T> T toDTOEnum(DisplayableExtendedEnumeration item, Class<T> enumm ) {
		return Arrays.stream(enumm.getEnumConstants())
				.filter( e -> item.getDisplayName().equals(e.toString()))
				.findFirst().get();

	}
}
