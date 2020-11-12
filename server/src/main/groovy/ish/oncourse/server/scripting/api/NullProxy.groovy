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

package ish.oncourse.server.scripting.api
/**
 * Wrapper around null value, which purpose is to allow addition of meta properties to it.
 */
class NullProxy extends  groovy.util.Proxy {

	static groovy.util.Proxy getInstance() {
		return new NullProxy().wrap(null)
	}

    boolean equals(Object to) {
		return to == null
    }

    boolean is(Object other) {
		return other == null
    }

    boolean asBoolean() {
		return false
	}

    Object asType(Class c) {
		return null
    }

    String toString() {
		return "null"
    }

    int hashCode() {
		throw new NullPointerException("Cannot invoke method hashCode() on null object")
    }
}
