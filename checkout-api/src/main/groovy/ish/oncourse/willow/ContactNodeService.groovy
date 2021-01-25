package ish.oncourse.willow


import groovy.transform.CompileStatic
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.*
import ish.oncourse.willow.checkout.functions.*
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.request.ContactNodeRequest
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileStatic
class ContactNodeService {
    
    final static  Logger logger = LogManager.getLogger(ContactNodeService)

    //params
    private EntityRelationService relationService
    private ContactNodeRequest request

    //controller state
    private List<CourseClass> shoppingCartClasses =[]
    private Set<Course> shoppingCartCourses = []
    private List<Product> shoppingCartProducts = []
    private College college
    private ObjectContext context

    private Set<Course> programCourses = []
    private Set<Product> programProducts = []
    
    //result
    Contact contact


    ContactNode node

    ContactNodeService(CayenneService cayenneService, CollegeService collegeService, EntityRelationService relationService, ContactNodeRequest request) {
        this.context = cayenneService.newContext()
        this.college = collegeService.getCollege()
        this.relationService = relationService
        this.request = request
    }
    
    ContactNode getContactNode() {

        contact = new GetContact(context, college, request.contactId).get(false)

        node = new ContactNode()
        node.contactId = contact.id.toString()

        if (!contact.isCompany) {
            ProcessClasses classesController = new ProcessClasses(context, contact, college, request.classIds, request.promotionIds).process()
            node.enrolments = classesController.enrolments
            node.applications = classesController.applications

            shoppingCartClasses = classesController.classesToEnrol
            shoppingCartCourses = shoppingCartClasses*.course.toSet()

            ProcessWaitingLists processWaitingLists = new ProcessWaitingLists(context, contact, college, request.waitingCourseIds).process()
            node.waitingLists += processWaitingLists.waitingLists
        }

        ProcessProducts processProducts = new ProcessProducts(context, contact, college, request.products).process()
        node.articles += processProducts.articles
        node.memberships += processProducts.memberships
        node.vouchers += processProducts.vouchers

        shoppingCartProducts = processProducts.productsToPurchase

        if (!shoppingCartCourses.empty) {
            addCourseRelations()
        }
        
        if (!shoppingCartProducts.empty) {
            addProductRelations()
        }
        
        //add suggested items after adding all 'shopping cart' items (since suggested items could be in cart already)
        addSuggestedItems()
        
        node
    } 
    
    
    private void addCourseRelations() {
        
        shoppingCartCourses.each { shoppingCartCourse ->
            String relatedClassId = shoppingCartClasses.find {clazz -> clazz.course == shoppingCartCourse }.id.toString()
            relationService.getCoursesToAdd(shoppingCartCourse, contact).each { relatedCourse, allowRemove ->
                addRelatedCourse(relatedCourse, allowRemove, relatedClassId.toString(), null)
            }

            relationService.getProductsToAdd(shoppingCartCourse).each {relatedProduct, allowRemove ->
                addRelatedProduct(relatedProduct, allowRemove, relatedClassId, null )
            }
        }
    }

    private void addProductRelations() {
        shoppingCartProducts.each { shoppingCartProduct ->
            if (contact.student) {
                relationService.getCoursesToAdd(shoppingCartProduct, contact).each { relatedCourse, allowRemove ->
                    addRelatedCourse(relatedCourse, allowRemove, null, shoppingCartProduct.id.toString())
                } 
            }
            relationService.getProductsToAdd(shoppingCartProduct).each {relatedProduct, allowRemove ->
                addRelatedProduct(relatedProduct, allowRemove, null, shoppingCartProduct.id.toString() )
            }
        }
    }

