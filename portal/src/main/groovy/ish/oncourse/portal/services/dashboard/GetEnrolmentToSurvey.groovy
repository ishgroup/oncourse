package ish.oncourse.portal.services.dashboard

import ish.oncourse.model.CourseClass
import ish.oncourse.model.Enrolment
import ish.oncourse.model.Student
import ish.oncourse.model.Survey
import org.apache.cayenne.query.ObjectSelect

class GetEnrolmentToSurvey {

	def Student student
	def Enrolment enrolment

	def GetEnrolmentToSurvey(Student student) {
		this.student = student
	}
	
	def Enrolment get() {
		if (!student) {
			return null
		} else if (!enrolment) {
			enrolment = ObjectSelect.query(Enrolment).where(Enrolment.STUDENT.eq(student))
					.and(Enrolment.COURSE_CLASS.dot(CourseClass.END_DATE).isNotNull())
					.and(Enrolment.COURSE_CLASS.dot(CourseClass.END_DATE).lte(new Date()))
					.and(Enrolment.SURVEYS.outer().dot(Survey.CREATED).isNull())
					.orderBy(Enrolment.COURSE_CLASS.dot(CourseClass.END_DATE).asc())
					.selectFirst(student.getObjectContext())
		}
		return enrolment
	}
	
}
