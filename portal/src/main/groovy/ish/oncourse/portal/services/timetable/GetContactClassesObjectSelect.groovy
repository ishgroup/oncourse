package ish.oncourse.portal.services.timetable

import ish.common.types.EnrolmentStatus
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Enrolment
import ish.oncourse.model.Session
import ish.oncourse.model.Student
import ish.oncourse.model.Tutor
import ish.oncourse.model.TutorRole
import ish.oncourse.utils.DateUtils
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.portal.services.timetable.ContactType.*


class GetContactClassesObjectSelect {

	def Date dateFrom
	def Student student
	def Tutor tutor

	
	def GetContactClassesObjectSelect(Date dateFrom, Student student, Tutor tutor) {
		this.dateFrom = dateFrom
		this.student = student
		this.tutor = tutor
	}
	
	def ObjectSelect<CourseClass> get() {
		
		ObjectSelect query = ObjectSelect.query(CourseClass).where(CourseClass.SESSIONS.dot(Session.START_DATE).gte(DateUtils.startOfMonth(dateFrom)))

		Expression tutorCondition = CourseClass.CANCELLED.isFalse().andExp(CourseClass.TUTOR_ROLES.outer().dot(TutorRole.TUTOR).eq(tutor))
		Expression studentCondition = CourseClass.ENROLMENTS.dot(Enrolment.STATUS).eq(EnrolmentStatus.SUCCESS).andExp(CourseClass.ENROLMENTS.dot(Enrolment.STUDENT).eq(student))
		
		switch (valueOf(student, tutor)) {
			case STUDENT_TUTOR:
				return query.and(tutorCondition.orExp(studentCondition))
			case TUTOR:
				return query.and(tutorCondition)
			case STUDENT:
				return query.and(studentCondition)
			case NOBODY:
			default: throw new IllegalArgumentException();
				
		}
	}
	
}

enum ContactType {
	STUDENT,
	TUTOR,
	STUDENT_TUTOR,
	NOBODY
	
	def static ContactType valueOf(Student student, Tutor tutor) {
		tutor && student ? STUDENT_TUTOR : tutor ? TUTOR : student ? STUDENT : NOBODY
	}
}
