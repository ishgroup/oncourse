package ish.common

import ish.oncourse.cayenne.CourseClassInterface
import ish.oncourse.cayenne.OutcomeInterface
import ish.oncourse.cayenne.SessionInterface
import ish.oncourse.cayenne.SessionModuleInterface
import org.apache.cayenne.query.Ordering
import org.apache.cayenne.query.SortOrder

class GetOutcomeStartDate {


	private  OutcomeInterface outcome

	def GetOutcomeStartDate(OutcomeInterface outcome)  {
		this.outcome = outcome
	}

	def Date get() {
		if (outcome.startDate) {
			outcome.startDate
		} else {
			if (!outcome.enrolment) {
				return null
			}

			// this is a flexible delivery class so the start date varies for each enrolment
			// 'No sessions' means CourseClass.startDateTime is null
			CourseClassInterface courseClass = outcome.enrolment.courseClass
			if (courseClass.isDistantLearningCourse || courseClass.sessions.empty) {
				return outcome.enrolment.createdOn;
			}

			if (outcome.module) {
				List<SessionModuleInterface> sessionModules = outcome.enrolment.courseClass.sessions*.sessionModules
						.flatten()
						.findAll { sm -> sm.module == outcome.module }
						.sort { a,b -> a.session.startDatetime <=> b.session.startDatetime }
				
				if (sessionModules.size()) {
					return sessionModules.first().session.startDatetime
				}
			}

			// if the module for the outcome isn't found in the sessions, return the class start date
			return outcome.enrolment.courseClass.startDateTime;
		}
	}
}
