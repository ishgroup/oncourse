package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.services.application.FindOfferedApplication
import ish.oncourse.willow.model.checkout.Application
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.web.CourseClassPrice
import org.apache.cayenne.ObjectContext

import static ish.common.types.CourseEnrolmentType.ENROLMENT_BY_APPLICATION

class ProcessClass {
    
    ObjectContext context
    Contact contact
    College college
    String classId

    Enrolment enrolment
    Application application

    CourseClass persistentClass 
    
    ProcessClass(ObjectContext context, Contact contact, College college, String classId) {
        this.context = context
        this.contact = contact
        this.college = college
        this.classId = classId
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    ProcessClass process() {

        persistentClass = new GetCourseClass(context, college, classId).get()

        boolean allowByApplication = false
        Money overridenFee = null

        if (ENROLMENT_BY_APPLICATION == persistentClass.course.enrolmentType) {
            ish.oncourse.model.Application application = new FindOfferedApplication(persistentClass.course, contact.student, context).get()
            if (application != null) {
                overridenFee = application.feeOverride
                allowByApplication = false
            } else {
                allowByApplication = true
            }
        }

        if (allowByApplication) {
            application = new Application().with { a ->
                a.contactId = contact.id.toString()
                a.classId = persistentClass.id.toString()
                a.selected = true
                ValidateApplication validateApplication = new ValidateApplication(context, college).validate(persistentClass.course, contact.student)
                a.errors += validateApplication.errors
                a.warnings += validateApplication.warnings
                a
            }
        } else {
            enrolment  = new Enrolment().with { e ->
                e.contactId = contact.id.toString()
                e.classId = persistentClass.id.toString()
                e.selected = true
                ValidateEnrolment validateEnrolment = new ValidateEnrolment(context, college).validate(persistentClass, contact.student)
                e.errors += validateEnrolment.errors
                e.warnings += validateEnrolment.warnings
                if (e.errors.empty) {
                    e.price = new CourseClassPrice().with { ccp ->
                        ccp.hasTax = !persistentClass.gstExempt 
                        if (overridenFee != null) {
                            ccp.feeOverriden = persistentClass.gstExempt ? overridenFee.doubleValue() : overridenFee.multiply(BigDecimal.ONE.add(persistentClass.taxRate)).doubleValue()
                        } else {
                            ccp.fee = new CalculatePrice(persistentClass.feeExGst, Money.ZERO, persistentClass.taxRate, CalculatePrice.calculateTaxAdjustment(persistentClass)).calculate().finalPriceToPayIncTax.doubleValue()
                        }
                        ccp
                    }
                }
                e
            }
        }
        
        this
    }
}
