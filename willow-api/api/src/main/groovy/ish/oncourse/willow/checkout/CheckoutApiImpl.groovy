package ish.oncourse.willow.checkout

import com.google.inject.Inject
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.checkout.functions.ProcessClasses
import ish.oncourse.willow.model.checkout.PurchaseItems
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.service.CheckoutApi
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.configuration.server.ServerRuntime

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

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
    PurchaseItems getPurchaseItems(String contactId, List<String> classesIds, List<String> productIds, List<String> promotionIds) {

        if (classesIds.empty && productIds.empty) {
            logger.error('There are not selected items for purchase')
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'there are not selected items for purchase')).build())
        }
        
        ObjectContext context = cayenneRuntime.newContext()
        College college = collegeService.college
        
        Contact contact = new GetContact(context, college, contactId).get()
        
        PurchaseItems items = new PurchaseItems()
        ProcessClasses processClasses = new ProcessClasses(context, contact, college, classesIds, promotionIds).process()
        items.enrolments = processClasses.enrolments
        items.applications = processClasses.applications
        items

    }
}
