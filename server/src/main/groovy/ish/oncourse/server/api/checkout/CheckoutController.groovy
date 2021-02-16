/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.checkout

import groovy.transform.CompileStatic
import ish.common.types.OutcomeStatus
import ish.oncourse.server.api.dao.EntityRelationDao
import ish.oncourse.server.api.dao.ModuleDao
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.EntityRelation
import ish.oncourse.server.cayenne.FundingSource
import ish.oncourse.server.cayenne.Module

import static ish.common.types.ConfirmationStatus.DO_NOT_SEND
import static ish.common.types.ConfirmationStatus.NOT_SENT
import ish.common.types.EnrolmentStatus
import ish.common.types.ExpiryType
import static ish.common.types.PaymentSource.SOURCE_ONCOURSE
import ish.common.types.PaymentStatus
import static ish.common.types.PaymentType.CREDIT_CARD
import ish.common.types.ProductStatus
import static ish.common.types.ProductStatus.ACTIVE
import ish.common.types.StudyReason
import ish.common.types.VoucherPaymentStatus
import ish.math.Money
import ish.oncourse.server.CayenneService
import ish.oncourse.server.api.dao.FundingSourceDao
import ish.oncourse.server.api.function.CayenneFunctions
import ish.oncourse.server.api.service.ArticleProductApiService
import ish.oncourse.server.api.service.ContactApiService
import ish.oncourse.server.api.service.CourseClassApiService
import ish.oncourse.server.api.service.InvoiceApiService
import ish.oncourse.server.api.service.MembershipProductApiService
import ish.oncourse.server.api.service.VoucherProductApiService
import static ish.oncourse.server.api.v1.function.TaxFunctions.nonSupplyTax
import ish.oncourse.server.api.v1.model.CheckoutArticleDTO
import ish.oncourse.server.api.v1.model.CheckoutEnrolmentDTO
import ish.oncourse.server.api.v1.model.CheckoutMembershipDTO
import ish.oncourse.server.api.v1.model.CheckoutModelDTO
import ish.oncourse.server.api.v1.model.CheckoutValidationErrorDTO
import ish.oncourse.server.api.v1.model.CheckoutVoucherDTO
import ish.oncourse.server.api.v1.model.InvoiceDTO
import ish.oncourse.server.api.v1.model.SaleTypeDTO
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.Article
import ish.oncourse.server.cayenne.ArticleProduct
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassPaymentPlanLine
import ish.oncourse.server.cayenne.Discount
import ish.oncourse.server.cayenne.DiscountCourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceDueDate
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.InvoiceLineDiscount
import ish.oncourse.server.cayenne.Membership
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.PaymentMethod
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.Tax
import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.server.cayenne.VoucherPaymentIn
import ish.oncourse.server.cayenne.VoucherProduct
import ish.oncourse.server.lifecycle.UpdateAttendancesAndOutcomes
import ish.oncourse.server.users.SystemUserService
import ish.util.AccountUtil
import ish.util.DiscountUtils
import ish.util.LocalDateUtils
import ish.util.PaymentMethodUtil
import ish.util.ProductUtil
import ish.util.SecurityUtil
import static java.math.BigDecimal.ONE
import static java.math.BigDecimal.ZERO
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.LocalDate

import static org.apache.commons.lang3.StringUtils.trimToNull

@CompileStatic
class CheckoutController {

    static final Logger logger = LogManager.getLogger()

    private CayenneService cayenneService
    private SystemUserService systemUserService
    private ContactApiService contactApiService
    private InvoiceApiService invoiceApiService
    private CourseClassApiService courseClassApiService
    private MembershipProductApiService membershipApiService
    private VoucherProductApiService voucherApiService
    private ArticleProductApiService articleApiService
    private FundingSourceDao fundingSourceDao
    private ModuleDao moduleDao

    private CheckoutModelDTO checkout

    private Tax taxOverride
    private Account prepaidFeesAccount
    private Contact payer
    private ObjectContext context
    private List<CheckoutValidationErrorDTO> result = []
    private Boolean multyPurchase
    private Map<Long, Integer> currentEnrolments = [:]
    private List<CheckoutEnrolmentDTO> processedEnrolments = []

    private Invoice invoice
    private PaymentIn paymentIn

