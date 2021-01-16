package ish.oncourse.willow.checkout.persistent

import ish.oncourse.cayenne.FieldInterface
import ish.oncourse.common.field.PropertyGetSet
import ish.oncourse.common.field.PropertyGetSetFactory
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.WaitingList
import ish.oncourse.willow.checkout.functions.GetCourse
import ish.oncourse.willow.functions.field.FieldValueParser
import ish.oncourse.willow.model.field.Field
import org.apache.cayenne.ObjectContext 

class CreateWaitingList {

    private ObjectContext context
    private College college
    private ish.oncourse.willow.model.checkout.WaitingList w
    private Contact contact
    private PropertyGetSetFactory factory
    
    CreateWaitingList(ObjectContext context, College college, ish.oncourse.willow.model.checkout.WaitingList w, Contact contact) {
        this.context = context
        this.college = college
        this.w = w
        this.contact = contact
        this.factory = new PropertyGetSetFactory('ish.oncourse.model')

    }

    WaitingList create() {
        WaitingList waitingList = context.newObject(WaitingList)
        waitingList.college = college
        waitingList.student = contact.student
        // Put contact record side by side with student into replication queue.
        // Need to keep newly created Student/Contact records in single replication group
        contact.modified = new Date()
        waitingList.course = new GetCourse(context, college, w.courseId).get()
        (w.fieldHeadings.fields.flatten() as List<Field>).each { f  ->
            PropertyGetSet getSet = factory.get([getProperty: {f.key}] as FieldInterface, waitingList)
            getSet.set( new FieldValueParser(f, context).parse().value)
        }
        waitingList
    }
}
