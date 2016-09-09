package ish.oncourse.portal.services.dashboard

import ish.oncourse.model.Application
import ish.oncourse.model.Student
import org.apache.cayenne.query.ObjectSelect

import static ish.common.types.ApplicationStatus.OFFERED
import static ish.oncourse.portal.services.dashboard.CalculateAttendancePercent.DASHBOARD_CACHE
import static org.apache.cayenne.query.QueryCacheStrategy.LOCAL_CACHE

class GetApplicationToStudy {
	
	def Student student
	def Application application


	def GetApplicationToStudy(Student student) {
		this.student = student
	}
	
	def Application get() {
		if (!student) {
			return null
		} else if (!application) {
			application = ObjectSelect.query(Application)
					.where(Application.STUDENT.eq(student))
					.and(Application.ENROL_BY.isNull().orExp(Application.ENROL_BY.gte(new Date())))
					.and(Application.STATUS.eq(OFFERED))
					.orderBy(Application.MODIFIED.desc())
					.cacheStrategy(LOCAL_CACHE, DASHBOARD_CACHE)
					.selectFirst(student.objectContext)
		}
		return application
	}
}
