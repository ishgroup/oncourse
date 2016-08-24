/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.courseclass.outcome;

import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Outcome;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class EnrolmentOutcomes {

	@Parameter
	@Property
	private Enrolment enrolment;

	@Property
	private Outcome outcome;

	@Property
	private boolean vet;


	@SetupRender
	public void setupRender() {
		vet = enrolment.getCourseClass().getCourse().getIsVETCourse();
	}
}
