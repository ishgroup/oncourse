package ish.oncourse.portal.services.dashboard

import ish.oncourse.model.Application
import ish.oncourse.model.Student
import org.apache.cayenne.query.ObjectSelect

import static ish.common.types.ApplicationStatus.OFFERED

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
					.and(Application.STATUS.eq(OFFERED))
					.orderBy(Application.MODIFIED.desc())
					.selectFirst(student.objectContext)
		}
		return application
	}
}
