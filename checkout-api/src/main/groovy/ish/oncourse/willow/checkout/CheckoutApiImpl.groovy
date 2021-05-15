package ish.oncourse.willow.checkout

import com.google.inject.Inject
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.math.Money
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.WebSite
import ish.oncourse.willow.ContactNodeService
import ish.oncourse.willow.EntityRelationService
import ish.oncourse.willow.FinancialService
import ish.oncourse.willow.checkout.functions.*
import ish.oncourse.willow.checkout.functions.v2.ValidatePaymentRequest as V2ValidatePaymentRequest
import ish.oncourse.willow.checkout.payment.CreatePaymentModel
import ish.oncourse.willow.checkout.payment.GetPaymentStatus
import ish.oncourse.willow.checkout.payment.ProcessPaymentModel
import ish.oncourse.willow.checkout.payment.v2.CreatePaymentModel as V2CreatePaymentModel
import ish.oncourse.willow.checkout.payment.v2.ProcessPaymentModel as V2ProcessPaymentModel
import ish.oncourse.willow.checkout.windcave.IPaymentService
import ish.oncourse.willow.checkout.windcave.PaymentServiceBuilder
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.checkout.payment.PaymentResponse
import ish.oncourse.willow.model.checkout.request.ContactNodeRequest
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.common.ValidationError
import ish.oncourse.willow.model.v2.checkout.payment.PaymentRequest as V2PaymentRequest
import ish.oncourse.willow.model.v2.checkout.payment.PaymentResponse as V2PaymentResponse
import ish.oncourse.willow.service.CheckoutApi
import ish.oncourse.willow.service.CheckoutV2Api
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

@CompileStatic
class CheckoutApiImpl implements CheckoutApi, CheckoutV2Api {
    
    final static  Logger logger = LogManager.getLogger(CheckoutApiImpl.class)


    private CayenneService cayenneService
    private CollegeService collegeService
    private FinancialService financialService
    private EntityRelationService relationService

    @Inject
    CheckoutApiImpl(CayenneService cayenneService, CollegeService collegeService, FinancialService financialService, EntityRelationService relationService) {
        this.cayenneService = cayenneService
        this.collegeService = collegeService
        this.financialService = financialService
        this.relationService = relationService
    }

    @Override
    CheckoutModel getCheckoutModel(CheckoutModelRequest checkoutModelRequest) {
        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college
        ProcessCheckoutModel processModel = new ProcessCheckoutModel(context, college, checkoutModelRequest, financialService).process()
        
        if (processModel.model.error) {
            throw new BadRequestException(Response.status(400).entity(processModel.model).build())
        }  
        
        return processModel.model
    }
    
