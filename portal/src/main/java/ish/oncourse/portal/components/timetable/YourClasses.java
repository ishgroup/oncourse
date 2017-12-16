/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.timetable;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;
import ish.oncourse.portal.services.timetable.GetContactClassesObjectSelect;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.util.FormatUtils;
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
		
		classes = new GetContactClassesObjectSelect(month, student, tutor).get().select(cayenneService.newContext());
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
