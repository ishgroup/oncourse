package ish.oncourse.services.application

import ish.common.types.ApplicationStatus
import ish.oncourse.model.Application
import ish.oncourse.model.Course
import ish.oncourse.model.Student
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

abstract class FindApplication {

    Course course
    Student student
    ObjectContext context

    FindApplication(Course course, Student student, ObjectContext context) {
        this.course = course
        this.student = student
        this.context = context
    }
    
    abstract Application get()

    Application get(ApplicationStatus status) {
        (ObjectSelect.query(Application).where(Application.COURSE.eq(course))
                & Application.STUDENT.eq(student)
                & Application.STATUS.eq(status)
                & Application.ENROL_BY.gte(new Date()).orExp(Application.ENROL_BY.isNull()))
                .orderBy(Application.FEE_OVERRIDE.asc().identity { self ->
            self.nullSortedFirst = false
            self
        }).selectFirst(context)
    }
}
