package ish.oncourse.willow.checkout.persistent

import ish.common.types.ApplicationStatus
import ish.common.types.ConfirmationStatus
import ish.common.types.PaymentSource
import ish.common.types.ProductStatus
import ish.oncourse.model.Application
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.willow.checkout.functions.GetCourseClass
import ish.oncourse.willow.model.field.Field
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

class CreateApplication {

    private ObjectContext context
    private College college
    private ish.oncourse.willow.model.checkout.Application a
    private Contact contact

    CreateApplication(ObjectContext context, College college, ish.oncourse.willow.model.checkout.Application a, Contact contact) {
        this.context = context
        this.college = college
        this.a = a
        this.contact = contact
    }

    Application create() {
        CourseClass courseClass = new GetCourseClass(context, college, a.classId).get()
        Application application = context.newObject(Application)
        application.college = college
        application.student = contact.student
        // Put contact record side by side with student into replication queue.
        // Need to keep newly created Student/Contact records in single replication group
        contact.modified = new Date()
        application.course = courseClass.course
        application.status = ApplicationStatus.NEW
        application.source = PaymentSource.SOURCE_WEB
        application.confirmationStatus = ConfirmationStatus.NOT_SENT

        (a.fieldHeadings.fields.flatten() as List<Field>).each { f  ->
            String value = StringUtils.trimToNull(f.value)
            if (value) {
                application.setCustomFieldValue(f.key.split("\\.")[2], value)
            }
        }
        application
    }
    
    
}
