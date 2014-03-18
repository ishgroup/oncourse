/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
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
