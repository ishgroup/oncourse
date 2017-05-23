package ish.oncourse.willow.checkout.functions

import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.willow.model.checkout.Application
import ish.oncourse.willow.model.checkout.Article
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.checkout.Membership
import ish.oncourse.willow.model.checkout.Voucher
import org.apache.cayenne.ObjectContext

class ProcessCheckoutModel {
    
    ObjectContext context
    College college
    CheckoutModel checkoutModel

    Money total = Money.ZERO
    Money payNow = Money.ZERO
    Money totalDiscount = Money.ZERO
    Money owing = Money.ZERO

    int enrolmentsCount = 0

    Map<Contact, List<CourseClass>> enrolmentsToProceed  = [:]

    ProcessCheckoutModel(ObjectContext context, College college, CheckoutModel checkoutModel) {
        this.context = context
        this.college = college
        this.checkoutModel = checkoutModel
    }

    ProcessCheckoutModel process() {
        checkoutModel.purchaseItemsList.each { purchaseItems ->
            Contact contact = new GetContact(context, college, purchaseItems.contactId).get()

            purchaseItems.articles.each { a ->
                processArticle(a, contact)
            }

            purchaseItems.memberships.each { m ->
                processMembership(m, contact)
            }

            purchaseItems.vouchers.each { v ->
                processVoucher(v)
            }
            //all products should be payed permanently
            payNow = Money.ZERO.add(total)
            
            purchaseItems.enrolments.each { e ->
                processEnrolment(e, contact)
            }

            purchaseItems.applications.each { a ->
                processApplication(a, contact)
            }
        }
        
        CalculateEnrolmentsPrice enrolmentsPrice = new CalculateEnrolmentsPrice(context, college, total, enrolmentsCount, checkoutModel, enrolmentsToProceed).calculate()
        checkoutModel = enrolmentsPrice.model
        payNow = payNow.add(enrolmentsPrice.totalPayNow)
        totalDiscount = totalDiscount.add(enrolmentsPrice.totalDiscount)
        owing = total.subtract(enrolmentsPrice.totalDiscount).subtract(enrolmentsPrice.totalPayNow)
        
        this
    }
    
    
    void processEnrolment(Enrolment e, Contact contact) {
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
    
    void processApplication(Application a, Contact contact) {
        ProcessClass processClass = new ProcessClass(context, contact, college, a.classId).process()
        CourseClass courseClass = processClass.persistentClass

        if (processClass.application == null) {
            a.errors << "Application for $contact.fullName on $courseClass.course.name ($courseClass.course.code - $courseClass.code) is wrong".toString()
        } else {
            a.errors += processClass.application.errors
            a.warnings += processClass.application.warnings
        }
    }

    void processArticle(Article a, Contact contact) {
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
    
    void processMembership(Membership m, Contact contact) {
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
    
    void processVoucher(Voucher v) {
        ValidateVoucher validateVoucher = new ValidateVoucher(context, college).validate(v)
        v.errors += validateVoucher.errors
        v.warnings += validateVoucher.warnings
        if (v.errors.empty) {
            total = total.add(new Money(v.price))
        }
    }

}