    CheckoutController(CayenneService cayenneService,
                       SystemUserService systemUserService,
                       ContactApiService contactApiService,
                       InvoiceApiService invoiceApiService,
                       CourseClassApiService courseClassApiService,
                       MembershipProductApiService membershipApiService,
                       VoucherProductApiService voucherApiService,
                       ArticleProductApiService articleApiService,
                       FundingSourceDao fundingSourceDao,
                       ModuleDao moduleDao) {
        this.cayenneService = cayenneService
        this.systemUserService = systemUserService
        this.contactApiService = contactApiService
        this.invoiceApiService = invoiceApiService
        this.courseClassApiService = courseClassApiService
        this.membershipApiService = membershipApiService
        this.voucherApiService = voucherApiService
        this.articleApiService = articleApiService
        this.fundingSourceDao = fundingSourceDao
        this.moduleDao = moduleDao
    }

    Checkout createCheckout(CheckoutModelDTO checkout) {
        this.checkout = checkout
        this.multyPurchase = (checkout.contactNodes.sum { it.memberships.size() + it.vouchers.size() + it.products.size() + it.enrolments.size() } as int) > 1
        this.context = cayenneService.newContext
        this.prepaidFeesAccount = AccountUtil.getDefaultPrepaidFeesAccount(context, Account)
        this.payer = contactApiService.getEntityAndValidateExistence(context, checkout.payerId)
        this.taxOverride = payer.taxOverride

        initInvoice()
        initPayment()

        checkout.contactNodes.each { node ->

            Contact contact = contactApiService.getEntityAndValidateExistence(context, node.contactId)
            Boolean confirmation = node.sendConfirmation

            node.products.each { dto -> processArticle(dto, contact, confirmation) }
            node.memberships.each { dto -> processMembership(dto, contact, confirmation) }

            if (contact != payer && !node.vouchers.empty) {
                result << new CheckoutValidationErrorDTO(nodeId: contact.id, error: "Voucher purchase available for payer only")
            } else {
                node.vouchers.each { dto -> processVoucher(dto, confirmation) }
            }

            if (contact.isCompany && !node.enrolments.empty)  {
                result << new CheckoutValidationErrorDTO(nodeId: contact.id, error: "Company can not be enrolled")
            } else {
                node.enrolments.each { dto -> processEnrolment(dto, contact, confirmation) }
            }
            if (node.fundingInvoices) {
                node.fundingInvoices.each { dto -> createFundingInvoice(dto) }
            }
        }

        processRedeemedVouchers()
        updateInvoice()
        updatePayment()

        return new Checkout(paymentIn: paymentIn, invoice: invoice, errors: result, context: context)

    }

