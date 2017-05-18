package ish.oncourse.services.application

import ish.common.types.ApplicationStatus
import ish.oncourse.model.Application
import ish.oncourse.model.Course
import ish.oncourse.model.Student
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import static ish.common.types.CourseEnrolmentType.ENROLMENT_BY_APPLICATION

class FindOfferedApplication extends FindApplication {
    
    FindOfferedApplication(Course course, Student student, ObjectContext context) {
        super(course, student, context)
    }

    @Override
    Application get() {
        return get(ApplicationStatus.OFFERED)
    }

    boolean isApplcation() {
        if (ENROLMENT_BY_APPLICATION == course.enrolmentType) {
            Application application = get()
            application ? false : true
        }
        false
    }
    
}
