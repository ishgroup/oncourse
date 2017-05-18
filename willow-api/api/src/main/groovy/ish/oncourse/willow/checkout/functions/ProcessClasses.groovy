package ish.oncourse.willow.checkout.functions

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Discount
import ish.oncourse.services.application.FindOfferedApplication
import ish.oncourse.willow.model.checkout.Application
import ish.oncourse.willow.model.checkout.Enrolment
import org.apache.cayenne.ObjectContext


class ProcessClasses {

    ObjectContext context
    Contact contact
    College college
    List<CourseClass> classes
    List<Discount> promotions
    
    List<Enrolment> enrolments = []
    List<Application> applications = []



    ProcessClasses process() {

        classes.each { c ->
            if (new FindOfferedApplication(c.course, contact.student, context).isApplcation()) {
                applications << new Application().with { a ->
                    a.contactId = c.id.toString()
                    a.title = "$c.course.name ($c.course.code-$c.code) $contact.fullName"
                    ValidateApplication validateApplication = new ValidateApplication(context: context, college: college, application: a).validate()
                    a.errors += validateApplication.errors
                    a.warnings += validateApplication.warnings

                    
                }
            }
            
            
            
            
            
        }
      
        
        this
    }
    
}