    private void processEnrolment(CheckoutEnrolmentDTO dto, Contact contact, Boolean sendEmail) {

        CourseClass courseClass = courseClassApiService.getEntityAndValidateExistence(context, dto.classId)
        Enrolment enrolment = createEnrolment(contact, courseClass, sendEmail, dto.studyReason?.dbType?:StudyReason.STUDY_REASON_NOT_STATED)

        if (dto.appliedDiscountId) {
            applyDiscount(dto.totalOverride, enrolment.originalInvoiceLine, courseClass, CayenneFunctions.getRecordById(context, Discount, dto.appliedDiscountId))
        }

        if (courseClass.isCancelled) {
            result << new CheckoutValidationErrorDTO(nodeId: contact.id, itemId: courseClass.id, itemType: SaleTypeDTO.CLASS, error: "Class is cancelled")
        }

        if (!courseClass.isActive) {
            result << new CheckoutValidationErrorDTO(nodeId: contact.id, itemId: courseClass.id, itemType: SaleTypeDTO.CLASS, error: "Class is disabled")
        }

        if (courseClass.course.isTraineeship) {
            if ((courseClass.enrolments.size() != 1)) {
                result << new CheckoutValidationErrorDTO(nodeId: contact.id, itemId: courseClass.id, itemType: SaleTypeDTO.CLASS, error: "No more than one enrolment available to traineeship.")
            }
            if (currentEnrolments[dto.classId]) {
                result << new CheckoutValidationErrorDTO(nodeId: contact.id, itemId: courseClass.id, itemType: SaleTypeDTO.CLASS, error: "Only one person can be enrolled to traineeship.")
            }
        }

        if (courseClass.minStudentAge != null || courseClass.maxStudentAge != null) {
            Integer studentAge = contact.age

            if (studentAge == null) {
                result << new CheckoutValidationErrorDTO(nodeId: contact.id, itemId: dto.classId, itemType: SaleTypeDTO.CLASS, error: "Age restrictions apply to enrolments in this class. $contact.fullName requires a date of birth")
            } else {
                Integer minAge = courseClass.minStudentAge
                Integer maxAge = courseClass.maxStudentAge

                if ((minAge != null && studentAge < minAge) || (maxAge != null && studentAge > maxAge)) {
                    result << new CheckoutValidationErrorDTO(nodeId: contact.id, itemId: dto.classId, itemType: SaleTypeDTO.CLASS, error: "$contact.fullName is unable to enrol in this class. They do not meet the age requirements")
                }
            }
        }

        if (enrolment.student.isEnrolled(courseClass)) {
            result << new CheckoutValidationErrorDTO(nodeId: contact.id, itemId: courseClass.id, itemType: SaleTypeDTO.CLASS, error: "$contact.fullName is already enrolled")
        } else if (courseClass.placesLeft < 0) {
            result << new CheckoutValidationErrorDTO(nodeId: contact.id, itemId: courseClass.id, itemType: SaleTypeDTO.CLASS, error: "No places available for class $courseClass.uniqueCode")
        } else {
            //TODO: make willow side validation
        }

        List<EntityRelation> relations = EntityRelationDao.getRelatedToOrEqual(context, Course.simpleName, courseClass.course.id)

        relations.findAll { Module.simpleName == it.toEntityIdentifier }.each { relation ->
            Module module = moduleDao.getById(context, relation.toEntityAngelId)

            if (!(module in (contact.student?.enrolments?.collect { it.outcomes.findAll { OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE.contains(it.status) }*.module } as List<Module>))) {
                if (!(module in (contact.student?.priorLearnings?.collect { it.outcomes.findAll { OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE.contains(it.status) }*.module } as List<Module>))) {
                    result << new CheckoutValidationErrorDTO(error: "You don't have necessary outcomes for that Course")
                }
            }
        }

        currentEnrolments[dto.classId] = ++(currentEnrolments[dto.classId]?:0)

    }

    private void processMembership(CheckoutMembershipDTO dto, Contact contact, Boolean sendEmail) {

        MembershipProduct membershipProduct = membershipApiService.getEntityAndValidateExistence(context, dto.productId)
        Membership membership = context.newObject(Membership)
        InvoiceLine invoiceLine = context.newObject(InvoiceLine)
        invoiceLine.invoice = invoice
        membership.invoiceLine = invoiceLine
        membership.product = membershipProduct
        membership.contact = contact
        membership.status = ACTIVE
        membership.confirmationStatus = sendEmail ? NOT_SENT : DO_NOT_SEND

        if (ExpiryType.LIFETIME == membershipProduct.expiryType) {
            membership.expiryDate = ProductUtil.calculateExpiryDate(new Date(), membershipProduct.expiryType, membershipProduct.expiryDays)
        } else {
            membership.expiryDate = LocalDateUtils.valueToDate(dto.validTo)
        }

        invoiceLine.tax = taxOverride ?: membershipProduct.tax
        invoiceLine.quantity = ONE
        invoiceLine.discountEachExTax = Money.ZERO
        invoiceLine.priceEachExTax = membershipProduct.priceExTax
        invoiceLine.account = membershipProduct.incomeAccount
        invoiceLine.prepaidFeesAccount = prepaidFeesAccount
        invoiceLine.title = GetInvoiceLineTitle.valueOf(membership).get()
        invoiceLine.description = GetInvoiceLineDescription.valueOf(membership).get()
    }

