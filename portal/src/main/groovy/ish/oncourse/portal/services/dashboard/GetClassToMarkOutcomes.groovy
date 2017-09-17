package ish.oncourse.portal.services.dashboard

import ish.oncourse.model.*
import ish.oncourse.services.preference.PreferenceController
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy

import static ish.common.types.OutcomeStatus.STATUS_NOT_SET
import static ish.oncourse.portal.services.dashboard.CalculateAttendancePercent.DASHBOARD_CACHE

class GetClassToMarkOutcomes {

    private Tutor tutor
    private CourseClass courseClass
    private PreferenceController preferenceController

    GetClassToMarkOutcomes(Tutor tutor, PreferenceController preferenceController) {
		this.tutor = tutor
		this.preferenceController = preferenceController
	}

    CourseClass get() {
		if (!tutor || !preferenceController.outcomeMarkingViaPortal) {
			return null
		} else if (!courseClass) {
			courseClass = ObjectSelect.query(CourseClass).where(CourseClass.TUTOR_ROLES.outer().dot(TutorRole.TUTOR).eq(tutor))
					.and(CourseClass.CANCELLED.isFalse())
					.and(CourseClass.END_DATE.lte(new Date()))
					.and(CourseClass.ENROLMENTS.outer().dot(Enrolment.OUTCOMES).outer().dot(Outcome.STATUS).eq(STATUS_NOT_SET))
					.orderBy(CourseClass.END_DATE.desc())
                    .cacheStrategy(QueryCacheStrategy.SHARED_CACHE, DASHBOARD_CACHE)
					.selectFirst(tutor.objectContext)
					
		}
		return courseClass
	}
}
