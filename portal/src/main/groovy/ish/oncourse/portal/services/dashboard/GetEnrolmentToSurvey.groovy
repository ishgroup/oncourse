package ish.oncourse.portal.services.dashboard

import ish.common.types.EnrolmentStatus
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Enrolment
import ish.oncourse.model.Student
import ish.oncourse.model.Survey
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.portal.services.dashboard.CalculateAttendancePercent.DASHBOARD_CACHE

class GetEnrolmentToSurvey {

    private Student student
    private Enrolment enrolment

    GetEnrolmentToSurvey(Student student) {
		this.student = student
	}

    Enrolment get() {
		if (!student) {
			return null
		} else if (!enrolment) {
			enrolment = ObjectSelect.query(Enrolment).where(Enrolment.STUDENT.eq(student))
					.and(Enrolment.STATUS.eq(EnrolmentStatus.SUCCESS))
					.and(Enrolment.COURSE_CLASS.dot(CourseClass.CANCELLED).isFalse())
					.and(Enrolment.COURSE_CLASS.dot(CourseClass.END_DATE).isNotNull())
					.and(Enrolment.COURSE_CLASS.dot(CourseClass.END_DATE).lte(new Date()))
					.and(Enrolment.SURVEYS.outer().dot(Survey.CREATED).isNull())
					.orderBy(Enrolment.COURSE_CLASS.dot(CourseClass.END_DATE).asc())
					.prefetch(Enrolment.COURSE_CLASS.disjoint())
					.prefetch(Enrolment.COURSE_CLASS.dot(CourseClass.COURSE).disjoint())
                    .cacheStrategy(DASHBOARD_CACHE, DASHBOARD_CACHE)
					.selectFirst(student.objectContext)
		}
		return enrolment
	}
	
}
