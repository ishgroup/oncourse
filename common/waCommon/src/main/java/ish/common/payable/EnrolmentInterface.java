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
