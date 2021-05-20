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

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.common.types.EntityRelationCartAction
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.CourseDao
import ish.oncourse.server.api.dao.EntityRelationDao
import ish.oncourse.server.api.dao.ModuleDao
import ish.oncourse.server.api.dao.ProductDao
import ish.oncourse.server.api.v1.model.CheckoutSaleRelationDTO
import ish.oncourse.server.api.v1.model.EntityRelationCartActionDTO
import ish.oncourse.server.api.v1.model.SaleDTO
import ish.oncourse.server.api.v1.model.SaleTypeDTO
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.Module
import ish.oncourse.server.cayenne.EntityRelation
import ish.oncourse.server.cayenne.EntityRelationType
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.Product
import org.apache.commons.lang3.StringUtils

import static ish.common.types.ConfirmationStatus.DO_NOT_SEND
import static ish.common.types.ConfirmationStatus.NOT_SENT
import ish.common.types.PaymentStatus
import ish.common.types.SystemEventType
import ish.math.Money
import ish.oncourse.common.SystemEvent
import ish.oncourse.server.CayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.checkout.Checkout
import ish.oncourse.server.api.checkout.CheckoutController
import ish.oncourse.server.api.dao.FundingSourceDao
import ish.oncourse.server.api.dao.PaymentInDao
import ish.oncourse.server.api.service.ArticleProductApiService
import ish.oncourse.server.api.service.ContactApiService
import ish.oncourse.server.api.service.CourseClassApiService
import ish.oncourse.server.api.service.InvoiceApiService
import ish.oncourse.server.api.service.MembershipProductApiService
import ish.oncourse.server.api.service.VoucherProductApiService
import ish.oncourse.server.api.v1.function.DiscountFunctions
import ish.oncourse.server.api.v1.model.CheckoutArticleDTO
import ish.oncourse.server.api.v1.model.CheckoutEnrolmentDTO
import ish.oncourse.server.api.v1.model.CheckoutMembershipDTO
import ish.oncourse.server.api.v1.model.CheckoutModelDTO
import ish.oncourse.server.api.v1.model.CheckoutResponseDTO
import ish.oncourse.server.api.v1.model.CheckoutValidationErrorDTO
import ish.oncourse.server.api.v1.model.CheckoutVoucherDTO
import ish.oncourse.server.api.v1.model.CourseClassDiscountDTO
import ish.oncourse.server.api.v1.model.InvoiceDTO
import ish.oncourse.server.api.v1.model.InvoiceInvoiceLineDTO
import ish.oncourse.server.api.v1.model.SessionStatusDTO
import ish.oncourse.server.api.v1.service.CheckoutApi
import ish.oncourse.server.cayenne.Article
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.DiscountCourseClass
import ish.oncourse.server.cayenne.Membership
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.ProductItem
import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.server.integration.EventService
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.users.SystemUserService
import ish.oncourse.server.windcave.PaymentService
import static ish.oncourse.server.windcave.PaymentService.AUTH_TYPE
import ish.oncourse.server.windcave.SessionAttributes
import ish.util.DiscountUtils
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.validation.ValidationException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

@CompileStatic
class CheckoutApiImpl implements CheckoutApi {

    private static final Logger logger = LogManager.getLogger(CheckoutApiImpl)

    public static final int WINDCAVE_ERROR = 411

    public static final int WINDCAVE_PAYMENT_ERROR = 412

    public static final int VALIDATION_ERROR = 400


    @Inject
    ContactDao contactDao
    
    @Inject
    CourseDao courseDao
    
    @Inject
    ProductDao productDao

    @Inject
    ModuleDao moduleDao
    
    @Inject
    PreferenceController preferenceController

    @Inject
    FundingSourceDao fundingSourceDao

    @Inject
    PaymentService paymentService
    
    @Inject
    LicenseService licenseService

    @Inject
    CourseClassApiService courseClassService

    @Inject
    CayenneService cayenneService

    @Inject
    ContactApiService contactApiService

    @Inject
    VoucherProductApiService voucherApiService

    @Inject
    ArticleProductApiService articleApiService

    @Inject
    MembershipProductApiService membershipApiService

    @Inject
    CourseClassApiService courseClassApiService

    @Inject
    InvoiceApiService invoiceApiService

    @Inject
    SystemUserService systemUserService

    @Inject
    EventService eventService

    @Inject
    PaymentInDao paymentInDao

