/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne;

public interface SessionModuleInterface {

	ModuleInterface getModule();

	SessionInterface getSession();

	AttendanceInterface getAttendanceForOutcome(OutcomeInterface outcome);
}
