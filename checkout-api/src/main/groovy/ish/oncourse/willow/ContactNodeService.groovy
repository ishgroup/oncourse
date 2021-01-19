package ish.oncourse.willow

import com.google.inject.Inject
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Course
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.checkout.functions.ProcessClasses
import ish.oncourse.willow.checkout.functions.ProcessProducts
import ish.oncourse.willow.checkout.functions.ProcessWaitingLists
import ish.oncourse.willow.model.checkout.ContactNode
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
    ContactNodeService(CayenneService cayenneService, CollegeService collegeService) {
        this.cayenneService = cayenneService
        this.collegeService = collegeService
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
            Set<Course> shoppingCartCourses = classesController.classesToEnrol*.course.toSet()

            shoppingCartCourses.each { shoppingCartCourse ->
                Map<Course, Boolean> relatedCourses = relationService.getCoursesToAdd(shoppingCartCourse, contact)
                relatedCourses.each { relatedCourse, allowRemove ->
                    if (!(relatedCourse.id in shoppingCartCourses*.id)) {
                         //TODO add first available class                         
                    }
                } 
                
            }
        }
        
        
        node
        
    } 
}
