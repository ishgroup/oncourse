/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne;

import ish.common.payable.EnrolmentInterface;
import java.util.Date;


public interface OutcomeInterface {

	ModuleInterface getModule();

	EnrolmentInterface getEnrolment();

	Date getStartDate();
	Date getEndDate();

}
