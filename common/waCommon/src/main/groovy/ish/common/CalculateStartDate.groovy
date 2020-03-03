package ish.common

import ish.oncourse.cayenne.AssessmentClassModuleInterface
import ish.oncourse.cayenne.CourseClassInterface
import ish.oncourse.cayenne.OutcomeInterface
import ish.oncourse.cayenne.SessionModuleInterface

class CalculateStartDate {
	
	private  OutcomeInterface outcome

	def CalculateStartDate(OutcomeInterface outcome)  {
		this.outcome = outcome
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
			List<SessionModuleInterface> sessionModules = courseClass.sessions*.sessionModules
					.flatten()
					.findAll { sm -> sm.module == outcome.module } as List<SessionModuleInterface>

			List<AssessmentClassModuleInterface> assessmentClassModules = courseClass.assessmentClasses*.assessmentClassModules
					.flatten()
					.findAll { acm -> acm.module == outcome.module } as List<AssessmentClassModuleInterface>

			List<SessionModuleInterface> attendedSessionModules = filterAttendedModulesOnly(sessionModules, outcome)

			if (!sessionModules.isEmpty() || !assessmentClassModules.isEmpty()) {
				return (attendedSessionModules*.session*.startDatetime + assessmentClassModules*.assessmentClass*.dueDate).sort().first()
			}
		}

		// if the module for the outcome isn't found in the sessions, return the class start date
		return courseClass.startDateTime;
	}

	private List<SessionModuleInterface> filterAttendedModulesOnly(List<SessionModuleInterface> sessionModuleList, OutcomeInterface outcome) {
		sessionModuleList.findAll { !it.getAttendanceForOutcome(outcome).absent }
	}
}