    private void addSuggestedItems() {
        shoppingCartCourses.each { shoppingCartCourse ->
            node.suggestedCourseIds += relationService.getSuggestedCourses(shoppingCartCourse).findAll { suggestedCurse ->
                !(suggestedCurse.id in shoppingCartCourses*.id || suggestedCurse.id in programCourses*.id)
            }.collect { it.id.toString() }

            node.suggestedProductIds += relationService.getSuggestedProducts(shoppingCartCourse).findAll { suggestedProduct ->
                !(suggestedProduct.id in shoppingCartProducts*.id || suggestedProduct.id in programProducts*.id)
            }.collect { it.id.toString() }
        }

        shoppingCartProducts.each { shoppingCartProduct ->
            node.suggestedCourseIds += relationService.getSuggestedCourses(shoppingCartProduct).findAll { suggestedCurse ->
                !(suggestedCurse.id in shoppingCartCourses*.id || suggestedCurse.id in programCourses*.id)
            }.collect { it.id.toString() }

            node.suggestedProductIds += relationService.getSuggestedProducts(shoppingCartProduct).findAll { suggestedProduct ->
                !(suggestedProduct.id in shoppingCartProducts*.id || suggestedProduct.id in programProducts*.id)
            }.collect { it.id.toString() }
        }

    }

    private void addRelatedCourse(Course relatedCourse, Boolean allowRemove, String relatedClassId, String relatedProductId) {
        
        List<CourseClass> addedClasses = shoppingCartClasses.findAll {it.course.id == relatedCourse.id }
        
        if (!addedClasses.empty) {
            addedClasses.each { addedClass ->
                node.enrolments.find { it.classId == addedClass.id.toString()}.relatedProductId(relatedProductId).relatedClassId(relatedClassId)
            }
        } else if (!(relatedCourse.id in programCourses*.id)) {
            programCourses << relatedCourse

            CourseClass availableClass = relatedCourse.getAvailableClasses().find { clazz ->
                new ValidateEnrolment(context,college).validate(clazz, contact.student).errors.empty
            }

            ish.oncourse.willow.model.checkout.Enrolment enrolment = null

            if (availableClass) {
                enrolment = new ProcessClass(context, contact, college, availableClass.id.toString(), null).process().enrolment
            }

            if (!enrolment) {
                enrolment= new ish.oncourse.willow.model.checkout.Enrolment()
                enrolment.contactId = contact.id
                enrolment.selected = false
                enrolment.errors << "There are no classes for course ${relatedCourse.name} available${allowRemove?"":", but they are mandatory"}. Please contact the college to have them resolve this.".toString()
            }
            enrolment.courseId = relatedCourse.id
            enrolment.allowRemove = allowRemove
            enrolment.relatedClassId = relatedClassId
            enrolment.relatedProductId = relatedProductId
            node.enrolments << enrolment
        }

    }

    private void addRelatedProduct(Product relatedProduct, Boolean allowRemove, String relatedClassId, String relatedProductId) {


        List<CourseClass> addedProducts = shoppingCartClasses.findAll {it.course.id == relatedProduct.id }

        if (!addedProducts.empty) {
            addedProducts.each { addedProduct ->
                node.articles.find { article -> article.productId == addedProduct.id.toString()}?.relatedProductId(relatedProductId)?.relatedClassId(relatedClassId)
                node.vouchers.find { voucher -> voucher.productId == addedProduct.id.toString()}?.relatedProductId(relatedProductId)?.relatedClassId(relatedClassId)
                node.memberships.find { membership -> membership.productId == addedProduct.id.toString()}?.relatedProductId(relatedProductId)?.relatedClassId(relatedClassId)
            }
        } else if (!(relatedProduct.id in programProducts*.id)) {
            programProducts << relatedProduct
            ProcessProduct processProduct = new ProcessProduct(context, contact, college, relatedProduct.id.toString(), 1, null, null).process()
            processProduct.article && node.articles << processProduct.article.relatedClassId(relatedClassId).relatedProductId(relatedProductId).allowRemove(allowRemove)
            processProduct.membership && node.memberships << processProduct.membership.relatedClassId(relatedClassId).relatedProductId(relatedProductId).allowRemove(allowRemove)
            processProduct.voucher && node.vouchers << processProduct.voucher.relatedClassId(relatedClassId).relatedProductId(relatedProductId).allowRemove(allowRemove)
        }
    }

}
