package ish.oncourse.willow.checkout.persistent

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
import org.apache.cayenne.ObjectContext

class CreateEnrolment {


    private ObjectContext context
    private College college
    private ish.oncourse.willow.model.checkout.Enrolment e
    private Contact contact
    private EnrolmentStatus status
    private Closure setInvoice
    private Tax taxOverridden

    CreateEnrolment(ObjectContext context, College college, ish.oncourse.willow.model.checkout.Enrolment e, Contact contact, EnrolmentStatus status, Tax taxOverridden, Closure setInvoice) {
        this.context = context
        this.college = college
        this.e = e
        this.contact = contact
        this.status = status
        this.setInvoice = setInvoice
        this.taxOverridden = taxOverridden
    }

    void create() {
        CourseClass courseClass = new GetCourseClass(context, college, e.classId).get()
        Enrolment enrolment = context.newObject(Enrolment)
        enrolment.courseClass = courseClass
        enrolment.student = contact.student
        enrolment.status = status
        enrolment.source = PaymentSource.SOURCE_WEB
        enrolment.college = college
        enrolment.confirmationStatus = ConfirmationStatus.NOT_SENT

        InvoiceLine invoiceLine = new EnrolmentInvoiceLine(enrolment, e.price, taxOverridden).create()
        invoiceLine.enrolment = enrolment
        
        setInvoice.call(enrolment, invoiceLine)
    }
}
