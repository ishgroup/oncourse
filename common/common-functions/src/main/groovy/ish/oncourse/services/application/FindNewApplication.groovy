package ish.oncourse.services.application

import ish.common.types.ApplicationStatus
import ish.oncourse.model.Application
import ish.oncourse.model.Course
import ish.oncourse.model.Student
import org.apache.cayenne.ObjectContext

class FindNewApplication extends FindApplication {

    FindNewApplication(Course course, Student student, ObjectContext context) {
        super(course, student, context)
    }

    @Override
    Application get() {
        return get(ApplicationStatus.NEW)
    }
}
