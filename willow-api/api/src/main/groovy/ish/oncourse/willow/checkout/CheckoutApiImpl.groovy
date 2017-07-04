package ish.oncourse.willow.checkout

import com.google.inject.Inject
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.WebSite
import ish.oncourse.willow.cayenne.CayenneService
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.checkout.functions.ProcessCheckoutModel
import ish.oncourse.willow.checkout.functions.ProcessClasses
import ish.oncourse.willow.checkout.functions.ProcessProducts
import ish.oncourse.willow.checkout.payment.CreatePaymentModel
import ish.oncourse.willow.checkout.payment.GetPaymentStatus
import ish.oncourse.willow.checkout.payment.HasErrors
import ish.oncourse.willow.checkout.payment.ProcessPaymentModel
import ish.oncourse.willow.checkout.payment.ValidateCreditCardForm
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.checkout.payment.PaymentResponse
import ish.oncourse.willow.model.checkout.request.ContactNodeRequest
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.common.ValidationError
import ish.oncourse.willow.service.CheckoutApi
import ish.oncourse.willow.service.CollegeInfo
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Response


@CompileStatic
class CheckoutApiImpl implements CheckoutApi {
    
    final static  Logger logger = LoggerFactory.getLogger(CheckoutApiImpl.class)


    private CayenneService cayenneService
    private CollegeService collegeService

    @Inject
    CheckoutApiImpl(CayenneService cayenneService, CollegeService collegeService) {
        this.cayenneService = cayenneService
        this.collegeService = collegeService
    }

    @Override
    CheckoutModel getCheckoutModel(CheckoutModelRequest checkoutModelRequest) {
        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college
        ProcessCheckoutModel processModel = new ProcessCheckoutModel(context, college, checkoutModelRequest).process()
        processModel.model
    }
    

    @Override
    ContactNode getContactNode(ContactNodeRequest contactNodeRequest) {

        if (contactNodeRequest.classIds.empty && contactNodeRequest.productIds.empty) {
            logger.error('There are not selected items for purchase')
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'there are not selected items for purchase')).build())
        }
        
        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college
        
        Contact contact = new GetContact(context, college, contactNodeRequest.contactId).get()

        ContactNode items = new ContactNode()
        items.contactId = contact.id.toString()
        
        ProcessClasses processClasses = new ProcessClasses(context, contact, college, contactNodeRequest.classIds, contactNodeRequest.promotionIds).process()
        items.enrolments = processClasses.enrolments
        items.applications = processClasses.applications
        
        ProcessProducts processProducts = new ProcessProducts(context, contact, college, contactNodeRequest.productIds, null).process()
        items.articles += processProducts.articles
        items.memberships += processProducts.memberships
        items.vouchers += processProducts.vouchers
        
        items
    }

    @Override
    PaymentResponse getPaymentStatus(String sessionId) {
        
        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college
        
        new GetPaymentStatus(context, college, sessionId).get()
    }

    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
    PaymentResponse makePayment(PaymentRequest paymentRequest) {
        ObjectContext context = cayenneService.newContext()
        WebSite webSite = collegeService.webSite
        College college = webSite.college
        
        CheckoutModel checkoutModel = getCheckoutModel(paymentRequest.checkoutModelRequest)

        if (new HasErrors(checkoutModel).hasErrors()) {
            checkoutModel.error = new CommonError(message: 'Purchase items are not valid')
            throw new BadRequestException(Response.status(400).entity(checkoutModel).build())
        } else if (checkoutModel.amount.payNow != paymentRequest.payNow) {
            checkoutModel.error = new CommonError(message: 'Payment amount is wrong')
            throw new BadRequestException(Response.status(400).entity(checkoutModel).build())
        }
        Money payNow = checkoutModel.amount.payNow.toMoney()
        
        ValidationError validationError = new ValidateCreditCardForm(paymentRequest, context).validate(Money.ZERO == payNow)
        if (!validationError.formErrors.empty || !validationError.fieldsErrors.empty) {
            throw new BadRequestException(Response.status(400).entity(validationError).build())
        }
        
        CreatePaymentModel createPaymentModel =  new CreatePaymentModel(context, college, webSite, paymentRequest, checkoutModel).create()
        
        ProcessPaymentModel processPaymentModel = new ProcessPaymentModel(context, cayenneService.newNonReplicatingContext, college,createPaymentModel, paymentRequest).process()

        if (processPaymentModel.error == null) {
            return processPaymentModel.response
        } else {
            checkoutModel.error = processPaymentModel.error
            throw new BadRequestException(Response.status(400).entity(checkoutModel).build())
        }
        
    }
}
