package ish.oncourse.portal.services.dashboard

import groovy.time.TimeCategory
import ish.oncourse.model.Session
import ish.oncourse.model.SessionTutor
import ish.oncourse.model.Tutor
import org.apache.cayenne.query.ObjectSelect

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
						.and(Session.START_DATE.lte(untilDate))
						.orderBy(Session.START_DATE.desc())
						.selectFirst(tutor.getObjectContext())
			}
		}
		return session
	}
	
}
