package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.willow.model.checkout.Amount
import ish.oncourse.willow.model.checkout.Application
import ish.oncourse.willow.model.checkout.Article
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.checkout.Membership
import ish.oncourse.willow.model.checkout.Voucher
import org.apache.cayenne.ObjectContext

@CompileStatic
class ProcessCheckoutModel {
    
    ObjectContext context
    College college
    CheckoutModelRequest checkoutModelRequest

    Money totalAmount = Money.ZERO
    Money payNowAmount = Money.ZERO
    Money totalDiscount = Money.ZERO

    int enrolmentsCount = 0

    Map<Contact, List<CourseClass>> enrolmentsToProceed  = [:]
    Map<CourseClass, Integer> freePlacesMap  = [:]

    CheckoutModel model


    ProcessCheckoutModel(ObjectContext context, College college, CheckoutModelRequest checkoutModelRequest) {
        this.context = context
        this.college = college
        this.checkoutModelRequest = checkoutModelRequest
        this.model = new CheckoutModel()
        this.model.payerId = checkoutModelRequest.payerId
    }

    ProcessCheckoutModel process() {
        checkoutModelRequest.contactNodes.each { contactNode ->
            model.contactNodes << contactNode
            
            Contact contact = new GetContact(context, college, contactNode.contactId).get()
            contactNode.articles.each { a ->
                processArticle(a, contact)
            }

            contactNode.memberships.each { m ->
                processMembership(m, contact)
            }

            contactNode.vouchers.each { v ->
                processVoucher(v)
            }
            //all products should be payed permanently
            payNowAmount = Money.ZERO.add(totalAmount)
            
            contactNode.enrolments.each { e ->
                processEnrolment(e, contact)
            }

            contactNode.applications.each { a ->
                processApplication(a, contact)
            }
        }
        
        CalculateEnrolmentsPrice enrolmentsPrice = new CalculateEnrolmentsPrice(context, college, totalAmount, enrolmentsCount, model, enrolmentsToProceed, checkoutModelRequest.promotionIds).calculate()
        payNowAmount = payNowAmount.add(enrolmentsPrice.totalPayNow)
        model.amount = new Amount().with { a ->
            a.total = totalAmount.toPlainString()
            a.owing = totalAmount.subtract(enrolmentsPrice.totalDiscount).subtract(payNowAmount).toPlainString()
            a.payNow =  payNowAmount.toPlainString()
            a.discount = totalDiscount.add(enrolmentsPrice.totalDiscount).toPlainString()
            a
        }
        
        this
    }
    
    
    void processEnrolment(Enrolment e, Contact contact) {
        if (e.selected) {
            ProcessClass processClass = new ProcessClass(context, contact, college, e.classId).process()
            CourseClass courseClass = processClass.persistentClass

            if (processClass.enrolment == null) {
                e.errors << "Enrolment for $contact.fullName on $courseClass.course.name ($courseClass.course.code - $courseClass.code) avalible by application".toString()
            } else if (!checkAndBookPlace(courseClass)) {
                e.errors << "Unfortunately you just missed out. The class $courseClass.course.name ($courseClass.course.code - $courseClass.code) was removed from your shopping basket since the last place has now been filled. Please select another class from this course or join the waiting list. <a href=\"/course/$courseClass.course.code\">[ Show course ]</a>".toString()

            } else {
                e.errors += processClass.enrolment.errors
                e.warnings += processClass.enrolment.warnings
                if (e.errors.empty) {
                    enrolmentsCount++
                    e.price = processClass.enrolment.price
                    totalAmount = totalAmount.add(new Money(e.price.fee ?: e.price.feeOverriden))
                    List<CourseClass> classes = enrolmentsToProceed.get(contact)
                    if (classes == null) {
                        classes = new ArrayList<CourseClass>()
                        enrolmentsToProceed.put(contact, classes)
                    }
                    classes.add(courseClass)
                }
            }
        }
    }
    
    void processApplication(Application a, Contact contact) {
        if (a.selected) {
            ProcessClass processClass = new ProcessClass(context, contact, college, a.classId).process()
            CourseClass courseClass = processClass.persistentClass

            if (processClass.application == null) {
                a.errors << "Application for $contact.fullName on $courseClass.course.name ($courseClass.course.code - $courseClass.code) is wrong".toString()
            } else {
                a.errors += processClass.application.errors
                a.warnings += processClass.application.warnings
            }
        }
    }

    void processArticle(Article a, Contact contact) {
        if (a.selected) {
            ProcessProduct processProduct = new ProcessProduct(context, contact, college, a.productId, model.payerId).process()
            if (processProduct.article == null) {
                a.errors << "Purchase is wrong"
            } else {
                a.errors += processProduct.article.errors
                a.warnings += processProduct.article.warnings
                if (a.errors.empty) {
                    a.price = processProduct.article.price
                    totalAmount = totalAmount.add(new Money(a.price))
                }
            }
        }
    }
    
    void processMembership(Membership m, Contact contact) {
        if (m.selected) {
            ProcessProduct processProduct = new ProcessProduct(context, contact, college, m.productId, model.payerId).process()
            if (processProduct.membership == null) {
                m.errors << "Purchase is wrong"
            } else {
                m.errors += processProduct.membership.errors
                m.warnings += processProduct.membership.warnings
                if (m.errors.empty) {
                    m.price = processProduct.membership.price
                    totalAmount = totalAmount.add(new Money(m.price))
                }
            }
        }
    }
    
    void processVoucher(Voucher v) {
        if (v.selected) {
            ValidateVoucher validateVoucher = new ValidateVoucher(context, college, model.payerId).validate(v)
            v.errors += validateVoucher.errors
            v.warnings += validateVoucher.warnings
            if (v.errors.empty) {
                totalAmount = totalAmount.add(new Money(v.price))
            }
        }
    }

    private  boolean checkAndBookPlace(CourseClass courseClass) {
       Integer places = freePlacesMap.get(courseClass)
       if (places == null) {
           places = courseClass.availableEnrolmentPlaces
           freePlacesMap.put(courseClass, places)
       } 
        
       if (places > 0) {
           freePlacesMap.put(courseClass, places - 1)
           return true
       } else {
           return false
       }
    }

}
