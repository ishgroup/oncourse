package ish.oncourse.willow

import com.google.inject.Inject
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.*
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

        //handle enrolment program
        if (classesController) {
            List<CourseClass> shoppingCartClasses = classesController.classesToEnrol
            Set<Course> shoppingCartCourses = shoppingCartClasses*.course.toSet()

            List<Product> shoppingCartProducts = processProducts.productsToPurchase

            Set<Course> programCourses = []
            Set<Product> programProducts = []

            shoppingCartCourses.each { shoppingCartCourse ->
                
                relationService.getCoursesToAdd(shoppingCartCourse, contact).each { relatedCourse, allowRemove ->
                    if (!(relatedCourse.id in shoppingCartCourses*.id || relatedCourse.id in programCourses*.id)) {
                        programCourses << relatedCourse
                        
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
                
                relationService.getProductsToAdd(shoppingCartCourse).each {relatedProduct, allowRemove ->
                    if (!(relatedProduct.id in shoppingCartProducts*.id || relatedProduct.id in programProducts*.id)) {
                        programProducts << relatedProduct
                        ProcessProduct processProduct = new ProcessProduct(context, contact, college, relatedProduct.id.toString(), 1, null, null).process()
                        processProduct.article && node.articles << processProduct.article
                        processProduct.membership && node.memberships << processProduct.membership
                        processProduct.voucher && node.vouchers << processProduct.voucher
                    }
                }
            }
            //add suggested items after adding all 'shopping cart' items (since suggested items could be in cart already)
            shoppingCartCourses.each { shoppingCartCourse ->
                 node.suggestedCourseIds += relationService.getSuggestedCourses(shoppingCartCourse).findAll { suggestedCurse -> 
                     !(suggestedCurse.id in shoppingCartCourses*.id || suggestedCurse.id in programCourses*.id)
                 }.collect { it.id.toString() }

                node.suggestedProductIds += relationService.getSuggestedProducts(shoppingCartCourse).findAll { suggestedProduct ->
                    !(suggestedProduct.id in shoppingCartProducts*.id || suggestedProduct.id in programProducts*.id)
                }.collect { it.id.toString() }
            }
        }
        node
    } 
    

}