    private void processArticle(CheckoutArticleDTO dto, Contact contact, Boolean sendEmail) {

        ArticleProduct articleProduct = articleApiService.getEntityAndValidateExistence(context, dto.productId)

        InvoiceLine invoiceLine = context.newObject(InvoiceLine)
        invoiceLine.invoice = invoice
        Article article = null
        if (dto.quantity ==  null || dto.quantity < 1) {
            result << new CheckoutValidationErrorDTO(nodeId: contact.id, itemId: articleProduct.id, itemType: SaleTypeDTO.PRODUCT, error: "Quantity is required")
            return
        }

        (1..dto.quantity).each {
            article = context.newObject(Article)
            article.product = articleProduct
            article.status = ACTIVE
            article.contact = contact
            article.invoiceLine = invoiceLine
            article.confirmationStatus = sendEmail ? NOT_SENT : DO_NOT_SEND
        }

        invoiceLine.tax = taxOverride ?: articleProduct.tax
        invoiceLine.priceEachExTax = articleProduct.priceExTax
        invoiceLine.discountEachExTax = Money.ZERO
        invoiceLine.quantity = dto.quantity
        invoiceLine.account = articleProduct.incomeAccount
        invoiceLine.prepaidFeesAccount = prepaidFeesAccount

        invoiceLine.title = GetInvoiceLineTitle.valueOf(article).get()
        invoiceLine.description = GetInvoiceLineDescription.valueOf(article).get()
        if (dto.quantity >1) {
            invoiceLine.description += ", quantity:$dto.quantity"
        }
    }

    private void processVoucher(CheckoutVoucherDTO dto, Boolean sendEmail) {

        VoucherProduct product = voucherApiService.getEntityAndValidateExistence(context, dto.productId)
        InvoiceLine invoiceLine = context.newObject(InvoiceLine)
        invoiceLine.invoice = invoice
        Voucher voucher = context.newObject(Voucher)
        voucher.product = product
        voucher.status = ACTIVE
        voucher.code = SecurityUtil.generateVoucherCode()
        voucher.source = SOURCE_ONCOURSE
        voucher.invoiceLine = invoiceLine
        voucher.expiryDate = LocalDateUtils.valueToDate(dto.validTo)
        voucher.confirmationStatus =  sendEmail ? NOT_SENT : DO_NOT_SEND

        if (dto.restrictToPayer) {
           voucher.redeemableBy = payer
        }
        //chech is this a product with predefined value on purchase
        if (product.priceExTax != null) {
            //if product have specified value we should set it
            voucher.redemptionValue = product.value ?: product.priceExTax
        } else {
            //else set the initial value as $0
            voucher.redemptionValue = new Money(dto.value)
        }
        voucher.valueOnPurchase = voucher.redemptionValue
        voucher.redeemedCourseCount = 0

        invoiceLine.quantity = ONE
        invoiceLine.priceEachExTax = product.priceExTax != null ? product.priceExTax : voucher.redemptionValue
        invoiceLine.discountEachExTax = Money.ZERO
        invoiceLine.tax = nonSupplyTax(context)
        invoiceLine.account = product.liabilityAccount

        invoiceLine.title = GetInvoiceLineTitle.valueOf(voucher).get()
        invoiceLine.description = GetInvoiceLineDescription.valueOf(voucher).get()

    }

    private void initInvoice() {
        if (checkout.contactNodes.any {node -> !node.enrolments.empty || !node.products.empty  || !node.vouchers.empty || !node.memberships.empty }) {
            invoice = context.newObject(Invoice)
            invoice.amountOwing = Money.ZERO
            invoice.source  = SOURCE_ONCOURSE
            invoice.confirmationStatus = checkout.sendInvoice ? NOT_SENT : DO_NOT_SEND
            invoice.contact = payer
            if (trimToNull(payer.getAddress()) != null) {
                invoice.setBillToAddress(payer.getAddress())
            }
            invoice.dateDue = LocalDate.now()
            invoice.invoiceDate = LocalDate.now()
            invoice.allowAutoPay = checkout.allowAutoPay
            invoice.createdByUser = context.localObject(systemUserService.currentUser)
            invoice.customerReference = checkout.invoiceCustomerReference
            invoice.publicNotes = checkout.invoicePublicNotes
        }
    }

    private void updateInvoice() {
        if (invoice == null) {
            return
        }
        invoice.invoiceLines.each {
            // do not update tax for discounted lines since it is already has rounding
            if (it.invoiceLineDiscounts.empty) {
                it.recalculateTaxEach()
            }
            it.recalculatePrepaidFeesRemaining()

        }

        invoice.updateAmountOwing()

        if (!checkout.paymentPlans.empty) {
            if (checkout.payForThisInvoice && checkout.payForThisInvoice > 0) {
                createDueDate(new Money(checkout.payForThisInvoice),LocalDate.now())
            }

            checkout.paymentPlans.each {
                createDueDate(new Money(it.amount),  it.date)
            }
        } else if (checkout.invoiceDueDate)  {
            invoice.dateDue = checkout.invoiceDueDate
        }
    }

