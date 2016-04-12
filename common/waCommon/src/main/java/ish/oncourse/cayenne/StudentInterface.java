/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne;

import java.util.List;

public interface StudentInterface {

	String CONTACT_KEY = "contact";
	String YEAR_SCHOOL_COMPLETED_PROPERTY = "yearSchoolCompleted";

	ContactInterface getContact();

	List<StudentConcessionInterface> getConcessions();

	Integer getYearSchoolCompleted();

}