    @Override
    List<CourseClassDiscountDTO> getContactDiscounts(Long contactId, Long classId,
                                                     String courseIds, String productIds, String promoIds, String membershipIds,
                                                     Integer enrolmentsCount, BigDecimal purchaseTotal) {
        ObjectContext context = cayenneService.newContext
        CourseClass courseClass = courseClassService.getEntityAndValidateExistence(context, classId)
        Contact contact = contactApiService.getEntityAndValidateExistence(context, contactId)
        if (contact.isCompany) {
            return []
        }
        List<Long> courses = courseIds == null || courseIds.empty ? [] : courseIds.split(',').collect { Long.valueOf(it) }
        List<Long> products = productIds == null || productIds.empty ? [] : productIds.split(',').collect { Long.valueOf(it) }
        List<Long> promos = promoIds == null || promoIds.empty ? [] : promoIds.split(',').collect { Long.valueOf(it)}
        List<MembershipProduct> memberships = membershipIds == null || membershipIds.empty ? [] :
                membershipIds.split(',').collect { membershipApiService.getEntityAndValidateExistence(context,Long.valueOf(it)) }
        Money total = new Money(purchaseTotal)

        List<DiscountCourseClass> discountCourseClasses = courseClass.getAvalibleDiscounts(contact, courses, products, promos, memberships, enrolmentsCount, total)

        if (discountCourseClasses.empty) {
            return []
        } else {
            DiscountCourseClass best = DiscountUtils.chooseDiscountForApply(discountCourseClasses, courseClass.feeExGst, courseClass.tax.rate) as DiscountCourseClass
            if (best) {
                discountCourseClasses.add(0, best)
            }
            return discountCourseClasses.unique().collect {
                CourseClassDiscountDTO dto = new CourseClassDiscountDTO()
                dto.forecast = it.predictedStudentsPercentage
                dto.discountOverride = it.discountDollar?.toBigDecimal()
                dto.setDiscount(DiscountFunctions.toRestDiscountMinimized(it.discount))
                dto
            }
        }
    }

    List<CheckoutSaleRelationDTO> getSaleRelations(Long id, String entityName, Contact contact) {
        ObjectContext context = cayenneService.newContext
        List<EntityRelation> relations = EntityRelationDao.getRelatedToOrEqual(context, entityName, id)
                .findAll { EntityRelationCartAction.NO_ACTION != it.relationType.shoppingCart }
        List<CheckoutSaleRelationDTO> result = []
        
        relations.findAll { Course.simpleName == it.toEntityIdentifier && id != it.toEntityAngelId }.each { relation ->
            EntityRelationType relationType = relation.relationType
            Course course = courseDao.getById(context, relation.toEntityAngelId)

            if (contact && relationType.considerHistory  && contact.student.isEnrolled(course)) {
                //ignore that course since student already enrolled in 
            } else {
                result << createCourseCheckoutSaleRelation(id, entityName, course, relationType)
            }
        }

        relations.findAll { Course.simpleName == it.fromEntityIdentifier && id != it.fromEntityAngelId }.each { relation ->
            EntityRelationType relationType = relation.relationType
            Course course = courseDao.getById(context, relation.fromEntityAngelId)

            if (contact && relationType.considerHistory  && contact.student.isEnrolled(course)) {
                //ignore that course since student already enrolled in
            } else {
                result << createCourseCheckoutSaleRelation(id, entityName, course, relationType)
            }
        }
        
        relations.findAll { Product.simpleName == it.toEntityIdentifier && id != it.toEntityAngelId }.each { relation ->
            EntityRelationType relationType = relation.relationType
            Product product = productDao.getById(context, relation.toEntityAngelId)
            result << createProductCheckoutSaleRelation(id, entityName, product, relationType)
        }

        relations.findAll { Product.simpleName == it.fromEntityIdentifier && id != it.fromEntityAngelId }.each { relation ->
            EntityRelationType relationType = relation.relationType
            Product product = productDao.getById(context, relation.fromEntityAngelId)
            result << createProductCheckoutSaleRelation(id, entityName, product, relationType)
        }

        result
    }