    private void initPayment() {
        if (checkout.paymentMethodId == null && !checkout.payWithSavedCard &&
                (checkout.previousInvoices.isEmpty() || !checkout.previousInvoices.any { id, amount -> Money.valueOf(amount) != Money.ZERO })) {
            return
        }

        paymentIn = context.newObject(PaymentIn)
        paymentIn.source = SOURCE_ONCOURSE
        paymentIn.payer = payer
        paymentIn.createdBy = context.localObject(systemUserService.currentUser)
        paymentIn.administrationCentre = paymentIn.createdBy.defaultAdministrationCentre
        paymentIn.paymentDate = LocalDate.now()

        paymentIn.amount = new Money(checkout.payNow)

        PaymentMethod method

        if (paymentIn.amount > ZERO) {
            if (checkout.paymentMethodId != null) {
                method =  SelectById.query(PaymentMethod, checkout.paymentMethodId).selectOne(context)
            } else if (checkout.payWithSavedCard) {
                method = PaymentMethodUtil.getRealTimeCreditCardPaymentMethod(context, PaymentMethod)
            } else {
                logger.error('Payment method must be set: {}', checkout.toString())
                throw new IllegalStateException('Payment method must be set')
            }
        } else {
            method = PaymentMethodUtil.getCONTRAPaymentMethods(context, PaymentMethod)
        }

        paymentIn.paymentMethod = method

        if (CREDIT_CARD != paymentIn.paymentMethod.type) {
            paymentIn.paymentDate = checkout.paymentDate?:LocalDate.now()
        }
        paymentIn.account = paymentIn.paymentMethod.account
        paymentIn.undepositedFundsAccount = paymentIn.paymentMethod.undepositedFundsAccount


        if (invoice) {
            PaymentInLine line = context.newObject(PaymentInLine)
            line.payment = paymentIn
            line.invoice = invoice
            line.amount =  Money.ZERO
        }


        if (CREDIT_CARD == paymentIn.paymentMethod.type) {
            paymentIn.status =PaymentStatus.IN_TRANSACTION
            //send only when payment success
            paymentIn.confirmationStatus = DO_NOT_SEND
        } else {
            paymentIn.status = PaymentStatus.SUCCESS
            paymentIn.confirmationStatus = checkout.sendInvoice ? NOT_SENT : DO_NOT_SEND
        }
    }

    private void updatePayment() {

        if (paymentIn == null) {
            return
        }

        if (invoice) {
            Money payForThisInvoice = new Money(checkout.payForThisInvoice)
            Money payByVouchers = getAmountPaidByVouchers(invoice)
            Money finalPayAmount = payForThisInvoice.subtract(payByVouchers)
            if (finalPayAmount.isGreaterThan(invoice.amountOwing)) {
                result << new CheckoutValidationErrorDTO(propertyName: "payForThisInvoice", error: "Payment amount allocated to current invoice is bigger than invoice total")
            } else {
                invoice.paymentLines[0].amount = finalPayAmount
                invoice.updateAmountOwing()
                invoice.updateDateDue()
                invoice.updateOverdue()
            }
        }

        checkout.previousInvoices.each { id, amount ->
            Money payAmount = new Money(amount)
            if (payAmount != Money.ZERO) {
                Invoice previousInvoice = invoiceApiService.getEntityAndValidateExistence(context, Long.valueOf(id))
                previousInvoice.updateAmountOwing()
                Money payByVouchers = getAmountPaidByVouchers(previousInvoice)
                Money owing = previousInvoice.amountOwing.add(payByVouchers)

                if (payAmount.abs().isGreaterThan(owing.abs())) {
                    result << new CheckoutValidationErrorDTO(propertyName: "previousInvoices",  itemId: Long.valueOf(id),  error: "Payment amount allocated to invoice #$previousInvoice.invoiceNumber bigger than invoice outstanding amount")
                } else {
                    PaymentInLine line = context.newObject(PaymentInLine)
                    line.payment = paymentIn
                    line.invoice = previousInvoice
                    line.amount = payAmount.subtract(payByVouchers)
                    line.invoice.updateAmountOwing()
                    line.invoice.updateDateDue()
                    line.invoice.updateOverdue()
                }
            }
        }

        paymentIn.amount = paymentIn.paymentInLines
                .collect { it.amount }.inject(Money.ZERO) { a, b -> a.add(b) } as Money

        if (paymentIn.amount != new Money(checkout.payNow)) {
            result << new CheckoutValidationErrorDTO(propertyName: "payNow",  error:  "Payment amount doesn't match invoice allocated total amount")
        }
    }

