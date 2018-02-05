package ish.oncourse.services.application

import ish.common.types.ApplicationStatus
import ish.oncourse.model.Application
import ish.oncourse.model.Course
import ish.oncourse.model.Student
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

abstract class FindApplication {

    Course course
    Student student
    ObjectContext context
    QueryCacheStrategy cacheStrategy

    FindApplication(Course course, Student student, ObjectContext context) {
        this(course, student, context, null)
    }

    FindApplication(Course course, Student student, ObjectContext context, QueryCacheStrategy cacheStrategy) {
        this.course = course
        this.student = student
        this.context = context
        this.cacheStrategy = cacheStrategy
    }
    
    abstract Application get()

    Application get(ApplicationStatus status) {

        ObjectSelect<Application> select = (ObjectSelect.query(Application).where(Application.COURSE.eq(course))
                & Application.STUDENT.eq(student)
                & Application.STATUS.eq(status)
                & Application.ENROL_BY.gte(getLastHourQuarter()).orExp(Application.ENROL_BY.isNull()))
                .orderBy(Application.FEE_OVERRIDE.asc().identity { self ->
            self.nullSortedFirst = false
            self
        })
        if (cacheStrategy) {
            select = select.cacheStrategy(cacheStrategy).cacheGroup(Application.simpleName)
        }
        
        select.selectFirst(context)

    }
    
    private static Date getLastHourQuarter() {
        Date now = new Date()
        Calendar calendar = Calendar.getInstance()
        calendar.time = now
        calendar.add(Calendar.MINUTE, -(calendar.get(Calendar.MINUTE) % 15))
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}
