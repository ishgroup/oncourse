package ish.oncourse.willow.checkout.payment

import ish.common.GetInvoiceDueDate
import ish.common.types.ApplicationStatus
import ish.common.types.ConfirmationStatus
import ish.common.types.EnrolmentStatus
import ish.common.types.PaymentSource
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.enrol.checkout.model.InvoiceNode
import ish.oncourse.enrol.checkout.model.PaymentPlanBuilder
import ish.oncourse.enrol.checkout.model.UpdateInvoiceAmount
import ish.oncourse.model.Application
import ish.oncourse.model.Article
import ish.oncourse.model.ArticleProduct
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Enrolment
import ish.oncourse.model.Invoice
import ish.oncourse.model.InvoiceDueDate
import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.Membership
import ish.oncourse.model.MembershipProduct
import ish.oncourse.model.PaymentIn
import ish.oncourse.model.PaymentInLine
import ish.oncourse.model.Voucher
import ish.oncourse.model.VoucherProduct
import ish.oncourse.model.WebSite
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.checkout.functions.GetCourseClass
import ish.oncourse.willow.checkout.functions.GetProduct
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.persistence.CommonPreferenceController
import ish.util.ProductUtil
import ish.util.SecurityUtil
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.time.DateUtils


class CreatePaymentModel {


    ObjectContext context
    College college
    WebSite webSite
    PaymentRequest paymentRequest

    PaymentIn paymentIn
    Invoice mainInvoice
    Contact payer
    List<InvoiceNode> paymentPlan = []

    CreatePaymentModel(ObjectContext context, College college, WebSite webSite, PaymentRequest paymentRequest) {
        this.context = context
        this.webSite = this.context.localObject(webSite)
        this.college = this.context.localObject(college)
        this.paymentRequest = paymentRequest
        payer = new GetContact(context, college, paymentRequest.checkoutModel.payerId).get()
    }

    CreatePaymentModel create() {
        paymentRequest.checkoutModel.purchaseItemsList.each { purchaseItems ->
            Contact contact = new GetContact(context, college, purchaseItems.contactId).get()
            
            purchaseItems.enrolments.each { e ->
                createEnrolment(e, contact)
            }
            purchaseItems.applications.each { a ->
                createApplication(a, contact)
            }
            purchaseItems.articles.each { a ->
                createArticle(a,contact)
            }
            purchaseItems.memberships.each { m ->
                createMembership(m,contact)
            }
            purchaseItems.vouchers.each { v ->
                createVoucher(v,contact)
            }
        }

        updateSumm()
        adjustSortOrder()
        this
    }


    void createVoucher(ish.oncourse.willow.model.checkout.Voucher v, Contact contact) {
        VoucherProduct voucherProduct = new GetProduct(context, college, v.productId).get() as VoucherProduct
        Voucher voucher = context.newObject(Voucher)
        voucher.code = SecurityUtil.generateRandomPassword(Voucher.VOUCHER_CODE_LENGTH)
        voucher.college = college

        voucher.source = PaymentSource.SOURCE_WEB
        voucher.status = ProductStatus.NEW
        voucher.product = voucherProduct
        voucher.redeemedCoursesCount = 0

        voucher.expiryDate = ProductUtil.calculateExpiryDate(new Date(), voucherProduct.expiryType, voucherProduct.expiryDays)

        InvoiceLine invoiceLine
        Money price = null
        if (voucherProduct.redemptionCourses.empty && voucherProduct.priceExTax == null) {
            price = new Money(v.value)
            voucher.redemptionValue = price
            voucher.valueOnPurchase = price
        } else if (voucherProduct.priceExTax != null) {
            voucher.redemptionValue = voucherProduct.value
            voucher.valueOnPurchase = voucherProduct.value
            price = voucherProduct.priceExTax
        }
        invoiceLine = new ProductItemInvoiceLine(voucher, contact, price?: voucherProduct.priceExTax).create()
        invoiceLine.invoice = getInvoice()
    }
    
    void createMembership(ish.oncourse.willow.model.checkout.Membership m, Contact contact) {
        MembershipProduct mp = new GetProduct(context, college, m.productId).get() as MembershipProduct
        Membership membership = context.newObject(Membership)
        membership.college = college
        membership.contact = contact
        membership.expiryDate = ProductUtil.calculateExpiryDate(new Date(), mp.expiryType, mp.expiryDays)
        membership.product = mp
        membership.status = ProductStatus.NEW
        InvoiceLine invoiceLine = new ProductItemInvoiceLine(membership, contact, membership.product.priceExTax).create()
        invoiceLine.invoice = getInvoice()
    }
    
    void createArticle(ish.oncourse.willow.model.checkout.Article a, Contact contact) {
        ArticleProduct ap = new GetProduct(context, college, a.productId).get() as ArticleProduct
        Article article = context.newObject(Article)
        article.college = college
        article.contact = contact
        article.setProduct(ap)
        article.status = ProductStatus.NEW
        InvoiceLine invoiceLine = new ProductItemInvoiceLine(article, contact, article.product.priceExTax).create()
        invoiceLine.invoice = getInvoice()
    }

