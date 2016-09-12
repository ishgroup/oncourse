package ish.oncourse.portal.services.dashboard

import ish.oncourse.model.CourseClass
import ish.oncourse.model.Enrolment
import ish.oncourse.model.Outcome
import ish.oncourse.model.Tutor
import ish.oncourse.model.TutorRole
import ish.oncourse.services.preference.PreferenceController
import org.apache.cayenne.query.ObjectSelect

import static ish.common.types.OutcomeStatus.STATUS_NOT_SET
import static ish.oncourse.portal.services.dashboard.CalculateAttendancePercent.DASHBOARD_CACHE
import static org.apache.cayenne.query.QueryCacheStrategy.LOCAL_CACHE

class GetClassToMarkOutcomes {
	
	def Tutor tutor
	def CourseClass courseClass
	def PreferenceController preferenceController
	
	def GetClassToMarkOutcomes(Tutor tutor, PreferenceController preferenceController) {
		this.tutor = tutor
		this.preferenceController = preferenceController
	}
	
	def CourseClass get() {
		if (!tutor || !preferenceController.outcomeMarkingViaPortal) {
			return null
		} else if (!courseClass) {
			courseClass = ObjectSelect.query(CourseClass).where(CourseClass.TUTOR_ROLES.outer().dot(TutorRole.TUTOR).eq(tutor))
					.and(CourseClass.END_DATE.lte(new Date()))
					.and(CourseClass.ENROLMENTS.outer().dot(Enrolment.OUTCOMES).outer().dot(Outcome.STATUS).eq(STATUS_NOT_SET))
					.orderBy(CourseClass.END_DATE.desc())
					.cacheStrategy(LOCAL_CACHE, DASHBOARD_CACHE)
					.selectFirst(tutor.objectContext)
					
		}
		return courseClass
	}
}
