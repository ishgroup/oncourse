/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.dashboard;

import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Session;
import ish.oncourse.portal.services.attendance.AttendanceUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.util.TimeZone;

public class MarkRoll {

	@Parameter
	@Property
	private Session session;

	public String getSessionStartDate() {
		return AttendanceUtils.getStartDate(TimeZone.getTimeZone(session.getTimeZone()), session, "EEEE, d MMMM yyyy");
	}

	public String getSessionTime() {
		return AttendanceUtils.getSessionTime(TimeZone.getTimeZone(session.getTimeZone()), session);
	}
}
