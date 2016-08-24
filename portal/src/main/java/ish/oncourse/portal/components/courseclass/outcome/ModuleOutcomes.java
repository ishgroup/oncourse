/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.courseclass.outcome;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Module;
import ish.oncourse.model.Outcome;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.List;

import static ish.common.types.EnrolmentStatus.*;

public class ModuleOutcomes {

	@Parameter
	@Property
	private CourseClass courseClass;

	@Parameter
	@Property
	private Module module;

	@Property
	private List<Outcome> outcomes;

	@Property
	private Outcome outcome;

	@SetupRender
	public void setupRender() {
		outcomes = ObjectSelect.query(Outcome.class).where(Outcome.ENROLMENT.dot(Enrolment.COURSE_CLASS).eq(courseClass))
				.and(Outcome.ENROLMENT.dot(Enrolment.STATUS).in(SUCCESS, IN_TRANSACTION))
				.and(Outcome.MODULE.eq(module)).select(courseClass.getObjectContext());
		
	}
}
