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

package ish.print;

/**
 * interface for all objects which can be printed with onCourse
 *
 * @author marcin
 */
public interface PrintableObject {

	/**
	 * returns value for a given Strign key
	 *
	 * @param key
	 * @return value
	 */
	public Object getValueForKey(String key);

	/**
	 * @return short string description of object, used mostly for debugging and fault reporting
	 */
	public String getShortRecordDescription();
}