    private void createFundingInvoice(InvoiceDTO dto) {
        if (dto.total > 0) {
            Invoice fundingInvoice = context.newObject(Invoice)
            fundingInvoice.contact = contactApiService.getEntityAndValidateExistence(context, dto.contactId)
            fundingInvoice.invoiceDate = LocalDate.now()
            fundingInvoice.source = SOURCE_ONCOURSE
            fundingInvoice.createdByUser = context.localObject(systemUserService.currentUser)
            fundingInvoice.customerReference = trimToNull(dto.customerReference)
            fundingInvoice.confirmationStatus = DO_NOT_SEND
            if (trimToNull(invoice.contact.getAddress()) != null) {
                fundingInvoice.billToAddress = payer.address
            }
            invoiceApiService.updateInvoiceDueDates(fundingInvoice, dto.paymentPlans)

            FundingSource relatedFundingSource = fundingSourceDao.getById(context, dto.relatedFundingSourceId)
            dto.invoiceLines.findAll { it.finalPriceToPayIncTax > 0 }.each { dtoLine ->
                Enrolment enrolment = invoice.invoiceLines*.enrolment.find { it && it.courseClass.id == dtoLine.courseClassId }
                enrolment.vetPurchasingContractID = fundingInvoice.customerReference
                enrolment.relatedFundingSource = relatedFundingSource
                if (enrolment.relatedFundingSource) {
                    enrolment.relatedFundingSource.fundingProvider = fundingInvoice.contact
                }

                InvoiceLine invoiceLine = context.newObject(InvoiceLine)
                invoiceLine.invoice = fundingInvoice
                invoiceLine.enrolment = enrolment
                invoiceLine.courseClass = enrolment.courseClass
                invoiceLine.quantity = ONE
                invoiceLine.tax = nonSupplyTax(context)
                invoiceLine.priceEachExTax = new Money(dtoLine.finalPriceToPayIncTax)
                invoiceLine.sortOrder = 0
                invoiceLine.account = enrolment.courseClass.incomeAccount
                invoiceLine.prepaidFeesAccount = prepaidFeesAccount
                invoiceLine.discountEachIncTax = Money.ZERO
                invoiceLine.title = "Funding provided for the enrolment of ${enrolment.student.contact.getFullName()} " +
                        "in ${enrolment.courseClass.uniqueCode} ${enrolment.courseClass.course.name} " +
                        "commencing ${enrolment.courseClass.startDateTime?.format('dd-MM-yyyy', enrolment.courseClass.timeZone)}"
                invoiceLine.description = invoiceLine.title
                invoiceLine.recalculateTaxEach()
            }
            fundingInvoice.updateAmountOwing()
        }
    }

    private Enrolment createEnrolment(Contact contact, CourseClass courseClass, Boolean sendEmail, StudyReason reason) {

        Student student = contact.student
        if (student == null) {
            student = context.newObject(Student)
            contact.student = student
            contact.isStudent = true
        }
        Enrolment enrolment = context.newObject(Enrolment)

        enrolment.student = student
        enrolment.studyReason = reason
        enrolment.source = SOURCE_ONCOURSE
        enrolment.status = EnrolmentStatus.SUCCESS
        enrolment.confirmationStatus = sendEmail ? NOT_SENT : DO_NOT_SEND
        enrolment.courseClass = courseClass
        enrolment.fundingSource  = courseClass.fundingSource
        enrolment.relatedFundingSource = courseClass.relatedFundingSource
        enrolment.vetFundingSourceStateID = courseClass.vetFundingSourceStateID
        enrolment.vetPurchasingContractID = courseClass.vetPurchasingContractID

        InvoiceLine invoiceLine = context.newObject(InvoiceLine)
        invoiceLine.invoice = invoice
        invoiceLine.enrolment = enrolment
        invoiceLine.quantity = ONE
        invoiceLine.tax = taxOverride?:courseClass.tax as Tax
        invoiceLine.priceEachExTax = courseClass.feeExGst
        invoiceLine.sortOrder = 0
        invoiceLine.account = courseClass.incomeAccount
        invoiceLine.prepaidFeesAccount = prepaidFeesAccount
        invoiceLine.discountEachIncTax = Money.ZERO
        invoiceLine.title = GetInvoiceLineTitle.valueOf(enrolment).get()
        invoiceLine.description = GetInvoiceLineDescription.valueOf(enrolment).get()

        invoiceLine.recalculateTaxEach()

        new UpdateAttendancesAndOutcomes(context, enrolment, true).update()

        return enrolment
    }

