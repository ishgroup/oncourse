package ish.oncourse.willow.checkout.functions

import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.model.Student
import ish.oncourse.willow.model.checkout.WaitingList
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ValidateWaitingList extends Validate<WaitingList> {

    final static  Logger logger = LoggerFactory.getLogger(ValidateWaitingList.class)

    ValidateWaitingList(ObjectContext context, College college) {
        super(context, college)
    }

    ValidateWaitingList validate(WaitingList waitingList) {
        validate(new GetCourse(context, college, waitingList.courseId).get(), new GetContact(context, college, waitingList.contactId).get().student)
    }

    ValidateWaitingList validate(Course course, Student student) {
        List<ish.oncourse.model.WaitingList> waitingLists = (ObjectSelect.query(ish.oncourse.model.WaitingList.class).
                where(ish.oncourse.model.WaitingList.STUDENT.eq(student)) 
                & ish.oncourse.model.WaitingList.COURSE.eq(course)).
                select(context)
        
        if (!waitingLists.empty) {
            errors << "Student $student.fullName has already been added to waiting list for $course.name course".toString()
        }
        this
    }

}
