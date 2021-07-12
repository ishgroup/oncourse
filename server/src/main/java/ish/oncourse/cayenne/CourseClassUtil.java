/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.cayenne;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.server.cayenne.Module;
import ish.oncourse.server.cayenne.*;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import java.util.List;

/**
 */
public final class CourseClassUtil {

	private CourseClassUtil() {}

	/**
	 * @return the list of enrolments which are considered as valid, that is the one which have status success or which are queued.
	 */
	public static List<Enrolment> getSuccessAndQueuedEnrolments(List<Enrolment> theEnrolments) {
		if (theEnrolments == null || theEnrolments.size() == 0) {
			return theEnrolments;
		}
		Expression validEnrolmentExpr = ExpressionFactory.matchExp(Enrolment.STATUS_PROPERTY, null);
		for (EnrolmentStatus es : EnrolmentStatus.STATUSES_LEGIT) {
			validEnrolmentExpr = validEnrolmentExpr.orExp(ExpressionFactory.matchExp(Enrolment.STATUS_PROPERTY, es));
		}

		return validEnrolmentExpr.filterObjects(theEnrolments);
	}

	/**
	 * @return the list of enrolments which are considered as REFUNDED.
	 */
	public static List<Enrolment> getRefundedAndCancelledEnrolments(List<Enrolment> theEnrolments) {
		if (theEnrolments == null || theEnrolments.size() == 0) {
			return theEnrolments;
		}
		Expression validEnrolmentExpr = ExpressionFactory.matchExp(Enrolment.STATUS_PROPERTY, EnrolmentStatus.REFUNDED);
		validEnrolmentExpr = validEnrolmentExpr.orExp(ExpressionFactory.matchExp(Enrolment.STATUS_PROPERTY, EnrolmentStatus.CANCELLED));

		return validEnrolmentExpr.filterObjects(theEnrolments);
	}


	/**
	 * Adds module to all existing sessions of the class
	 */
	public static void addModuleToAllSessions(CourseClass courseClass, Module module) {
		for (Session session : courseClass.getSessions()) {
			SessionModule sessionModule =  courseClass.getContext().newObject(SessionModule.class);
			sessionModule.setSession(session);
			sessionModule.setModule(module);
		}
	}
}
