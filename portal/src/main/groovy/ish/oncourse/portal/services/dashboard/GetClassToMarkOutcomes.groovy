package ish.oncourse.portal.services.dashboard

import ish.common.types.OutcomeStatus
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Enrolment
import ish.oncourse.model.Outcome
import ish.oncourse.model.Tutor
import ish.oncourse.model.TutorRole
import org.apache.cayenne.query.ObjectSelect

import static ish.common.types.OutcomeStatus.STATUS_NOT_SET

class GetClassToMarkOutcomes {
	
	def Tutor tutor
	def CourseClass courseClass
	
	def GetClassToMarkOutcomes(Tutor tutor) {
		this.tutor = tutor
	}
	
	def CourseClass get() {
		if (!tutor) {
			return null
		} else if (!courseClass) {
			courseClass = ObjectSelect.query(CourseClass).where(CourseClass.TUTOR_ROLES.outer().dot(TutorRole.TUTOR).eq(tutor))
					.and(CourseClass.END_DATE.lte(new Date()))
					.and(CourseClass.ENROLMENTS.outer().dot(Enrolment.OUTCOMES).outer().dot(Outcome.STATUS).eq(STATUS_NOT_SET))
					.orderBy(CourseClass.END_DATE.desc())
					.selectFirst(tutor.objectContext)
					
		}
		return courseClass
	}
}
