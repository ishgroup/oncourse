/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne;

import java.util.List;
import java.util.Date;
import java.util.TimeZone;

public interface CourseClassInterface {

	Boolean getIsDistantLearningCourse();

	List<? extends SessionInterface> getSessions();

	Integer getMaximumDays();

	Date getStartDateTime();

	Date getEndDateTime();

	CourseInterface getCourse();

	String getCode();

	TimeZone getTimeZone();

}
