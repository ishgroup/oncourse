package ish.oncourse.willow.checkout

import com.google.inject.Inject
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.willow.model.checkout.PurchaseItems
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.service.CheckoutApi
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.StringUtils
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
        ObjectContext context = cayenneRuntime.newContext()
        College college = collegeService.college
        if (!StringUtils.trimToNull(contactId)) {
            logger.error("contactId required, request contactId param: $contactId")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'contactId required')).build())
        }
        Contact contact = SelectById.query(Contact, contactId).selectOne(context)
        
        if (!contact) {
            logger.error("contact is not exist, request param: $classesIds")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'contact is not exist')).build())
        } else if (!contact.student) {
            logger.error("contact has no student related, contact: $contact")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'contact has no student related')).build())
        }
        
        if (classesIds.empty && productIds.empty) {
            logger.error('There are not selected items for purchase')
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'there are not selected items for purchase')).build())
        }
        
        PurchaseItems items = new PurchaseItems()

        items
        
    }
}