    private static CheckoutSaleRelationDTO createCourseCheckoutSaleRelation(Long id, String entityName, Course course, EntityRelationType relationType) {
        new CheckoutSaleRelationDTO().with {saleRelation ->
            saleRelation.fromItem = new SaleDTO(id: id, type: SaleTypeDTO.values()[0].getFromCayenneClassName(entityName))
            saleRelation.toItem = new SaleDTO(id: course.id, type: SaleTypeDTO.COURSE)
            saleRelation.cartAction = EntityRelationCartActionDTO.values()[0].fromDbType(relationType.shoppingCart)
            if (relationType.discount) {
                saleRelation.discount = DiscountFunctions.toRestDiscount(relationType.discount, false)
            }
            saleRelation
        }
    }

    private static CheckoutSaleRelationDTO createProductCheckoutSaleRelation(Long id, String entityName, Product product, EntityRelationType relationType) {
        new CheckoutSaleRelationDTO().with { saleRelation ->
            saleRelation.fromItem = new SaleDTO(id: id, type: SaleTypeDTO.values()[0].getFromCayenneClassName(entityName))
            saleRelation.toItem = new SaleDTO(id: product.id, type: SaleTypeDTO.values()[0].getFromCayenneClassName(product.getClass().getSimpleName()))
            saleRelation.cartAction = EntityRelationCartActionDTO.values()[0].fromDbType(relationType.shoppingCart)
            saleRelation
        }
    }

    @Override
    List<CheckoutSaleRelationDTO> getSaleRelations(String courseIds, String productIds, Long contactId) {

        ObjectContext context = cayenneService.newContext
        List<CheckoutSaleRelationDTO> result = []
        Contact contact = contactId ? contactDao.getById(context, contactId) : null
        
        if (StringUtils.trimToNull(courseIds)) {
            (courseIds.split(',').collect {Long.valueOf(it)} as List<Long>).each { courseId ->
                result.addAll(getSaleRelations(courseId, Course.simpleName, contact))
            }
        } else if (StringUtils.trimToNull(productIds)) {
            (productIds.split(',').collect {Long.valueOf(it)} as List<Long>).each { productId ->
                result.addAll(getSaleRelations(productId, Product.simpleName, contact))
            }
        }
        return result
    }

    @Override
    SessionStatusDTO status(String sessionId) {
        SessionStatusDTO dto = new SessionStatusDTO()
        SessionAttributes attributes =  paymentService.checkStatus(sessionId)
        dto.authorised = attributes.authorised
        dto.complete = attributes.complete
        dto.responseText = attributes.statusText
        return dto
    }

