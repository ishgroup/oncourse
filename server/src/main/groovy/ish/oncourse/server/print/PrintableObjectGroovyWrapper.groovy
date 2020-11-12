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

package ish.oncourse.server.print

import ish.print.PrintableObject

/**
 * Wrapper around {@link PrintableObject} classes routing groovy properties getters
 * to {@link PrintableObject#getValueForKey(String)}.
 */
class PrintableObjectGroovyWrapper {

	PrintableObject record

    PrintableObjectGroovyWrapper(PrintableObject record) {
		this.record = record
	}

	def getProperty(String name) {
		def propertyValue = record.getValueForKey(name)

		if (propertyValue == null) {
			return null
		}

		return propertyValue instanceof PrintableObject ?
				new PrintableObjectGroovyWrapper(propertyValue) : propertyValue
	}

	def invokeMethod(String name, Object args) {
		record.invokeMethod(name, args)
	}
}
