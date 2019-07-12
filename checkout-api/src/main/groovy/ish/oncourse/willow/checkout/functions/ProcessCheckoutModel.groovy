package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CorporatePass
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Product
import ish.oncourse.model.Tax
import ish.oncourse.services.preference.IsCorporatePassEnabled
import ish.oncourse.services.preference.IsCreditCardPaymentEnabled
import ish.oncourse.willow.FinancialService
import ish.oncourse.willow.checkout.corporatepass.ValidateCorporatePass
import ish.oncourse.willow.functions.field.GetWaitingListFields
import ish.oncourse.willow.functions.field.ValidateCustomFields
import ish.oncourse.willow.functions.voucher.ProcessRedeemedVouchers
import ish.oncourse.willow.model.checkout.Amount
import ish.oncourse.willow.model.checkout.Application
import ish.oncourse.willow.model.checkout.Article
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.checkout.Membership
import ish.oncourse.willow.model.checkout.Voucher
import ish.oncourse.willow.model.checkout.VoucherPayment
import ish.oncourse.willow.model.checkout.WaitingList
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.common.ValidationError
import ish.oncourse.willow.model.field.FieldHeading
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

import static ish.math.Money.ZERO

@CompileStatic
class ProcessCheckoutModel {
    
    private ObjectContext context
    private College college
    private CheckoutModelRequest checkoutModelRequest

    private Money totalAmount = ZERO
    private Money totalProductsAmount = ZERO

    private Map<Contact, List<CourseClass>> enrolmentsToProceed  = [:]
    private List<Product> products = []

    private Map<CourseClass, Integer> freePlacesMap  = [:]
    private CorporatePass corporatePass
    private ContactNode corporatePassNode
    
    private CheckoutModel model
    private Tax taxOverridden

    private FinancialService financialService


    ProcessCheckoutModel(ObjectContext context, College college, CheckoutModelRequest checkoutModelRequest) {
        this(context, college, checkoutModelRequest, null)
    }


