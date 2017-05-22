package ish.oncourse.willow.checkout

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.willow.checkout.functions.ApplayDiscounts
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.checkout.functions.ProcessClass
import ish.oncourse.willow.checkout.functions.ProcessClasses
import ish.oncourse.willow.checkout.functions.ProcessProduct
import ish.oncourse.willow.checkout.functions.ProcessProducts
import ish.oncourse.willow.checkout.functions.ValidateVoucher
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.PurchaseItems
import ish.oncourse.willow.model.checkout.request.PurchaseItemsRequest
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.service.CheckoutApi
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.configuration.server.ServerRuntime

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response


@CompileStatic
class CheckoutApiImpl implements CheckoutApi {
    
    final static  Logger logger = LoggerFactory.getLogger(CheckoutApiImpl.class)


    private ServerRuntime cayenneRuntime
    private CollegeService collegeService

    @Inject
    CheckoutApiImpl(ServerRuntime cayenneRuntime, CollegeService collegeService) {
        this.cayenneRuntime = cayenneRuntime
        this.collegeService = collegeService
    }

    @Override
    CheckoutModel calculateAmount(CheckoutModel checkoutModel) {
        
        ObjectContext context = cayenneRuntime.newContext()
        College college = collegeService.college

        Money total = Money.ZERO
        int enrolmentsCount = 0
        
        Map<Contact, List<CourseClass>> enrolmentsToProceed  = [:]
        
        checkoutModel.purchaseItemsList.each { purchaseItems ->
            Contact contact = new GetContact(context, college, purchaseItems.contactId).get()
            purchaseItems.enrolments.each { e ->
                ProcessClass processClass = new ProcessClass(context, contact, college, e.classId).process()
                CourseClass courseClass = processClass.persistentClass
                
                if (processClass.enrolment == null) {
                    e.errors << "Enrolment for $contact.fullName on $courseClass.course.name ($courseClass.course.code - $courseClass.code) avalible by application".toString()
                } else {
                    e.errors += processClass.enrolment.errors
                    e.warnings += processClass.enrolment.warnings
                    if (e.errors.empty) {
                        enrolmentsCount++
                        e.price = processClass.enrolment.price
                        total = total.add(new Money(e.price.fee?:e.price.feeOverriden))
                        List<CourseClass> classes = enrolmentsToProceed.get(contact)
                        if (classes == null) {
                            classes = new ArrayList<CourseClass>()
                            enrolmentsToProceed.put(contact,classes)
                        }
                        classes.add(courseClass)
                    }
                }
            }

            purchaseItems.applications.each { a ->
                ProcessClass processClass = new ProcessClass(context, contact, college, a.classId).process()
                CourseClass courseClass = processClass.persistentClass

                if (processClass.application == null) {
                    a.errors << "Application for $contact.fullName on $courseClass.course.name ($courseClass.course.code - $courseClass.code) is wrong".toString()
                } else {
                    a.errors += processClass.application.errors
                    a.warnings += processClass.application.warnings
                }
            }
            
            purchaseItems.articles.each { a ->
                ProcessProduct processProduct = new ProcessProduct(context, contact, college, a.productId).process()
                if (processProduct.article == null) {
                    a.errors << "Purchase is wrong"
                } else {
                    a.errors += processProduct.article.errors
                    a.warnings += processProduct.article.warnings
                    if (a.errors.empty) {
                        a.price = processProduct.article.price
                        total = total.add(new Money(a.price))
                    }
                }
            }
            
            purchaseItems.memberships.each { m ->
                ProcessProduct processProduct = new ProcessProduct(context, contact, college, m.productId).process()
                if (processProduct.membership == null) {
                    m.errors << "Purchase is wrong"
                } else {
                    m.errors += processProduct.membership.errors
                    m.warnings += processProduct.membership.warnings
                    if (m.errors.empty) {
                        m.price = processProduct.membership.price
                        total = total.add(new Money(m.price))
                    }
                }
            }

            purchaseItems.vouchers.each { v ->
                ValidateVoucher validateVoucher = new ValidateVoucher(context, college).validate(v)
                v.errors += validateVoucher.errors
                v.warnings += validateVoucher.warnings
                if (v.errors.empty) {
                    total = total.add(new Money(v.price))
                }
            }
        }

        checkoutModel = new ApplayDiscounts(context, college, total, enrolmentsCount, checkoutModel).applyFor(enrolmentsToProceed)
        
        checkoutModel
    }
    

    @Override
    PurchaseItems getPurchaseItems(PurchaseItemsRequest purchaseItemsRequest) {

        if (purchaseItemsRequest.classIds.empty && purchaseItemsRequest.productIds.empty) {
            logger.error('There are not selected items for purchase')
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'there are not selected items for purchase')).build())
        }
        
        ObjectContext context = cayenneRuntime.newContext()
        College college = collegeService.college
        
        Contact contact = new GetContact(context, college, purchaseItemsRequest.contactId).get()
        
        PurchaseItems items = new PurchaseItems()
        items.contactId = contact.id.toString()
        ProcessClasses processClasses = new ProcessClasses(context, contact, college, purchaseItemsRequest.classIds, purchaseItemsRequest.promotionIds).process()
        items.enrolments = processClasses.enrolments
        items.applications = processClasses.applications
        
        ProcessProducts processProducts = new ProcessProducts(context, contact, college, purchaseItemsRequest.productIds).process()
        items.articles += processProducts.articles
        items.memberships += processProducts.memberships
        items.vouchers += processProducts.vouchers
        items
    }
}
