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

package ish.common.payable;

import ish.oncourse.cayenne.CourseClassInterface;
import java.util.Date;

/**
 * Common interface which used for recalculation of amount owing only for enrollments in final statuses.
 */
public interface EnrolmentInterface {

	public boolean isInFinalStatus();

	CourseClassInterface getCourseClass();

	Date getCreatedOn();

}