    ProcessCheckoutModel(ObjectContext context, College college, CheckoutModelRequest checkoutModelRequest, FinancialService financialService) {
        this.context = context
        this.college = college
        this.checkoutModelRequest = checkoutModelRequest
        this.model = new CheckoutModel()
        this.model.validationErrors = new ValidationError()
        this.model.payerId = checkoutModelRequest.payerId
        this.financialService = financialService
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    ProcessCheckoutModel process() {
        processCorporatePass()
        if (!corporatePass && this.model.payerId) {
            taxOverridden = new GetContact(context, college, this.model.payerId).get(false).taxOverride
            
        }
        
        processNodes()
        if (model.error) {
            return this
        }

        CalculateEnrolmentsPrice enrolmentsPrice = new CalculateEnrolmentsPrice(context, college, totalAmount, model, enrolmentsToProceed, checkoutModelRequest.promotionIds, corporatePass, taxOverridden).calculate()
        Money subTotal = totalAmount.subtract( enrolmentsPrice.totalDiscount)
        
        if (!corporatePass) {
            
            Money minPayNow = totalProductsAmount.add(enrolmentsPrice.minPayNow)
            Money availableCredit = financialService.getAvalibleCredit(model.payerId).min(subTotal)
            Money payNow = checkoutModelRequest.payNow != null ? checkoutModelRequest.payNow.toMoney() : minPayNow

            Money ccPayment = ZERO
            Money usedCredit = ZERO
                    
            ProcessRedeemedVouchers redeemedVouchers = new ProcessRedeemedVouchers(context, college, checkoutModelRequest, payNow, enrolmentsPrice.enrolmentNodes).process()
            if (redeemedVouchers.error) {
                model.error = redeemedVouchers.error
                return this
            }

            //adjust minPayNow if course voucher cover all payment plan invoice
            if (payNow.isLessThan(redeemedVouchers.vouchersTotal.add(redeemedVouchers.leftToPay)) && checkoutModelRequest.payNow == null) {
                minPayNow = redeemedVouchers.vouchersTotal.add(redeemedVouchers.leftToPay)
                payNow = ZERO.add(minPayNow)
            }

            if (payNow.isLessThan(minPayNow) || payNow.isGreaterThan(subTotal)) {
                model.error = new CommonError(message: "Pay now amount is wrong")
                return this
            }

            ccPayment = payNow.subtract(redeemedVouchers.vouchersTotal)

            if (ccPayment.isGreaterThan(ZERO)) {
                if (ccPayment.isGreaterThan(availableCredit)) {
                    ccPayment = ccPayment.subtract(availableCredit)
                    usedCredit = usedCredit.add(availableCredit)
                } else  {
                    usedCredit = usedCredit.add(ccPayment)
                    ccPayment = ZERO
                }
            }
            
            model.amount = new Amount().with { a ->
                a.total = totalAmount.doubleValue()
                a.discount = enrolmentsPrice.totalDiscount.doubleValue()
                a.subTotal = subTotal.doubleValue()
                a.minPayNow = minPayNow.doubleValue()
                a.payNow = payNow.doubleValue()
                a.isEditable = minPayNow.isLessThan(subTotal)
                
                a.voucherPayments = redeemedVouchers.voucherPayments
                a.credit = usedCredit.doubleValue()
                a.ccPayment = ccPayment.doubleValue()
                a.owing = subTotal.subtract(payNow).doubleValue()
                a
            }

            boolean paymentMethodsEnabled = (new IsCorporatePassEnabled(college, context).get() || new IsCreditCardPaymentEnabled(college, context).get())
            
            
            if (ccPayment.isGreaterThan(ZERO) && !paymentMethodsEnabled) {
                model.error  = new CommonError(message: 'No payment method is enabled for this college.')
            }
            
        } else {
            ValidateCorporatePass corporatePassValidate = new ValidateCorporatePass(corporatePass, enrolmentsToProceed.values().flatten().unique() as List<CourseClass>, products.unique())
            if (!corporatePassValidate.validate()) {
                model.error = corporatePassValidate.error
                return this
            }
            model.amount = new Amount().with { a ->
                a.total = totalAmount.doubleValue()
                a.discount = enrolmentsPrice.totalDiscount.doubleValue()
                a.subTotal = subTotal.doubleValue()
                a.owing = 0.0
                a.minPayNow = 0.0
                a
            }
        } 

        this
    }
    
    void processNodes() {
        
        checkoutModelRequest.contactNodes.each { contactNode ->
            ContactNode node = new ContactNode()
            node.contactId = contactNode.contactId

            Contact contact = new GetContact(context, college, contactNode.contactId).get(false)
            contactNode.articles.each { a ->
                node.articles << processArticle(a, contact)
            }

            contactNode.memberships.each { m ->
                node.memberships << processMembership(m, contact)
            }

            if (model.payerId && model.payerId == node.contactId) {
                contactNode.vouchers.each { v ->
                    node.vouchers << processVoucher(v)
                }
            }

            if (contact.student) {
                contactNode.enrolments.each { e ->
                   node.enrolments << processEnrolment(e, contact)
                }

                contactNode.applications.each { a ->
                    node.applications << processApplication(a, contact)
                }

                contactNode.waitingLists.each { w ->
                    node.waitingLists << processWaitingList(w)
                }
                
            } else if (!contactNode.enrolments.empty || !contactNode.applications.empty ) {
                model.error = new CommonError(message: 'Purchase items are not valid')
            }
            
            if (corporatePass) {
                corporatePassNode.vouchers += contactNode.vouchers.findAll { it.selected && it.errors.empty }.each { it.contactId = corporatePass.contact.id.toString() }
                node.vouchers.clear()
            }
            
            model.contactNodes << node
        }
        
        if (corporatePassNode && !corporatePassNode.vouchers.empty) {
            model.contactNodes << corporatePassNode
            model.payerId = corporatePass.contact.id.toString()
        }
    }
    
    
    @CompileStatic(TypeCheckingMode.SKIP)
    Enrolment processEnrolment(Enrolment e, Contact contact) {
        
        e.errors.clear()
        e.warnings.clear()
        
        ProcessClass processClass = new ProcessClass(context, contact, college, e.classId, taxOverridden).process()
        CourseClass courseClass = processClass.persistentClass
        String className = "$courseClass.course.name ($courseClass.course.code - $courseClass.code)"
        
        if (processClass.enrolment == null) {
            e.errors << "Enrolment for $contact.fullName on $className avalible by application".toString()
        } else if (e.selected && !checkAndBookPlace(courseClass)) {
            e.errors << "Unfortunately you just missed out. The class $className was removed from your shopping basket since the last place has now been filled. Please select another class from this course or join the waiting list. <a href=\"/course/$courseClass.course.code\">[ Show course ]</a>".toString()
        } else {
            e.errors += processClass.enrolment.errors
            e.warnings += processClass.enrolment.warnings
        }
        
        if (e.selected && e.errors.empty) {
            ValidateCustomFields validateCustomFields = ValidateCustomFields.valueOf(e.fieldHeadings, processClass.enrolment.fieldHeadings, className, 'Enrolment')
            validateCustomFields.validate()
            if (validateCustomFields.commonError) {
                model.error = validateCustomFields.commonError
            }
            validateCustomFields.fieldErrors.each { fieldError ->
                model.validationErrors.formErrors << fieldError.error
                model.validationErrors.fieldsErrors << fieldError
            }
            e.price = processClass.enrolment.price
            totalAmount = totalAmount.add(e.price.fee != null ? e.price.fee .toMoney() : e.price.feeOverriden.toMoney())
            List<CourseClass> classes = enrolmentsToProceed.get(contact)
            if (classes == null) {
                classes = new ArrayList<CourseClass>()
                enrolmentsToProceed.put(contact, classes)
            }
            classes.add(courseClass)
        } else {
            e.selected = false
        }
        
        return e
    }

    Application processApplication(Application a, Contact contact) {
        a.errors.clear()
        a.warnings.clear()
        
        ProcessClass processClass = new ProcessClass(context, contact, college, a.classId, taxOverridden).process()
        CourseClass courseClass = processClass.persistentClass
        String className = "$courseClass.course.name ($courseClass.course.code - $courseClass.code)"

        if (processClass.application == null) {
            a.errors << "Application for $contact.fullName on $className is wrong".toString()
            a.selected = false
        } else {
            a.errors += processClass.application.errors
            a.warnings += processClass.application.warnings
            if (a.selected && a.errors.empty) {

                ValidateCustomFields validateCustomFields = ValidateCustomFields.valueOf(a.fieldHeadings, processClass.application.fieldHeadings, className, 'Application')
                validateCustomFields.validate()
                if (validateCustomFields.commonError) {
                    model.error = validateCustomFields.commonError
                }
                validateCustomFields.fieldErrors.each { fieldError ->
                    model.validationErrors.formErrors << fieldError.error
                    model.validationErrors.fieldsErrors << fieldError
                }
            } else {
                a.selected = false
            }
        }
        
        return a
    }

    WaitingList processWaitingList(WaitingList w) {
        w.errors.clear()
        w.warnings.clear()
        if (w.selected) {
            ValidateWaitingList validate = new ValidateWaitingList(context, college).validate(w)
            w.errors += validate.errors
            w.warnings += validate.warnings
            if (w.errors.empty) {
                Course course = new GetCourse(context, college, w.courseId).get()
                List<FieldHeading> expectedHeadings = new GetWaitingListFields(course).get()
                ValidateCustomFields validateCustomFields = ValidateCustomFields.valueOf(w.fieldHeadings, expectedHeadings, course.name, 'Waiting List').validate()
                if (validateCustomFields.commonError) {
                    model.error = validateCustomFields.commonError
                }
                validateCustomFields.fieldErrors.each { fieldError ->
                    model.validationErrors.formErrors << fieldError.error
                    model.validationErrors.fieldsErrors << fieldError
                }
            } else {
                w.selected = false
            }

        }
        return w
    }


    @CompileStatic(TypeCheckingMode.SKIP)
    Article processArticle(Article a, Contact contact) {
        a.errors.clear()
        a.warnings.clear()
        if (a.selected) {
            ProcessProduct processProduct = new ProcessProduct(context, contact, college, a.productId, a.quantity, model.payerId, taxOverridden).process()
            if (processProduct.article == null) {
                a.errors << "Purchase is wrong"
            } else {
                a.errors += processProduct.article.errors
                a.warnings += processProduct.article.warnings
                if (a.errors.empty) {
                    products << processProduct.persistentProduct
                    a.price = processProduct.article.price
                    a.total = processProduct.article.total
                    totalAmount = totalAmount.add(a.total.toMoney())
                    totalProductsAmount = totalProductsAmount.add(a.total.toMoney())
                } else {
                    a.selected = false
                }
            }
        }
        a
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    Membership processMembership(Membership m, Contact contact) {
        m.errors.clear()
        m.warnings.clear()
        if (m.selected) {
            ProcessProduct processProduct = new ProcessProduct(context, contact, college, m.productId, 1, model.payerId, taxOverridden).process()
            if (processProduct.membership == null) {
                m.errors << "Purchase is wrong"
            } else {
                m.errors += processProduct.membership.errors
                m.warnings += processProduct.membership.warnings
                if (m.errors.empty) {
                    products << processProduct.persistentProduct
                    m.price = processProduct.membership.price
                    totalAmount = totalAmount.add(m.price.toMoney())
                    totalProductsAmount = totalProductsAmount.add(m.price.toMoney())
                } else {
                    m.selected = false
                }
            }
        }
        return m
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    Voucher processVoucher(Voucher v) {
        v.errors.clear()
        v.warnings.clear()
        if (v.selected) {
            ValidateVoucher validateVoucher = new ValidateVoucher(context, college, model.payerId).validate(v)
            v.errors += validateVoucher.errors
            v.warnings += validateVoucher.warnings
            if (v.errors.empty) {
                products << validateVoucher.persistentProduct
                totalAmount = totalAmount.add(v.total.toMoney())
                totalProductsAmount = totalProductsAmount.add(v.total.toMoney())
            } else {
                v.selected = false 
            }
        }
        return v
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
    
    void processCorporatePass() {
        String corporatePassId = StringUtils.trimToNull(checkoutModelRequest.corporatePassId)
        if (corporatePassId) {
            corporatePass = new GetCorporatePass(context, college, corporatePassId).get()
            taxOverridden = corporatePass.contact.taxOverride
            corporatePassNode = new ContactNode(contactId: corporatePass.contact.id.toString() )
        }
    }
    
    CheckoutModel getModel() {
        return model
    }
    CorporatePass getCorporatePass() {
        return corporatePass
    }
    
}
