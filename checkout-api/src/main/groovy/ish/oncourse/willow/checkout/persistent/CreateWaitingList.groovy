package ish.oncourse.willow.checkout.persistent

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.WaitingList
import ish.oncourse.willow.checkout.functions.GetCourse
import ish.oncourse.willow.model.field.Field
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

class CreateWaitingList {

    private ObjectContext context
    private College college
    private ish.oncourse.willow.model.checkout.WaitingList w
    private Contact contact

    CreateWaitingList(ObjectContext context, College college, ish.oncourse.willow.model.checkout.WaitingList w, Contact contact) {
        this.context = context
        this.college = college
        this.w = w
        this.contact = contact
    }

    WaitingList create() {
        WaitingList waitingList = context.newObject(WaitingList)
        waitingList.college = college
        waitingList.student = contact.student
        contact.modified = new Date()
        waitingList.course = new GetCourse(context, college, w.courseId).get()
        waitingList.detail = w.detail
        waitingList.potentialStudents = w.studentsCount.intValue()

        (w.fieldHeadings.fields.flatten() as List<Field>).each { f  ->
            String value = StringUtils.trimToNull(f.value)
            if (value) {
                waitingList.setCustomFieldValue(f.key.split("\\.")[2], value)
            }
        }
        waitingList
    }
}
