/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.timetable;

import ish.oncourse.model.CourseClass;

import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Session;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.utils.DateUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.List;

public class YourClasses {

	@Property
	private List<CourseClass> classes;

	@Property
	private CourseClass courseClass;

	@Inject
	private ICayenneService cayenneService;

	@Parameter
	private Date month;


	@Parameter
	private Student student;

	@Parameter
	private Tutor tutor;

	private Date now = new Date();
		
	@SetupRender
	void setupRender() {
		
		if (tutor == null && student == null) {
			return;
		}
		
		ObjectSelect query = ObjectSelect.query(CourseClass.class).where(CourseClass.SESSIONS.dot(Session.START_DATE).between(DateUtils.startOfMonth(month), DateUtils.endOfMonth(month)));
		Expression contactExp = null; 
		
		if (tutor != null) {
			contactExp = CourseClass.CANCELLED.isFalse().andExp(CourseClass.TUTOR_ROLES.outer().dot(TutorRole.TUTOR).eq(tutor));
		}
		if (student!= null) {
			if (contactExp == null) {
				contactExp = CourseClass.ENROLMENTS.dot(Enrolment.STATUS).in(Enrolment.VALID_ENROLMENTS).andExp(CourseClass.ENROLMENTS.dot(Enrolment.STUDENT).eq(student));
			} else {
				contactExp = contactExp.orExp(CourseClass.ENROLMENTS.dot(Enrolment.STATUS).in(Enrolment.VALID_ENROLMENTS).andExp(CourseClass.ENROLMENTS.dot(Enrolment.STUDENT).eq(student)));
			}
		}
		classes = query.and(contactExp).select(cayenneService.sharedContext());
	}
	
	public boolean isFuture() {
		return courseClass.getStartDate().after(now);
	}

	public boolean isComplited() {
		return courseClass.getEndDate().before(now);

	}

	public boolean isInProgress() {
		return !isComplited() && !isFuture();
	}
	
	public String getStartDate() {
		return  FormatUtils.getDateFormat("EEE dd MMM h.mm a", courseClass.getTimeZone()).format(courseClass.getStartDate());
	}
}
