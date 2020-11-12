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
package ish.common.util;

import org.apache.cayenne.ExtendedEnumeration;

/**
 * An extension of @see(org.apache.cayenne.ExtendedEnumeration) interface that binds the database value to a display value
 */
public interface DisplayableExtendedEnumeration<E extends Object> extends ExtendedEnumeration {

	/**
	 * @return name of the enumeration as it is to be displayed to the user (as opposed to the value stored in the database)
	 */
	public String getDisplayName();

	/**
	 * return the value of the enumeration as it is stored in the database
	 *
	 * @see org.apache.cayenne.ExtendedEnumeration#getDatabaseValue()
	 */
	public E getDatabaseValue();

}
