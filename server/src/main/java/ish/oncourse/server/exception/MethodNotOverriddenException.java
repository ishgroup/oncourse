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

package ish.oncourse.server.exception;

/**
 * Thrown when superclass method should be overriden by child.
 */
public class MethodNotOverriddenException extends Exception {
	/**
	 *
	 */


	public MethodNotOverriddenException() {
		super();
	}

	public MethodNotOverriddenException(final String message) {
		super(message);
	}

	public MethodNotOverriddenException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MethodNotOverriddenException(final Throwable cause) {
		super(cause);
	}

	public MethodNotOverriddenException(final String message, final Class<?> pclass) {
		super(message + pclass.getName());
	}
}