    private void applyDiscount(BigDecimal totalOverride, InvoiceLine invoiceLine, CourseClass courseClass, Discount discount) {
        DiscountCourseClass discountCourseClass = courseClass.discountCourseClasses.find { it.discount.id == discount.id }

        InvoiceLineDiscount invoiceLineDiscount = context.newObject(InvoiceLineDiscount)
        invoiceLineDiscount.invoiceLine = invoiceLine
        invoiceLineDiscount.discount = discountCourseClass.discount

        if (totalOverride != null) {

            Money total = Money.valueOf(totalOverride)
            BigDecimal taxRate = (taxOverride?:courseClass.tax).rate

            invoiceLine.discountEachExTax = invoiceLine.priceEachExTax.subtract(total.divide(Money.ONE.add(taxRate)))
            invoiceLine.taxEach = total.subtract(invoiceLine.priceEachExTax.subtract(invoiceLine.discountEachExTax))

        } else {
            DiscountUtils.applyDiscounts(discountCourseClass, invoiceLine, invoiceLine.tax.rate, discountCourseClass.courseClass.taxAdjustment)
        }
        invoiceLine.cosAccount = discountCourseClass.discount.cosAccount
    }
    
    private InvoiceDueDate createDueDate(Money amount, LocalDate date) {
        InvoiceDueDate dueDate = context.newObject(InvoiceDueDate)

        dueDate.invoice = invoice
        dueDate.amount = amount
        dueDate.dueDate = date

        return dueDate
    }


    private void processRedeemedVouchers() {
        checkout.redeemedVouchers.each { id, amount ->
            Money payAmount = new Money(amount)
            if (payAmount != Money.ZERO) {
                Voucher voucher = getRedeemedVoucherAndValidate(id)

                if (voucher.voucherProduct.maxCoursesRedemption != null) {
                    processCourseVoucher(voucher, payAmount)
                }

                if (voucher.voucherProduct.maxCoursesRedemption == null) {
                    processMoneyVoucher(voucher, payAmount)
                }
            }
        }
    }


    private Voucher getRedeemedVoucherAndValidate(String id) {
        if (!trimToNull(id)) {
            result << new CheckoutValidationErrorDTO(itemId: Long.valueOf(id), propertyName: "redeemedVouchers",  error:  "Redeemed voucher id required")
        }

        Voucher voucher = ObjectSelect.query(Voucher)
                .where(Voucher.ID.eq(Long.valueOf(id)))
                .and(Voucher.STATUS.eq(ProductStatus.ACTIVE))
                .and(Voucher.EXPIRY_DATE.gt(new Date()).orExp(Voucher.EXPIRY_DATE.isNull()))
                .selectOne(context)

        if (!voucher) {
            result << new CheckoutValidationErrorDTO(itemId: Long.valueOf(id), propertyName: "redeemedVouchers",  error:  "Voucher is not available")
        }

        if (voucher.contact && payer.id != voucher.contact.id) {
            result << new CheckoutValidationErrorDTO(itemId: Long.valueOf(id), propertyName: "redeemedVouchers",  error:  "Payer is wrong")
        }

        if (voucher.fullyRedeemed) {
            result << new CheckoutValidationErrorDTO(itemId: Long.valueOf(id), propertyName: "redeemedVouchers",  error:  "Voucher is fully redeemed")
        }

        voucher
    }


