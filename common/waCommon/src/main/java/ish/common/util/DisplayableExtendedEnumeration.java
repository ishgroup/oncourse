/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
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
