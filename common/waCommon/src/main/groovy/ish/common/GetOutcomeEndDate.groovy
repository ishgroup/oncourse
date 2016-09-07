package ish.common

import groovy.time.TimeCategory
import ish.oncourse.cayenne.CourseClassInterface
import ish.oncourse.cayenne.OutcomeInterface
import ish.oncourse.cayenne.SessionInterface
import ish.oncourse.cayenne.SessionModuleInterface
import org.apache.cayenne.query.Ordering
import org.apache.cayenne.query.SortOrder

class GetOutcomeEndDate {

	private  OutcomeInterface outcome

	def GetOutcomeEndDate(OutcomeInterface outcome)  {
		this.outcome = outcome
	}
	
	def Date get() {
		if (outcome.endDate) {
			outcome.endDate
		} else {
			if (!outcome.enrolment) {
				return null
			}
			// this is a flexible delivery class so the start date varies for each enrolment
			// 'No sessions' means CourseClass.endDateTime is null
			CourseClassInterface courseClass = outcome.enrolment.courseClass;
			if (courseClass.isDistantLearningCourse || courseClass.sessions.empty) {
				use(TimeCategory) {
					return courseClass.maximumDays ? outcome.enrolment.createdOn + 365.day : outcome.enrolment.createdOn + courseClass.maximumDays.day	
				}
			}

			if (outcome.module) {
				List<SessionModuleInterface> sessionModules = outcome.enrolment.courseClass.sessions*.sessionModules
						.flatten()
						.findAll { sm -> sm.module == outcome.module }
						.sort { a,b -> -(a.session.startDatetime <=> b.session.startDatetime) }
				if (sessionModules.size()) {
					return sessionModules.first().session.endDatetime
				}
			}
		

			// if the module for the outcome isn't found in the sessions, return the class end date
			return outcome.enrolment.courseClass.endDateTime
		}
	}
}
