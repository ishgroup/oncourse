/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.application;

import ish.common.types.ApplicationStatus;
import ish.oncourse.model.Application;
import ish.oncourse.model.Course;
import ish.oncourse.model.Student;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.List;

public class ApplicationServiceImpl implements IApplicationService {

	@Inject
	private ICayenneService cayenneService;
	
	/**
	 * @param course
	 * @param student
	 * @return offered application for course-student if exist, else return null
	 */
	@Override
	public Application findOfferedApplicationBy(Course course, Student student) {
		List<Application> applications = findeApplications(course, student, ApplicationStatus.OFFERED);

		//find the lowest applicable fee
		Ordering ordering = new Ordering();
		ordering.setSortSpecString(Application.FEE_OVERRIDE_PROPERTY);
		ordering.setNullSortedFirst(false);
		ordering.setAscending();

		ordering.orderList(applications);

		//exclude expired applications
		Expression expression = ExpressionFactory.greaterExp(Application.ENROL_BY_PROPERTY, new Date()).orExp(ExpressionFactory.matchExp(Application.ENROL_BY_PROPERTY, null));
		applications = expression.filterObjects(applications);
		
		return applications.size() > 0 ? applications.get(0) : null;
	}
	
	/**
	 * @param course
	 * @param student
	 * @return new application for course-student if exist, else return null
	 */
	@Override
	public Application findNewApplicationBy(Course course, Student student) {
		List<Application> applications = findeApplications(course, student, ApplicationStatus.NEW);
		return applications.size() > 0 ? applications.get(0) : null;	
	}
	
	private List<Application> findeApplications(Course course, Student student, ApplicationStatus status) {
		
		SelectQuery q = new SelectQuery(Application.class);
		q.andQualifier(ExpressionFactory.matchExp(Application.COURSE_PROPERTY, course));
		q.andQualifier(ExpressionFactory.matchExp(Application.STUDENT_PROPERTY, student));
		q.andQualifier(ExpressionFactory.matchExp(Application.STATUS_PROPERTY, status));
		
		return cayenneService.sharedContext().performQuery(q);
	}


}
