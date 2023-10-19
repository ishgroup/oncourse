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
package ish.oncourse.cayenne;

import ish.common.types.CourseClassType;

import java.util.List;
import java.util.Date;

public interface CourseClassInterface {

	Boolean getIsDistantLearningCourse();

	CourseClassType getType();

	List<? extends SessionInterface> getSessions();

	Integer getMaximumDays();

	Date getStartDateTime();

	Date getEndDateTime();

	List<? extends AssessmentClassInterface> getAssessmentClasses();

}
