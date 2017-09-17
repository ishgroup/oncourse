package ish.oncourse.portal.services.dashboard

import groovy.time.TimeCategory
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Session
import ish.oncourse.model.SessionTutor
import ish.oncourse.model.Tutor
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.portal.services.dashboard.CalculateAttendancePercent.DASHBOARD_CACHE
import static org.apache.cayenne.query.QueryCacheStrategy.SHARED_CACHE

class GetSessionToMarkRoll {
	
	def Tutor tutor
	def Session session
	
	def GetSessionToMarkRoll(Tutor tutor) {
		this.tutor = tutor
	}
	
	def Session get() {
		if (!tutor) {
			return null
		} else if (!session) {
			use(TimeCategory) {
				Date untilDate = new Date() + 1.hour
				session = ObjectSelect.query(Session).where(Session.SESSION_TUTORS.outer().dot(SessionTutor.TUTOR).eq(tutor))
						.and(Session.COURSE_CLASS.dot(CourseClass.CANCELLED).isFalse())
						.and(Session.START_DATE.lte(untilDate))
						.orderBy(Session.START_DATE.desc())
                        .cacheStrategy(SHARED_CACHE, DASHBOARD_CACHE)
						.selectFirst(tutor.getObjectContext())
			}
		}
		return session
	}
	
}