    void createApplication(ish.oncourse.willow.model.checkout.Application a, Contact contact) {
        CourseClass courseClass = new GetCourseClass(context, college, a.contactId).get()
        Application application = context.newObject(Application)
        application.college = college
        application.student = contact.student
        application.course = courseClass.course
        application.status = ApplicationStatus.NEW
        application.source = PaymentSource.SOURCE_WEB
        application.confirmationStatus = ConfirmationStatus.NOT_SENT
    }
    
    
    void createEnrolment(ish.oncourse.willow.model.checkout.Enrolment e, Contact contact) {
        CourseClass courseClass = new GetCourseClass(context, college, e.contactId).get()
        Enrolment enrolment = context.newObject(Enrolment)
        enrolment.courseClass =  courseClass
        enrolment.student = contact.student
        enrolment.status = EnrolmentStatus.IN_TRANSACTION
        enrolment.source = PaymentSource.SOURCE_WEB
        InvoiceLine invoiceLine = new EnrolmentInvoiceLine(enrolment, e.price).create()
        invoiceLine.setEnrolment(enrolment)

        if (courseClass.paymentPlanLines.empty) {
            invoiceLine.invoice = getInvoice()

        } else {
            Invoice paymentPlanInvoice = createInvoice()
            invoiceLine.invoice = paymentPlanInvoice

            List<InvoiceDueDate> selectedDueDates = PaymentPlanBuilder.valueOf(enrolment).build().selectedDueDates
            paymentPlan << InvoiceNode.valueOf(invoice, paymentPlanInvoice.paymentInLines[0], invoiceLine, enrolment, selectedDueDates, null)
        }
    }
    
    private void updateSumm() {
        if (!paymentIn)  {
            if (!mainInvoice) {
                mainInvoice.invoiceLines.each { il ->
                    paymentIn.amount = payment.amount.add(il.finalPriceToPayIncTax)
                }
                invoice.paymentInLines[0].amount = paymentIn.amount
                UpdateInvoiceAmount.valueOf(mainInvoice, null).update()
                adjustDueDate()
            }
            paymentPlan.each { node ->
                paymentIn.amount = paymentIn.amount.add(node.paymentAmount)
                node.paymentInLine.amount = node.paymentAmount
                UpdateInvoiceAmount.valueOf(node.invoice, null).update()
            }
        }
    }

    private PaymentIn getPayment() {
        if (!paymentIn) {
            paymentIn = createPayment()
        }
        paymentIn
    }

    private Invoice getInvoice() {
        if (!mainInvoice) {
            mainInvoice = createInvoice()
        }
        mainInvoice
    }

    private PaymentIn createPayment() {
        PaymentIn payment = context.newObject(PaymentIn)
        payment.status = PaymentStatus.IN_TRANSACTION
        payment.source = PaymentSource.SOURCE_WEB
        payment.type = PaymentType.CREDIT_CARD
        payment.sessionId = paymentRequest.sessionId
        payment.college = college
        payment.setContact(payer)
        payment
    }

    private Invoice createInvoice() {
        Invoice invoice = context.newObject(Invoice)
        // fill the invoice with default values
        invoice.invoiceDate = DateUtils.setHours(new Date(), 12)
        invoice.amountOwing = Money.ZERO
        invoice.dateDue = new Date()
        invoice.source = PaymentSource.SOURCE_WEB
        invoice.college = college
        invoice.contact = payer
        invoice.webSite = webSite
        invoice.sessionId = paymentRequest.sessionId


        PaymentInLine paymentInLine = context.newObject(PaymentInLine)
        paymentInLine.invoice = invoice
        paymentInLine.paymentIn = getPayment()
        paymentInLine.amount = Money.ZERO
        paymentInLine.college = college
        
        invoice
    }

    private void  adjustDueDate() {
        Integer defaultTerms = new GetPreference(college, CommonPreferenceController.ACCOUNT_INVOICE_TERMS, context).integer
        Integer contactTerms = payer.invoiceTerms
        Date dueDate = GetInvoiceDueDate.valueOf(defaultTerms, contactTerms).get();
        mainInvoice.dateDue = dueDate
    }

    private void adjustSortOrder() {
        adjustSortOrder(getInvoice())
        for (InvoiceNode node : paymentPlan) {
            adjustSortOrder(node.invoice)
        }
    }

    private void adjustSortOrder(Invoice invoice) {
        List<InvoiceLine> invoiceLines = invoice.invoiceLines
        for (int i = 0; i < invoiceLines.size(); i++) {
            InvoiceLine invoiceLine = invoiceLines[i]
            invoiceLine.sortOrder = i
        }
    }
    
}
