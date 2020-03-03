package ish.common

import groovy.time.TimeCategory
import ish.oncourse.cayenne.AssessmentClassModuleInterface
import ish.oncourse.cayenne.CourseClassInterface
import ish.oncourse.cayenne.OutcomeInterface
import ish.oncourse.cayenne.SessionModuleInterface

class CalculateEndDate {

	private  OutcomeInterface outcome

	def CalculateEndDate(OutcomeInterface outcome)  {
		this.outcome = outcome
	}
	
	def Date calculate() {
		if (!outcome.enrolment) {
			return null
		}
		// this is a flexible delivery class so the start date varies for each enrolment
		// 'No sessions' means CourseClass.endDateTime is null
		CourseClassInterface courseClass = outcome.enrolment.courseClass;
		if (courseClass.isDistantLearningCourse || (courseClass.sessions.empty && courseClass.assessmentClasses.empty)) {
			Date endDate
			use(TimeCategory) {
				endDate = courseClass.maximumDays ? outcome.enrolment.createdOn + courseClass.maximumDays.day : outcome.enrolment.createdOn + 365.day 
			}
			return endDate
		}

		if (outcome.module) {
			List<SessionModuleInterface> sessionModules = courseClass.sessions*.sessionModules
					.flatten()
					.findAll { sm -> sm.module == outcome.module } as List<SessionModuleInterface>
			
			List<AssessmentClassModuleInterface> assessmentClassModules = courseClass.assessmentClasses*.assessmentClassModules
					.flatten()
					.findAll { acm -> acm.module == outcome.module } as List<AssessmentClassModuleInterface>

			List<SessionModuleInterface> attendedSessionModules = filterAttendedModulesOnly(sessionModules, outcome)
			
			if (!attendedSessionModules.isEmpty() || !assessmentClassModules.isEmpty()) {
				return (attendedSessionModules*.session*.endDatetime + assessmentClassModules*.assessmentClass*.dueDate).sort().last()
			}
		}
		// if the module for the outcome isn't found in the sessions, return the class end date
		return courseClass.endDateTime
	}

	private List<SessionModuleInterface> filterAttendedModulesOnly(List<SessionModuleInterface> sessionModule, OutcomeInterface outcome) {
		sessionModule.findAll { !it.getAttendanceForOutcome(outcome).absent }
	}
}
