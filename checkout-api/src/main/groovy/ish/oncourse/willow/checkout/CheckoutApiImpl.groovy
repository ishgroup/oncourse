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
import ish.oncourse.willow.checkout.functions.ProcessWaitingLists
import ish.oncourse.willow.checkout.functions.ValidatePaymentRequest
import ish.oncourse.willow.checkout.payment.CreatePaymentModel
import ish.oncourse.willow.checkout.payment.GetPaymentStatus
import ish.oncourse.willow.checkout.payment.ProcessPaymentModel
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.checkout.payment.PaymentResponse
import ish.oncourse.willow.model.checkout.request.ContactNodeRequest
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.service.CheckoutApi
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
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
        
        if (processModel.model.error) {
            throw new BadRequestException(Response.status(400).entity(processModel.model).build())
        }  
        
        return processModel.model
    }
    
    @Override
    ContactNode getContactNode(ContactNodeRequest contactNodeRequest) {

        if (contactNodeRequest.classIds.empty && contactNodeRequest.productIds.empty) {
            logger.error('There are not selected items for purchase')
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'there are not selected items for purchase')).build())
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

        ProcessProducts processProducts = new ProcessProducts(context, contact, college, contactNodeRequest.productIds).process()
        node.articles += processProducts.articles
        node.memberships += processProducts.memberships
        node.vouchers += processProducts.vouchers

        node
    }

    @Override
    PaymentResponse getPaymentStatus(String sessionId) {
        new GetPaymentStatus(cayenneService.newContext(), collegeService.college, sessionId).get()
    }

    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
    PaymentResponse makePayment(PaymentRequest paymentRequest) {
        ObjectContext context = cayenneService.newContext()
        WebSite webSite = collegeService.webSite
        College college = webSite.college
        
        CheckoutModel checkoutModel = getCheckoutModel(paymentRequest.checkoutModelRequest)
        ValidatePaymentRequest validatePaymentRequest = new ValidatePaymentRequest(checkoutModel, paymentRequest, context)
        
        if (validatePaymentRequest.commonError) {
            throw new BadRequestException(Response.status(400).entity(checkoutModel).build())
        } else if (validatePaymentRequest.validationError) {
            throw new BadRequestException(Response.status(400).entity(validatePaymentRequest.validationError).build())
        }
        
        CreatePaymentModel createPaymentModel =  new CreatePaymentModel(context, college, webSite, paymentRequest, checkoutModel).create()
        ProcessPaymentModel processPaymentModel = new ProcessPaymentModel(context, cayenneService.newNonReplicatingContext(), college, createPaymentModel, paymentRequest).process()
        
        if (processPaymentModel.error == null) {
            return processPaymentModel.response
        } else {
            checkoutModel.error = processPaymentModel.error
            throw new BadRequestException(Response.status(400).entity(checkoutModel).build())
        }
        
    }
}