    @Override
    @Deprecated
    ContactNode getContactNode(ContactNodeRequest contactNodeRequest) {

        if (contactNodeRequest.classIds.empty 
                && contactNodeRequest.products.empty
                && contactNodeRequest.waitingCourseIds.empty) {
            logger.info('There are no selected items for purchase')
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'There are no selected items for purchase')).build())
        }
        
        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college
        
        Contact contact = new GetContact(context, college, contactNodeRequest.contactId).get(false)

        ContactNode node = new ContactNode()
        node.contactId = contact.id.toString()
        
        if (!contact.isCompany) {
            ProcessClasses processClasses = new ProcessClasses(context, contact, college, contactNodeRequest.classIds, contactNodeRequest.promotionIds).process()
            node.enrolments = processClasses.enrolments
            node.applications = processClasses.applications
            
            ProcessWaitingLists processWaitingLists = new ProcessWaitingLists(context, contact, college, contactNodeRequest.waitingCourseIds).process()
            node.waitingLists += processWaitingLists.waitingLists
        }

        ProcessProducts processProducts = new ProcessProducts(context, contact, college, contactNodeRequest.products).process()
        node.articles += processProducts.articles
        node.memberships += processProducts.memberships
        node.vouchers += processProducts.vouchers

        node
    }

    @Override
    @Deprecated
    PaymentResponse getPaymentStatus(String sessionId) {
        new GetPaymentStatus(cayenneService.newContext(), collegeService.college, sessionId).get()
    }

    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
    @Deprecated
    PaymentResponse makePayment(PaymentRequest paymentRequest) {
        ObjectContext context = cayenneService.newContext()
        WebSite webSite = collegeService.webSite
        College college = webSite.college
        
        CheckoutModel checkoutModel = getCheckoutModel(paymentRequest.checkoutModelRequest)
        ValidatePaymentRequest validatePaymentRequest = new ValidatePaymentRequest(checkoutModel, paymentRequest, context, financialService.getAvailableCredit(checkoutModel.payerId)).validate()
        
        if (validatePaymentRequest.commonError) {
            throw new BadRequestException(Response.status(400).entity(checkoutModel).build())
        } else if (validatePaymentRequest.validationError) {
            throw new BadRequestException(Response.status(400).entity(validatePaymentRequest.validationError).build())
        }
        
        CreatePaymentModel createPaymentModel =  new CreatePaymentModel(context, college, webSite, paymentRequest, checkoutModel, financialService).create()
        ProcessPaymentModel processPaymentModel = new ProcessPaymentModel(context, cayenneService.newNonReplicatingContext(), college, createPaymentModel, paymentRequest).process()
        
        if (processPaymentModel.error == null) {
            return processPaymentModel.response
        } else {
            checkoutModel.error = processPaymentModel.error
            throw new BadRequestException(Response.status(400).entity(checkoutModel).build())
        }
        
    }

    @Override
    @CompileDynamic
    V2PaymentResponse makePayment(V2PaymentRequest paymentRequest, Boolean xValidate, String payerId, String origin) {
        try {
            ObjectContext context = cayenneService.newContext()
            College college = collegeService.college
            Contact payer = collegeService.payer
            WebSite webSite = collegeService.webSite

            CheckoutModel checkoutModel = null

            if (paymentRequest.checkoutModelRequest) {
                checkoutModel = getCheckoutModel(paymentRequest.checkoutModelRequest)
                V2ValidatePaymentRequest validatePaymentRequest = new V2ValidatePaymentRequest(checkoutModel, (paymentRequest.ccAmount != null ? paymentRequest.ccAmount.toMoney() : Money.ZERO), context, financialService.getAvailableCredit(checkoutModel.payerId)).validate()
                if (validatePaymentRequest.commonError) {
                    throw new BadRequestException(Response.status(400).entity(checkoutModel).build())
                }
            } else {
                Money owing = financialService.getOwing(payer)
                Money payNow = paymentRequest.ccAmount.toMoney()
                if (payNow.isGreaterThan(owing)) {
                    throw new BadRequestException(Response.status(400).entity(new ValidationError(formErrors: ['Payment amount is greater than payer owing amount'])).build())
                }
            }

            V2CreatePaymentModel createPaymentModel = new V2CreatePaymentModel(context, college, webSite, paymentRequest, checkoutModel, financialService, payer).create()

            V2ProcessPaymentModel processPaymentModel = new V2ProcessPaymentModel(context, college, createPaymentModel, paymentRequest, xValidate, origin).process()

            if (processPaymentModel.error == null) {
                return processPaymentModel.response
            } else {
                logger.error(processPaymentModel.error)
                throw new BadRequestException(Response.status(400).entity(processPaymentModel.error).build())
            }
        } catch(Exception e) {
            logger.error("Checkout failed, paymentRequest: ${paymentRequest.toString()}, xValidate: $xValidate, payerId: $payerId, origin: $origin", e)
            logger.catching(e)
            throw e
        }
    }

    @Override
    ContactNode getContactNodeV2(ContactNodeRequest request) {
        if (request.classIds.empty
                && request.products.empty
                && request.waitingCourseIds.empty) {
            logger.info('There are no selected items for purchase')
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'There are no selected items for purchase')).build())
        }
        return new ContactNodeService(cayenneService, collegeService, relationService, request).getContactNode()
    }

    @Override
    V2PaymentResponse getStatus(String sessionId, String payerId) {
        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college

        IPaymentService paymentService = new PaymentServiceBuilder().build(college, context)
        paymentService.checkStatus(sessionId)
        new V2PaymentResponse(responseText:paymentService.checkStatus(sessionId).statusText)
    }
}