    @Override
    CheckoutResponseDTO submit(CheckoutModelDTO checkoutModel, Boolean xValidateOnly, String xPaymentSessionId, String xOrigin ) {
        CheckoutResponseDTO dtoResponse = new CheckoutResponseDTO()
        Checkout checkout = checkoutController.createCheckout(checkoutModel)
        String cardId = null

        if (!checkout.errors.empty) {
            hanbleError(VALIDATION_ERROR, checkout.errors)
        }  else if (xValidateOnly) {
            eventService.postEvent(SystemEvent.valueOf(SystemEventType.VALIDATE_CHECKOUT, checkoutModel))
          
        }

        if (checkoutModel.payWithSavedCard) {
            cardId =  paymentInDao.getCreditCardId(checkout.paymentIn.payer)
            if (cardId == null) {
                hanbleError(VALIDATION_ERROR, [new CheckoutValidationErrorDTO(propertyName: 'payWithSavedCard', error: 'Payer has no credit card history')])
            }
        }

        if (checkout.creditCard) {
            if (!preferenceController.licenseCCProcessing) {
                 hanbleError(VALIDATION_ERROR, [new CheckoutValidationErrorDTO(error:"Real time credit card processing would be available here with a support contract from ish")])
            }

            if (xValidateOnly) {
                save(checkout)

                if (checkoutModel.payWithSavedCard) {
                    return dtoResponse
                }

                String merchantReference = UUID.randomUUID().toString()
                SessionAttributes attributes = paymentService.createSession(xOrigin, new Money(checkoutModel.payNow), merchantReference, checkoutModel.allowAutoPay)
                if (attributes.sessionId) {
                    dtoResponse.sessionId = attributes.sessionId
                    dtoResponse.ccFormUrl = attributes.ccFormUrl
                    dtoResponse.merchantReference = merchantReference
                } else if (attributes.errorMessage) {
                    hanbleError(WINDCAVE_ERROR, [new CheckoutValidationErrorDTO(error: attributes.errorMessage)])
                } else {
                    hanbleError(WINDCAVE_ERROR)
                }

            } else {
                Money amount  = checkout.paymentIn.amount
                SessionAttributes sessionAttributes
                String merchantReference = null

                if (checkoutModel.payWithSavedCard) {
                    merchantReference = UUID.randomUUID().toString()
                    sessionAttributes = paymentService.makeTransaction(amount, merchantReference, cardId)
                } else {
                    if (!checkoutModel.merchantReference) {
                        hanbleError(VALIDATION_ERROR, [new CheckoutValidationErrorDTO(propertyName: 'merchantReference', error: "Merchant reference is required")])
                    } else {
                        merchantReference = checkoutModel.merchantReference
                    }
                    sessionAttributes = paymentService.checkStatus(xPaymentSessionId)

                    if (!sessionAttributes.complete) {
                        hanbleError(VALIDATION_ERROR, [new CheckoutValidationErrorDTO(error: "Credit card authorisation is not complite")])
                    }
                }

                if (!sessionAttributes.authorised) {
                    hanbleError(VALIDATION_ERROR, [new CheckoutValidationErrorDTO(error: "Credit card declined: $sessionAttributes.statusText")])
                }

                if (ObjectSelect.query(PaymentIn).where(PaymentIn.GATEWAY_REFERENCE.eq(sessionAttributes.transactionId)).selectFirst(cayenneService.newContext) != null) {
                    hanbleError(VALIDATION_ERROR, [new CheckoutValidationErrorDTO(error: "Credit card payment already complete")])
                }

                PaymentIn paymentIn = checkout.paymentIn
                paymentIn.creditCardExpiry = sessionAttributes.creditCardExpiry
                paymentIn.creditCardName = sessionAttributes.creditCardName
                paymentIn.creditCardNumber = sessionAttributes.creditCardNumber
                paymentIn.creditCardType = sessionAttributes.creditCardType
                paymentIn.gatewayResponse = sessionAttributes.statusText
                paymentIn.gatewayReference = sessionAttributes.transactionId
                paymentIn.paymentDate = sessionAttributes.paymentDate
                paymentIn.billingId = sessionAttributes.billingId
                paymentIn.sessionId = merchantReference
                paymentIn.privateNotes = sessionAttributes.responceJson

                if (preferenceController.purchaseWithoutAuth) {
                    succeedPayment(dtoResponse, checkout, checkoutModel.sendInvoice)
                } else {
                    if (AUTH_TYPE != sessionAttributes.type) {
                        hanbleError(VALIDATION_ERROR, [new CheckoutValidationErrorDTO(error: "Credit card transaction has wrong type")])
                    }

                    save(checkout)
                    sessionAttributes = paymentService.completeTransaction(sessionAttributes.transactionId, amount, merchantReference)

                    if (sessionAttributes.authorised) {
                        succeedPayment(dtoResponse, checkout, checkoutModel.sendInvoice)
                    } else {
                        paymentIn.gatewayResponse = sessionAttributes.statusText
                        paymentIn.privateNotes = sessionAttributes.responceJson
                        checkout.context.commitChanges()
                        hanbleError(WINDCAVE_PAYMENT_ERROR,  new SessionStatusDTO(complete: sessionAttributes.complete, authorised: sessionAttributes.authorised, responseText: sessionAttributes.statusText))
                    }
                }
            }
        } else {
            save(checkout)
            fillResponce(dtoResponse, checkout)
        }

        postEnrolmentSuccessfulEvents(checkout, xValidateOnly)
        return dtoResponse
    }

    private void succeedPayment(CheckoutResponseDTO dtoResponse, Checkout checkout, Boolean sendInvoice) {
        checkout.paymentIn.status = PaymentStatus.SUCCESS
        checkout.paymentIn.privateNotes += ' Payment successful.'
        checkout.paymentIn.confirmationStatus = sendInvoice ? NOT_SENT : DO_NOT_SEND
        checkout.paymentIn.paymentInLines.each {  line  ->
            line.invoice.updateAmountOwing()
            line.invoice.updateDateDue()
            line.invoice.updateOverdue()
        }
        checkout.context.commitChanges()
        fillResponce(dtoResponse, checkout)
    }

    private void save(Checkout checkout) {
        try {
            checkout.context.commitChanges()
        } catch (ValidationException e) {
            List<CheckoutValidationErrorDTO> errors = e.validationResult.failures.collect { new CheckoutValidationErrorDTO( error: it.description) }
            String validationMessages = errors*.error.join('\n')
            logger.error(validationMessages)
            logger.catching(e)
            hanbleError(VALIDATION_ERROR, errors)
        } catch (Exception e) {
            logger.error('Unexpected error')
            logger.catching(e)
            hanbleError(VALIDATION_ERROR, [new CheckoutValidationErrorDTO(error: "Sorry, something unexpected happened. Please contact ish support team")])
        }
    }

