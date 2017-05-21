package ish.oncourse.willow.checkout.functions

import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Discount
import ish.oncourse.services.application.FindOfferedApplication
import ish.oncourse.willow.model.checkout.Application
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

import static ish.common.types.CourseEnrolmentType.ENROLMENT_BY_APPLICATION


class ProcessClasses {

    ObjectContext context
    Contact contact
    College college
    List<String> classesIds
    List<String> promotionIds

    List<Enrolment> enrolments = []
    List<Application> applications = []
    
    ProcessClasses(ObjectContext context, Contact contact, College college, List<String> classesIds, List<String> promotionIds) {
        this.context = context
        this.contact = contact
        this.college = college
        this.classesIds = classesIds
        this.promotionIds = promotionIds
    }
    
    final static  Logger logger = LoggerFactory.getLogger(ProcessClasses.class)

    ProcessClasses process() {

        if (classesIds.unique().size() < classesIds.size()) {
            logger.error("classes list contains duplicate entries: $classesIds")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'classes list contains duplicate entries')).build())
        }

        classesIds.each { id ->
            CourseClass c = new GetCourseClass(context, college,id).get()
            List<Discount> promotion = []
            promotionIds.each {
                promotion << new GetDiscount(context, college,id).get()
            }
            
            boolean allowByApplication = false
            Money overridenFee = null

            if (ENROLMENT_BY_APPLICATION == c.course.enrolmentType) {
                ish.oncourse.model.Application application = new FindOfferedApplication(c.course, contact.student, context).get()
                if (application != null) {
                    overridenFee = application.feeOverride
                    allowByApplication = false
                } else {
                    allowByApplication = true
                }
            }

            if (allowByApplication) {
                applications << new Application().with { a ->
                    a.contactId = contact.id.toString()
                    a.classId = c.id.toString()
                    ValidateApplication validateApplication = new ValidateApplication(context, college).validate(c.course, contact.student)
                    a.errors += validateApplication.errors
                    a.warnings += validateApplication.warnings
                    a
                }
            } else {
                enrolments << new Enrolment().with { e ->
                    e.contactId = contact.id.toString()
                    e.classId = c.id.toString()
                    ValidateEnrolment validateEnrolment = new ValidateEnrolment(context, college).validate(c, contact.student).validate(c, contact.student).validate(c, contact.student)
                    e.errors += validateEnrolment.errors
                    e.warnings += validateEnrolment.warnings
                    if (errors.empty) {
                        e.price = new BuildClassPrice(c, contact.student, allowByApplication, overridenFee, promotion).build()
                    }
                    e
                }
            }
        }
      
        
        this
    }
    
}
