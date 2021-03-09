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

package ish.common

import ish.oncourse.cayenne.CourseClassInterface
import ish.oncourse.cayenne.OutcomeInterface
import ish.oncourse.cayenne.SessionModuleInterface

import static ish.common.GetAssessmentDueDate.getAssessmentDueDates

class CalculateStartDate {

	private OutcomeInterface outcome
	private boolean attendanceTakenIntoAccount = false

	def CalculateStartDate(OutcomeInterface outcome, Boolean attendanceTakenIntoAccount)  {
		this.outcome = outcome
		this.attendanceTakenIntoAccount = attendanceTakenIntoAccount
	}

	def Date calculate() {

		if (!outcome.enrolment) {
			return null
		}

		// this is a flexible delivery class so the start date varies for each enrolment
		// 'No sessions' means CourseClass.startDateTime is null
		CourseClassInterface courseClass = outcome.enrolment.courseClass
		if (courseClass.isDistantLearningCourse || (courseClass.sessions.empty && courseClass.assessmentClasses.empty)) {
			return outcome.enrolment.createdOn;
		}

		if (outcome.module) {
			List<Date> sessionModuleStartDates = getSessionStartDates(courseClass)

			List<Date> assessmentModuleDueDates = getAssessmentDueDates(courseClass, outcome, attendanceTakenIntoAccount)

			if (!sessionModuleStartDates.isEmpty() || !assessmentModuleDueDates.isEmpty()) {
				return (sessionModuleStartDates + assessmentModuleDueDates).sort().first()
			}
		}

		// if the module for the outcome isn't found in the sessions, return the class start date
		return courseClass.startDateTime;
	}

	private List<Date> getSessionStartDates(CourseClassInterface courseClass) {
		List<SessionModuleInterface> sessionModules = (courseClass.sessions*.sessionModules
				.flatten() as List<SessionModuleInterface>)
				.findAll { sm -> sm.module == outcome.module }

		if (attendanceTakenIntoAccount) {
			sessionModules = sessionModules.findAll { !it.getAttendanceForOutcome(outcome)?.absent }
		}

		return sessionModules*.session*.startDatetime
	}
}
