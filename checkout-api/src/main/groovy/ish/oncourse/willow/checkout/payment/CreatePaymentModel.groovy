package ish.oncourse.willow.checkout.payment

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
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
import ish.oncourse.model.WaitingList
import ish.oncourse.model.WebSite
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.util.payment.CreditCardValidator
import ish.oncourse.util.payment.PaymentInModel
import ish.oncourse.util.payment.PaymentInModelFromPaymentInBuilder
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.checkout.functions.GetCourseClass
import ish.oncourse.willow.checkout.functions.GetProduct
import ish.oncourse.willow.checkout.persistent.CreateApplication
import ish.oncourse.willow.checkout.persistent.CreateArticle
import ish.oncourse.willow.checkout.persistent.CreateEnrolment
import ish.oncourse.willow.checkout.persistent.CreateInvoice
import ish.oncourse.willow.checkout.persistent.CreateMembership
import ish.oncourse.willow.checkout.persistent.CreateVoucher
import ish.oncourse.willow.checkout.persistent.CreateWaitingList
import ish.oncourse.willow.functions.voucher.GetVoucher
import ish.oncourse.willow.functions.voucher.VoucherRedemptionHelper
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.persistence.CommonPreferenceController
import ish.util.CreditCardUtil
import ish.util.ProductUtil
import ish.util.SecurityUtil
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class CreatePaymentModel {
    
    ObjectContext context
    College college
    WebSite webSite
    PaymentRequest paymentRequest

    List<Application> applications = []
    List<WaitingList> waitingLists = []

    PaymentIn paymentIn
    Invoice mainInvoice
    Contact payer
    List<InvoiceNode> paymentPlan = []
    PaymentInModel model
    CheckoutModel checkoutModel

    final static Logger logger = LoggerFactory.getLogger(CreatePaymentModel.class)


    CreatePaymentModel(ObjectContext context, College college, WebSite webSite, PaymentRequest paymentRequest, CheckoutModel checkoutModel) {
        this.context = context
        this.webSite = this.context.localObject(webSite)
        this.college = this.context.localObject(college)
        this.paymentRequest = paymentRequest
        this.checkoutModel = checkoutModel
        if (checkoutModel.payerId) {
            this.payer = new GetContact(context, college, checkoutModel.payerId).get(false)
        }
    }

    CreatePaymentModel create() {
        processNodes()
        
        if (paymentIn) {
            updateVoucherPayments()
            updateSumm()
            fillCCDetails()
            createModel()
            adjustSortOrder()
        }
        
        this
    }

    private void processNodes() {
        checkoutModel.contactNodes.each { node ->
            Contact contact = new GetContact(context, college, node.contactId).get(false)

            node.enrolments.findAll{it.selected}.each { e ->
                new CreateEnrolment(context, college, e, contact, EnrolmentStatus.IN_TRANSACTION,  ConfirmationStatus.DO_NOT_SEND, payer.taxOverride, { Enrolment enrolment, InvoiceLine il ->
                    if (enrolment.courseClass.paymentPlanLines.empty) {
                        il.invoice = getInvoice()
                    } else {
                        Invoice paymentPlanInvoice = createInvoice()
                        il.invoice = paymentPlanInvoice

                        List<InvoiceDueDate> selectedDueDates = PaymentPlanBuilder.valueOf(enrolment).build().selectedDueDates
                        paymentPlan << InvoiceNode.valueOf(il.invoice, paymentPlanInvoice.paymentInLines[0], il, enrolment, selectedDueDates, null)
                    }}).create()
            }

            node.applications.findAll{it.selected}.each { a ->
                applications << new CreateApplication(context, college, a, contact).create()
            }
            node.articles.findAll{it.selected}.each { a ->
                new CreateArticle(context, college, a, contact, getInvoice(), ProductStatus.NEW, payer.taxOverride).create()
            }
            node.memberships.findAll{it.selected}.each { m ->
                new CreateMembership(context, college, m, contact, getInvoice(), ProductStatus.NEW, payer.taxOverride).create()
            }
            node.vouchers.findAll{it.selected}.each { v ->
                new CreateVoucher(context, college, v, contact, getInvoice(), ProductStatus.NEW, ConfirmationStatus.DO_NOT_SEND).create()
            }
            node.waitingLists.findAll { it.selected }.each { w ->
                waitingLists << new CreateWaitingList(context, college, w, contact).create()
            }
        }
    }
    
    @CompileStatic(TypeCheckingMode.SKIP)
    private void updateVoucherPayments() {
        
        VoucherRedemptionHelper voucherRedemptionHelper = new VoucherRedemptionHelper(context, college, payer)

        List<Voucher> vouchers = checkoutModel.amount.voucherPayments
                .findAll { it.amount.toMoney().isGreaterThan(Money.ZERO) }
                .collect { new GetVoucher(context, college, it.redeemVoucherId).get() }
                
        for (Voucher voucher : vouchers) {
            voucherRedemptionHelper.addVoucher(voucher, voucher.valueRemaining)
        }

        if (mainInvoice) {
            voucherRedemptionHelper.addInvoiceLines(mainInvoice.invoiceLines)
        }
        
        for (InvoiceNode node : paymentPlan) {
            voucherRedemptionHelper.addInvoiceLines(node.invoice.invoiceLines)
            voucherRedemptionHelper.addPaymentPlan(node.invoice, node.paymentAmount)
        }
        
        voucherRedemptionHelper.createPaymentsForVouchers()
    }
    
    private void updateSumm() {
        if (paymentIn)  {
            
            if (mainInvoice) {

                mainInvoice.paymentInLines.find { it.paymentIn == paymentIn }.amount = calculatePaymentAmountForInvoice(mainInvoice, null)
                
                UpdateInvoiceAmount.valueOf(mainInvoice, null).update()
                mainInvoice.updateAmountOwing()
                adjustDueDate()
            }
            
            paymentPlan.each { node ->
                node.paymentInLine.amount = calculatePaymentAmountForInvoice(node.invoice, node.paymentAmount).min(node.paymentAmount)
                UpdateInvoiceAmount.valueOf(node.invoice, null).update()
                node.invoice.updateAmountOwing()
            }

            paymentIn.paymentInLines.each { paymentIn.amount = paymentIn.amount.add(it.amount)}

            allocateExtraMoney()

            if (paymentIn.amount == Money.ZERO) {
                paymentIn.type = PaymentType.INTERNAL
            }
        }
    }

    private Money calculatePaymentAmountForInvoice(Invoice invoice, Money payNow) {
        
        Money voucherPaymentsTotal = Money.ZERO
        invoice.paymentInLines.findAll { it.paymentIn.type == PaymentType.VOUCHER }.each { voucherPaymentsTotal = voucherPaymentsTotal.add(it.amount) }

        Money invoiceTotal = Money.ZERO
        if (payNow) {
            invoiceTotal = invoiceTotal.add(payNow)
        } else {
            invoice.invoiceLines.each { invoiceTotal = invoiceTotal.add(it.finalPriceToPayIncTax) }
        }

        invoiceTotal.subtract(voucherPaymentsTotal).max(Money.ZERO)
    }
    
    @CompileStatic(TypeCheckingMode.SKIP)
    private allocateExtraMoney() {
        Money offeredAmount = paymentRequest.payNow.toMoney()
        Money extraAmount = offeredAmount.subtract(paymentIn.amount)

        if (checkoutModel.amount.isEditable && offeredAmount.isGreaterThan(paymentIn.amount)) {
            for(InvoiceNode node : paymentPlan) {
                Money leftToPay = node.invoice.amountOwing.subtract(node.paymentAmount)
                Money amountToAdd = leftToPay.min(extraAmount)
                node.paymentInLine.amount = node.paymentInLine.amount.add(amountToAdd)
                paymentIn.amount = paymentIn.amount.add(amountToAdd)
                extraAmount = extraAmount.subtract(amountToAdd)
                
                if (!extraAmount.isGreaterThan(Money.ZERO)) {
                    break
                }
            }
        }
        
        if (extraAmount.isGreaterThan(Money.ZERO)) {
            context.rollbackChanges()
            logger.info("Payment amount is wrong, Extra amount supplied, offered amount: $offeredAmount, checkout model: $checkoutModel, paymen request: $paymentRequest")
            throw new IllegalStateException('Payment amount is wrong, Extra amount supplied')
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
        payment.amount = Money.ZERO
        payment.confirmationStatus = ConfirmationStatus.DO_NOT_SEND

        payment
    }

    private Invoice createInvoice() {
        new CreateInvoice(context, college, webSite, payer)
                .forPaymentModel(getPayment(), paymentRequest.sessionId)
    }

    private void  adjustDueDate() {
        Integer defaultTerms = new GetPreference(college, CommonPreferenceController.ACCOUNT_INVOICE_TERMS, context).integerValue
        Integer contactTerms = payer.invoiceTerms
        Date dueDate = GetInvoiceDueDate.valueOf(defaultTerms, contactTerms).get()
        mainInvoice.dateDue = dueDate
    }

    private void adjustSortOrder() {
        adjustSortOrder(mainInvoice)
        for (InvoiceNode node : paymentPlan) {
            adjustSortOrder(node.invoice)
        }
    }

    private void adjustSortOrder(Invoice invoice) {
        if (invoice) {
            List<InvoiceLine> invoiceLines = invoice.invoiceLines
            for (int i = 0; i < invoiceLines.size(); i++) {
                InvoiceLine invoiceLine = invoiceLines[i]
                invoiceLine.sortOrder = i
            }
        }
    }
    
    boolean isNoPayment() {
       return paymentIn == null && mainInvoice == null && paymentPlan.empty && (!applications.empty || !waitingLists.empty)
    }

    private void createModel() {
        model = PaymentInModelFromPaymentInBuilder.valueOf(paymentIn).build().model
    }
    
    private void fillCCDetails() {
        paymentIn.creditCardType = CreditCardValidator.determineCreditCardType(paymentRequest.creditCardNumber)
        paymentIn.creditCardCVV = CreditCardUtil.obfuscateCVVNumber(paymentRequest.creditCardCvv)
        paymentIn.creditCardNumber = CreditCardUtil.obfuscateCCNumber(paymentRequest.creditCardNumber)
        paymentIn.creditCardName = paymentRequest.creditCardName
        paymentIn.creditCardExpiry = paymentRequest.expiryMonth + "/" + paymentRequest.expiryYear
    }
}
