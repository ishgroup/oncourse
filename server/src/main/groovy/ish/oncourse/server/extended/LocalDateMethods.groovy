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

package ish.oncourse.server.extended

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.UnsupportedTemporalTypeException

class LocalDateMethods {
	static String format(LocalDate self, String patterm) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(patterm)
		try {
			self.format(format)
		} catch (UnsupportedTemporalTypeException e) {
			self.atTime(12, 0).atZone(ZoneId.systemDefault()).format(format)
		}
	}
}