    private void processCourseVoucher(Voucher voucher, Money amount) {
        if (voucher.classesRemaining < 1) {
            return
        }

        PaymentIn vPaymentIn = createPaymentInPaidByVoucher(amount)

        PaymentInLine paymentInLine = context.newObject(PaymentInLine)
        paymentInLine.payment = vPaymentIn
        paymentInLine.invoice = invoice
        paymentInLine.amount  = amount

        checkout.contactNodes.each { node ->
            node.enrolments
                    .findAll { !processedEnrolments.contains(it) && voucher.id == it.appliedVoucherId }
                    .each { enrolment ->
                        VoucherPaymentIn voucherPayment = createVoucherPaymentIn(voucher, vPaymentIn)
                        voucherPayment.invoiceLine = invoice.invoiceLines
                                .find { line -> line.enrolment.courseClass.id == enrolment.classId && line.enrolment.student.contact.id == node.contactId }
                        voucher.redeemedCourseCount += 1
                        processedEnrolments.add(enrolment)
                    }
        }

        if (voucher.fullyRedeemed) {
            voucher.status = ProductStatus.REDEEMED
        }
    }


    private void processMoneyVoucher(Voucher voucher, Money amountVoucher) {
        Money leftToPay = Money.ZERO.add(amountVoucher)
        PaymentIn vPaymentIn = createPaymentInPaidByVoucher()
        createVoucherPaymentIn(voucher, vPaymentIn)

        checkout.previousInvoices.each { id, amount ->
            Money payNow = Money.valueOf(amount)
            if (payNow > Money.ZERO) {
                Invoice prevInvoice = invoiceApiService.getEntityAndValidateExistence(context, Long.valueOf(id))

                Money amountForInvoice = payNow.min(leftToPay)
                if (amountForInvoice.isGreaterThan(Money.ZERO)) {
                    PaymentInLine line = context.newObject(PaymentInLine)
                    line.payment = vPaymentIn
                    line.invoice = prevInvoice
                    line.amount  = amountForInvoice
                }

                voucher.redemptionValue = voucher.redemptionValue.subtract(amountForInvoice)
                leftToPay = leftToPay.subtract(amountForInvoice)
            }
        }

        if (leftToPay > Money.ZERO) {
            if (invoice) {
                PaymentInLine line = context.newObject(PaymentInLine)
                line.payment = vPaymentIn
                line.invoice = invoice
                line.amount  = leftToPay
                voucher.redemptionValue = voucher.valueRemaining.subtract(leftToPay)
            }
        }

        vPaymentIn.amount = vPaymentIn.paymentInLines
                .collect { it.amount }.inject(Money.ZERO) { a, b -> a.add(b) } as Money

        if (vPaymentIn.amount != amountVoucher) {
            result << new CheckoutValidationErrorDTO(itemId: voucher.id, propertyName: "redeemedVouchers",  error:  "Redeemed voucher amount isn't correct.")
        }

        if (voucher.fullyRedeemed) {
            voucher.status = ProductStatus.REDEEMED
        }
    }


    private PaymentIn createPaymentInPaidByVoucher(Money amount = Money.ZERO) {
        PaymentIn vPaymentIn = context.newObject(PaymentIn)
        vPaymentIn.source = SOURCE_ONCOURSE
        vPaymentIn.payer = payer
        vPaymentIn.createdBy = context.localObject(systemUserService.currentUser)
        vPaymentIn.administrationCentre = vPaymentIn.createdBy.defaultAdministrationCentre
        vPaymentIn.paymentDate = checkout.paymentDate?:LocalDate.now()

        vPaymentIn.amount = amount
        vPaymentIn.paymentMethod = PaymentMethodUtil.getVOUCHERPaymentMethods(context, PaymentMethod)
        vPaymentIn.status = PaymentStatus.SUCCESS
        vPaymentIn.confirmationStatus = checkout.sendInvoice ? NOT_SENT : DO_NOT_SEND

        vPaymentIn
    }


    private VoucherPaymentIn createVoucherPaymentIn(Voucher voucher, PaymentIn vPaymentIn) {
        VoucherPaymentIn voucherPayment = context.newObject(VoucherPaymentIn)
        voucherPayment.voucher   = voucher
        voucherPayment.paymentIn = vPaymentIn
        voucherPayment.status    = VoucherPaymentStatus.APPROVED
        vPaymentIn.account       = (voucher.product as VoucherProduct).liabilityAccount

        voucherPayment
    }


    private static Money getAmountPaidByVouchers(Invoice invoicePaidByVouchers) {

        invoicePaidByVouchers.paymentLines
                .findAll { line -> line.isNewRecord() }
                .findAll { line -> line.amount.isGreaterThan(Money.ZERO) }
                .inject(Money.ZERO) { a, b -> a.add(b.amount) }

    }
}
