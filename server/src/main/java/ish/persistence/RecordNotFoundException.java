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

package ish.persistence;

import org.apache.cayenne.exp.Expression;

/**
 * Exception used to indicate that the record looked up does not exist, or that there are multiple instances matching the criteria
 *
 */
public class RecordNotFoundException extends RuntimeException {

	public RecordNotFoundException(String message) {
		super(message);
	}

	public RecordNotFoundException(Class cclass, Expression expression, int foundObjects) {
		this("For expression "+expression.toString()+" found " +foundObjects+ " objects of class "+cclass+", was expecting 1");
	}
}
