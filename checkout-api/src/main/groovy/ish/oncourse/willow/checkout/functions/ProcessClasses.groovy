package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.willow.model.checkout.Application
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

@CompileStatic
class ProcessClasses {

    ObjectContext context
    Contact contact
    College college
    List<String> classesIds
    List<String> promotionIds

    List<Enrolment> enrolments = []
    List<Application> applications = []
    
    List<CourseClass> classesToEnrol = []
    
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
            ProcessClass processClass = new ProcessClass(context, contact, college, id, null).process()
            processClass.application && applications << processClass.application
            if (processClass.enrolment) {
                enrolments << processClass.enrolment
                classesToEnrol << processClass.persistentClass
            }
            
        }
        
        this
    }
    
}
