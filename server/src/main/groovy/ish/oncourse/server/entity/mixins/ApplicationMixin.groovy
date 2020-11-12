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

package ish.oncourse.server.entity.mixins

import ish.common.types.ApplicationStatus
import ish.oncourse.server.cayenne.Application

class ApplicationMixin {

	private static final String EXPIRED_STATUS = "Expired"
	public static final String DISPLAY_STATUS = "displayStatus"

	static String getDisplayStatus(Application self) {
		def status = self.getValueForKey("status")
		if (((status.equals(ApplicationStatus.NEW))
				|| (status.equals(ApplicationStatus.IN_PROGRESS))
				|| (status.equals(ApplicationStatus.OFFERED)))
		&& (self.enrolBy != null) && (self.enrolBy.before(new Date()))) {
			return EXPIRED_STATUS
		}
		return status.toString()
	}

}
