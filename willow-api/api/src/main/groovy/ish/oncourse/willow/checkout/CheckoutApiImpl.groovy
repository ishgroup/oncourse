package ish.oncourse.willow.checkout

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.willow.checkout.functions.ApplayDiscounts
import ish.oncourse.willow.checkout.functions.ApplyDiscounts
import ish.oncourse.willow.checkout.functions.ApplyPaymentPlan
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.checkout.functions.ProcessCheckoutModel
import ish.oncourse.willow.checkout.functions.ProcessClass
import ish.oncourse.willow.checkout.functions.ProcessClasses
import ish.oncourse.willow.checkout.functions.ProcessProduct
import ish.oncourse.willow.checkout.functions.ProcessProducts
import ish.oncourse.willow.checkout.functions.ValidateVoucher
import ish.oncourse.willow.model.checkout.Amount
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.PurchaseItems
import ish.oncourse.willow.model.checkout.request.PurchaseItemsRequest
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.service.CheckoutApi
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.configuration.server.ServerRuntime

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response


@CompileStatic
class CheckoutApiImpl implements CheckoutApi {
    
    final static  Logger logger = LoggerFactory.getLogger(CheckoutApiImpl.class)


    private ServerRuntime cayenneRuntime
    private CollegeService collegeService

    @Inject
    CheckoutApiImpl(ServerRuntime cayenneRuntime, CollegeService collegeService) {
        this.cayenneRuntime = cayenneRuntime
        this.collegeService = collegeService
    }

    @Override
    CheckoutModel calculateAmount(CheckoutModel checkoutModel) {
        
        ObjectContext context = cayenneRuntime.newContext()
        College college = collegeService.college
        
        ProcessCheckoutModel processModel = new ProcessCheckoutModel(context, college, checkoutModel).process()
        CheckoutModel result = processModel.checkoutModel
        result.amount = new Amount().with { a ->
            a.total = processModel.total.toPlainString()
            a.owing = processModel.owing.toPlainString()
            a.payNow = processModel.payNow.toPlainString()
            a.discount = processModel.totalDiscount.toPlainString()
            a
        }
        result
    }
    

    @Override
    PurchaseItems getPurchaseItems(PurchaseItemsRequest purchaseItemsRequest) {

        if (purchaseItemsRequest.classIds.empty && purchaseItemsRequest.productIds.empty) {
            logger.error('There are not selected items for purchase')
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'there are not selected items for purchase')).build())
        }
        
        ObjectContext context = cayenneRuntime.newContext()
        College college = collegeService.college
        
        Contact contact = new GetContact(context, college, purchaseItemsRequest.contactId).get()
        
        PurchaseItems items = new PurchaseItems()
        items.contactId = contact.id.toString()
        ProcessClasses processClasses = new ProcessClasses(context, contact, college, purchaseItemsRequest.classIds, purchaseItemsRequest.promotionIds).process()
        items.enrolments = processClasses.enrolments
        items.applications = processClasses.applications
        
        ProcessProducts processProducts = new ProcessProducts(context, contact, college, purchaseItemsRequest.productIds).process()
        items.articles += processProducts.articles
        items.memberships += processProducts.memberships
        items.vouchers += processProducts.vouchers
        items
    }
}
