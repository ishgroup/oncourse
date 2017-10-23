package ish.oncourse.willow.checkout.functions

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Course
import ish.oncourse.willow.functions.field.GetWaitingListFields
import ish.oncourse.willow.model.checkout.WaitingList
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class ProcessWaitingLists {
    
    final static  Logger logger = LoggerFactory.getLogger(ProcessWaitingLists.class)

    private ObjectContext context
    private Contact contact
    private College college
    private List<String> courseIds

    private List<WaitingList> waitingLists = []

    ProcessWaitingLists(ObjectContext context, Contact contact, College college, List<String> courseIds) {
        this.context = context
        this.contact = contact
        this.college = college
        this.courseIds = courseIds
    }

    ProcessWaitingLists process() {
        if (courseIds.unique().size() < courseIds.size()) {
            logger.error("Courses list contains duplicate entries: $courseIds")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'Courses list contains duplicate entries')).build())
        }
        courseIds.each { id ->
            Course course = new GetCourse(context, college, id).get()
            WaitingList waitingList = new WaitingList().with { list -> 
                list.contactId = contact.id.toString()
                list.courseId = course.id.toString()
                list.studentsCount = 1
                list.selected = true
                ValidateWaitingList validate = new ValidateWaitingList(context, college).validate(course, contact.student)
                list.errors += validate.errors
                list.warnings += validate.warnings
                list.fieldHeadings = new GetWaitingListFields(course).get()
                list
            }
            waitingLists << waitingList
        }
        this
    }
    
    List<WaitingList> getWaitingLists() {
        return waitingLists
    }
}
