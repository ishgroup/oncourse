/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.Session;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.portal.services.attendance.AttendanceUtils;
import ish.oncourse.portal.services.attendance.SessionUtils;
import ish.oncourse.services.IReachtextConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class SessionStudentDetails {

	private TimeZone timeZone;

	@Inject
	private IReachtextConverter textileConverter;

	@Property
	@Parameter
	private Session session;

	@Property
	private List<Tutor> tutors;

	@Property
	private Tutor tutor;

	@SetupRender
	boolean setupRender() {
		timeZone = session.getCourseClass().getClassTimeZone();

		tutors = new ArrayList<>();
		List<TutorRole> tutorRoles = session.getCourseClass().getTutorRoles();
		for (TutorRole tutorRole : tutorRoles) {
			tutors.add(tutorRole.getTutor());
		}


		return true;
	}

	public String getVenue() {
		return SessionUtils.getVenue(session);
	}

	public String convertTextile(String note) {
		String detail = textileConverter.convertCustomText(note, new ValidationErrors());
		return detail == null ? StringUtils.EMPTY : detail;
	}

	public String getSessionDate() {
		return AttendanceUtils.getSessionDateTime(timeZone, session);
	}
}
