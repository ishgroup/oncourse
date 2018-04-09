package ish.oncourse.willow.checkout.persistent

import groovy.transform.CompileStatic
import ish.common.types.ConfirmationStatus
import ish.common.types.EnrolmentStatus
import ish.common.types.PaymentSource
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Enrolment
import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.Tax
import ish.oncourse.willow.checkout.functions.GetCourseClass
import ish.oncourse.willow.checkout.payment.EnrolmentInvoiceLine
import ish.oncourse.willow.model.field.Field
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

@CompileStatic
class CreateEnrolment {
    
    private ObjectContext context
    private College college
    private ish.oncourse.willow.model.checkout.Enrolment e
    private Contact contact
    private EnrolmentStatus status
    private Closure setInvoice
    private Tax taxOverridden
    private ConfirmationStatus confirmationStatus


    CreateEnrolment(ObjectContext context, College college, ish.oncourse.willow.model.checkout.Enrolment e, Contact contact, EnrolmentStatus status, ConfirmationStatus confirmationStatus, Tax taxOverridden, Closure setInvoice) {
        this.context = context
        this.college = college
        this.e = e
        this.contact = contact
        this.status = status
        this.setInvoice = setInvoice
        this.taxOverridden = taxOverridden
        this.confirmationStatus = confirmationStatus
    }

    void create() {
        CourseClass courseClass = new GetCourseClass(context, college, e.classId).get()
        Enrolment enrolment = context.newObject(Enrolment)
        enrolment.courseClass = courseClass
        enrolment.student = contact.student
        contact.modified = new Date()
        enrolment.status = status
        enrolment.source = PaymentSource.SOURCE_WEB
        enrolment.college = college
        enrolment.confirmationStatus = confirmationStatus

        InvoiceLine invoiceLine = new EnrolmentInvoiceLine(enrolment, e.price, taxOverridden).create()
        invoiceLine.enrolment = enrolment
        
        setInvoice.call(enrolment, invoiceLine)

        (e.fieldHeadings.fields.flatten() as List<Field>).each { f  -> 
            
            String value = StringUtils.trimToNull(f.value)
            if (value) {
                enrolment.setCustomFieldValue(f.key.split("\\.")[2], value)     
            }
        }
    }
}
