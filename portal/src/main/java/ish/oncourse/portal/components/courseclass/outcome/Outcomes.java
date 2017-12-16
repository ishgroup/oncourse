/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.courseclass.outcome;


import ish.oncourse.model.*;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static ish.common.types.EnrolmentStatus.IN_TRANSACTION;
import static ish.common.types.EnrolmentStatus.SUCCESS;
import static ish.common.types.OutcomeStatus.*;
import static ish.oncourse.portal.components.courseclass.outcome.OutcomeItem.*;

public class Outcomes {

	@Parameter
	@Property
	private CourseClass courseClass;
	
	@Parameter
	private boolean activeTab = false;
	
	@Property
	private CourseModule courseModule;

	@Property
	private Enrolment enrolment;

	@Property
	private Module module;

	@Inject
	private Block enrolmentOutcomes;

	@Inject
	private Block moduleOutcomes;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IPortalService portalService;


	@OnEvent(value = "getEnrolmentOutcomes")
	public Block getEnrolmentOutcomes(Long enrolmentId) throws IOException {
		enrolment = SelectById.query(Enrolment.class, enrolmentId).selectOne(cayenneService.newContext());
		return enrolmentOutcomes;
	}

	@OnEvent(value = "getModuleOutcomes")
	public Block getModuleOutcomes(Long moduleId) throws IOException {
		module = SelectById.query(Module.class, moduleId).selectOne(cayenneService.newContext());
		return moduleOutcomes;
	}

	@OnEvent(value="saveModule")
	public void saveModule(Long moduleId, Long classId, String status, boolean setEndDate) {
		ObjectContext context = cayenneService.newContext();
		CourseClass courseClass = SelectById.query(CourseClass.class, classId).selectOne(context);
		Module module = SelectById.query(Module.class, moduleId).selectOne(context);
		List<Outcome> outcomes = ObjectSelect.query(Outcome.class).where(Outcome.ENROLMENT.dot(Enrolment.COURSE_CLASS).eq(courseClass))
				.and(Outcome.ENROLMENT.dot(Enrolment.STATUS).in(SUCCESS, IN_TRANSACTION))
				.and(Outcome.MODULE.eq(module)).select(context);

		for (Outcome outcome : outcomes) {
			saveOutcome(outcome, status, setEndDate);
		}
		context.commitChanges();
	}

	@OnEvent(value="saveEnrolment")
	public void saveEnrolment(Long enrolmentId, String status, boolean setEndDate) {
		ObjectContext context = cayenneService.newContext();
		Enrolment enrolment = SelectById.query(Enrolment.class, enrolmentId).selectOne(context);
		for (Outcome outcome : enrolment.getOutcomes()) {
			saveOutcome(outcome, status, setEndDate);
		}
		context.commitChanges();

	}

	@OnEvent(value="saveOutcome")
	public void saveOutcome(Long outcomeId, String status, boolean setEndDate) {
		ObjectContext context = cayenneService.newContext();
		Outcome outcome = SelectById.query(Outcome.class, outcomeId).selectOne(context);
		saveOutcome(outcome, status, setEndDate);
		context.commitChanges();
	}
	
	private void saveOutcome(Outcome outcome, String status, boolean setEndDate) {
//		if (OutcomeUtils.isEditingAllowed(outcome)) {
//			return;
//		}
		outcome.setMarkedByTutor(outcome.getObjectContext().localObject(portalService.getContact().getTutor()));
		outcome.setMarkedByTutorDate(new Date());
		if (setEndDate) {
			Date today = DateUtils.setHours(DateUtils.truncate(new Date(), Calendar.HOUR), 12);
			outcome.setEndDate(today);
		}
		boolean vet = outcome.getModule() != null;
		switch (status.replaceAll("-"," ")) {
			case COMPETENT_LABEL:
				outcome.setStatus(vet ? STATUS_ASSESSABLE_PASS : STATUS_NON_ASSESSABLE_COMPLETED);
				break;
			case NOT_YET_COMPETENT_LABEL:
				outcome.setStatus(vet ? STATUS_ASSESSABLE_FAIL : STATUS_NON_ASSESSABLE_NOT_COMPLETED);
				break;
			case WITHDRAWN_LABEL:
				outcome.setStatus(STATUS_ASSESSABLE_WITHDRAWN);
				break;
			case NO_RESULT_LABEL:
				outcome.setStatus(STATUS_NOT_SET);
				break;
			default:
				throw new IllegalArgumentException(String.format("Unsupported status: %s", status));
		}
	}

	public String getActiveClass() {
		return activeTab ? "active" : StringUtils.EMPTY;
	}

	public String getStydentsContentClass() {
		return courseClass.getCourse().getCourseModules().isEmpty() ?  "active" : StringUtils.EMPTY;
	}
}
