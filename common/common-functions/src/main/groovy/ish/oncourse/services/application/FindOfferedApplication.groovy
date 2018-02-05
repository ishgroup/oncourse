package ish.oncourse.services.application

import ish.common.types.ApplicationStatus
import ish.oncourse.model.Application
import ish.oncourse.model.Course
import ish.oncourse.model.Student
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy

import static ish.common.types.CourseEnrolmentType.ENROLMENT_BY_APPLICATION

class FindOfferedApplication extends FindApplication {
    
    FindOfferedApplication(Course course, Student student, ObjectContext context) {
        super(course, student, context)
    }

    FindOfferedApplication(Course course, Student student, ObjectContext context, QueryCacheStrategy cacheStrategy) {
        super(course, student, context, cacheStrategy)
    }
    
    @Override
    Application get() {
        return get(ApplicationStatus.OFFERED)
    }

    boolean isApplcation() {
        if (ENROLMENT_BY_APPLICATION == course.enrolmentType) {
            Application application = get()
            return application ? false : true
        }
        false
    }
    
}
