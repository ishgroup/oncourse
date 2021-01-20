package ish.oncourse.willow

import com.google.inject.Inject
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.willow.checkout.functions.*
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.checkout.request.ContactNodeRequest
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ContactNodeService {
    
    final static  Logger logger = LogManager.getLogger(ContactNodeService)

    private CayenneService cayenneService
    private CollegeService collegeService
    private EntityRelationService relationService

    @Inject
    ContactNodeService(CayenneService cayenneService, CollegeService collegeService, EntityRelationService relationService) {
        this.cayenneService = cayenneService
        this.collegeService = collegeService
        this.relationService = relationService
    }
    
    ContactNode getContactNode(ContactNodeRequest request) {

        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college

        Contact contact = new GetContact(context, college, request.contactId).get(false)

        ContactNode node = new ContactNode()
        node.contactId = contact.id.toString()
        
        ProcessClasses classesController = null
        
        if (!contact.isCompany) {
            classesController = new ProcessClasses(context, contact, college, request.classIds, request.promotionIds).process()
            node.enrolments = classesController.enrolments
            node.applications = classesController.applications

            ProcessWaitingLists processWaitingLists = new ProcessWaitingLists(context, contact, college, request.waitingCourseIds).process()
            node.waitingLists += processWaitingLists.waitingLists
        }

        ProcessProducts processProducts = new ProcessProducts(context, contact, college, request.products).process()
        node.articles += processProducts.articles
        node.memberships += processProducts.memberships
        node.vouchers += processProducts.vouchers

        if (classesController) {
            List<CourseClass> shoppingCartClasses = classesController.classesToEnrol
            Set<Course> shoppingCartCourses = shoppingCartClasses*.course.toSet()
            Set<Course> programCourses = []

            shoppingCartCourses.each { shoppingCartCourse ->
                
                Map<Course, Boolean> relatedCourses = relationService.getCoursesToAdd(shoppingCartCourse, contact)
                
                relatedCourses.each { relatedCourse, allowRemove ->
                    
                    if (!(relatedCourse.id in shoppingCartCourses*.id || relatedCourse.id in programCourses*.id)) {
                        CourseClass availableClass = relatedCourse.getAvailableClasses().find { clazz -> 
                            new ValidateEnrolment(context,college).validate(clazz, contact.student).errors.empty
                        }

                        Enrolment enrolment = null
                        
                        if (availableClass) {
                            enrolment = new ProcessClass(context, contact, college, availableClass.id.toString(), null).process().enrolment
                        } 
                        
                        if (!enrolment) {
                            enrolment= new Enrolment()
                            enrolment.contactId = contact.id
                            enrolment.selected = false
                            enrolment.errors << "There are no classes for course ${relatedCourse.name} available${allowRemove?"":", but they are mandatory"}. Please contact the college to have them resolve this.".toString()
                        }
                        enrolment.courseId = relatedCourse.id
                        enrolment.allowRemove = allowRemove
                        enrolment.relatedClassId = shoppingCartClasses.find {clazz -> clazz.course == shoppingCartCourse }.id
                        node.enrolments << enrolment 
                    }
                } 
                
            }
        }
        
        
        node
        
    } 
    

}
