package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.willow.functions.voucher.ProcessRedeemedVouchers
import ish.oncourse.willow.model.checkout.Amount
import ish.oncourse.willow.model.checkout.Application
import ish.oncourse.willow.model.checkout.Article
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.checkout.Membership
import ish.oncourse.willow.model.checkout.Voucher
import ish.oncourse.willow.model.common.CommonError
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

    @CompileStatic(TypeCheckingMode.SKIP)
    ProcessCheckoutModel process() {
        
        processNodes()
        
        CalculateEnrolmentsPrice enrolmentsPrice = new CalculateEnrolmentsPrice(context, college, totalAmount, enrolmentsCount, model, enrolmentsToProceed, checkoutModelRequest.promotionIds).calculate()
        totalDiscount = totalDiscount.add(enrolmentsPrice.totalDiscount)
        
        ProcessRedeemedVouchers redeemedVouchers = new ProcessRedeemedVouchers(context, college, checkoutModelRequest, totalAmount.subtract(totalDiscount), enrolmentsPrice.enrolmentNodes).process()
        payNowAmount = enrolmentsPrice.totalPayNow.min(redeemedVouchers.leftToPay)

        if (redeemedVouchers.error) {
            model.error = redeemedVouchers.error
            return this
        }
        
        model.amount = new Amount().with { a ->
            a.total = totalAmount.doubleValue()
            a.owing = totalAmount.subtract(totalDiscount).subtract(payNowAmount).doubleValue()
            a.payNow =  payNowAmount.doubleValue()
            a.discount = totalDiscount.doubleValue()
            a.voucherPayments = redeemedVouchers.voucherPayments
            a
        }
        
        this
    }
    
    void processNodes() {
        checkoutModelRequest.contactNodes.each { contactNode ->
            model.contactNodes << contactNode

            Contact contact = new GetContact(context, college, contactNode.contactId).get(false)
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

            if (contact.student) {
                contactNode.enrolments.each { e ->
                    processEnrolment(e, contact)
                }

                contactNode.applications.each { a ->
                    processApplication(a, contact)
                }
            } else if (!contactNode.enrolments.empty || !contactNode.applications.empty ) {
                model.error = new CommonError(message: 'Purchase items are not valid')
            }

        }
    }
    
    
    @CompileStatic(TypeCheckingMode.SKIP)
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
                    totalAmount = totalAmount.add(e.price.fee?.toMoney() ?: e.price.feeOverriden.toMoney())
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


    @CompileStatic(TypeCheckingMode.SKIP)
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
                    totalAmount = totalAmount.add(a.price.toMoney())
                }
            }
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
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
                    totalAmount = totalAmount.add(m.price.toMoney())
                }
            }
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    void processVoucher(Voucher v) {
        if (v.selected) {
            ValidateVoucher validateVoucher = new ValidateVoucher(context, college, model.payerId).validate(v)
            v.errors += validateVoucher.errors
            v.warnings += validateVoucher.warnings
            if (v.errors.empty) {
                totalAmount = totalAmount.add(v.price.toMoney())
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
