package ish.oncourse.willow.checkout.functions

import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.model.Student
import ish.oncourse.services.application.FindOfferedApplication
import ish.oncourse.willow.model.checkout.Application
import org.apache.cayenne.ObjectContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ValidateApplication extends Validate<Application> {
    
    final static  Logger logger = LoggerFactory.getLogger(ValidateApplication.class)

    ValidateApplication(ObjectContext context, College college) {
        super(context, college)
    }
    
    ValidateApplication validate(Application application) {
        validate(new GetCourseClass(context, college, application.classId).get().course, new GetContact(context, college, application.contactId).get().student)
    }

    ValidateApplication validate(Course course, Student student) {
        if (new FindOfferedApplication(course, student, context).get() != null) {
            errors << "An application for this student has been received. Please contact us if you have any questions."
        }
        this
    }

}