    private void postEnrolmentSuccessfulEvents(Checkout checkout, Boolean xValidateOnly) {
        if (checkout.invoice && !xValidateOnly) {
            checkout.invoice.invoiceLines.findAll { it.enrolment }*.enrolment.each { enrol ->
                eventService.postEvent(SystemEvent.valueOf(SystemEventType.ENROLMENT_SUCCESSFUL, enrol))
            }
        }
    }

    private void fillResponce(CheckoutResponseDTO dtoResponse, Checkout checkout) {
        dtoResponse.paymentId = checkout.paymentIn?.id

        if (checkout.invoice) {
            dtoResponse.invoice = new InvoiceDTO().with { dtoInvoice ->
                dtoInvoice.id = checkout.invoice.id
                dtoInvoice.invoiceNumber = checkout.invoice.invoiceNumber
                dtoInvoice.amountOwing = checkout.invoice.amountOwing.toBigDecimal()
                checkout.invoice.invoiceLines.each { invoiceLine ->
                    dtoInvoice.invoiceLines << new InvoiceInvoiceLineDTO().with { dtoInvoiceLine ->
                        dtoInvoiceLine.priceEachExTax  = invoiceLine.priceEachExTax?.toBigDecimal()
                        dtoInvoiceLine.taxEach  = invoiceLine.taxEach?.toBigDecimal()
                        dtoInvoiceLine.discountEachExTax = invoiceLine.discountEachExTax?.toBigDecimal()
                        dtoInvoiceLine.finalPriceToPayIncTax = invoiceLine.finalPriceToPayIncTax?.toBigDecimal()
                        dtoInvoiceLine.quantity = invoiceLine.quantity
                        if (invoiceLine.enrolment) {
                            dtoInvoiceLine.contactId = invoiceLine.enrolment.student.contact.id
                            dtoInvoiceLine.enrolment = new CheckoutEnrolmentDTO(
                                    id: invoiceLine.enrolment.id,
                                    classId: invoiceLine.enrolment.courseClass.id,
                                    appliedDiscountId: invoiceLine.invoiceLineDiscounts.empty ? null : invoiceLine.invoiceLineDiscounts[0].discount.id)
                        } else if (!invoiceLine.productItems.empty) {
                            ProductItem item = invoiceLine.productItems[0]
                            switch (item.class) {
                                case Article:
                                    dtoInvoiceLine.contactId = item.contact.id
                                    dtoInvoiceLine.article = new CheckoutArticleDTO(ids: invoiceLine.productItems*.id,
                                            productId: item.product.id,
                                            quantity:invoiceLine.productItems.size().toBigDecimal())
                                    break
                                case Voucher:
                                    dtoInvoiceLine.contactId = checkout.invoice.contact.id
                                    dtoInvoiceLine.voucher = new CheckoutVoucherDTO(id: item.id,
                                            productId: item.product.id,
                                            code: (item as Voucher).code,
                                            validTo: LocalDateUtils.dateToValue((item as Voucher).expiryDate),
                                            value: (item as Voucher).redemptionValue.toBigDecimal(),
                                            restrictToPayer: (item as Voucher).redeemableBy != null)
                                    break
                                case Membership:
                                    dtoInvoiceLine.contactId = item.contact.id
                                    dtoInvoiceLine.membership = new CheckoutMembershipDTO(id: item.id,
                                            productId: item.product.id,
                                            validTo: LocalDateUtils.dateToValue((item as Membership).expiryDate) )
                                    break
                                default:
                                    throw new IllegalArgumentException(invoiceLine.productItems[0].class.simpleName)
                            }
                        }

                        dtoInvoiceLine
                    }
                }
                dtoInvoice
            }
        }
    }

    static hanbleError(int status, Object entity = null) {
        Response response = Response
                .status(status)
                .entity(entity)
                .build()

        throw new ClientErrorException(response)
    }

    private CheckoutController getCheckoutController() {
        new CheckoutController(cayenneService, systemUserService, contactApiService, invoiceApiService, courseClassApiService,  membershipApiService,  voucherApiService,  articleApiService, fundingSourceDao, moduleDao)
    }

}
