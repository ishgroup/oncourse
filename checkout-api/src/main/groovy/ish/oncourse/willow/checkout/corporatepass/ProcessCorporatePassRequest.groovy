package ish.oncourse.willow.checkout.corporatepass

import ish.oncourse.model.College
import ish.oncourse.model.CorporatePass
import ish.oncourse.services.preference.IsCorporatePassEnabled
import ish.oncourse.willow.checkout.functions.ProcessCheckoutModel
import ish.oncourse.willow.checkout.payment.HasErrors
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.corporatepass.MakeCorporatePassRequest
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.common.FieldError
import ish.oncourse.willow.model.common.ValidationError
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class ProcessCorporatePassRequest {
    
    final static  Logger logger = LoggerFactory.getLogger(ProcessCorporatePassRequest)

    private ObjectContext context
    private College college
    private MakeCorporatePassRequest request

    private ProcessCheckoutModel processModel
    
    ProcessCorporatePassRequest(ObjectContext context, College college, MakeCorporatePassRequest request) {
        this.context = context
        this.college = college
        this.request = request
    }

    ProcessCorporatePassRequest process() {

        if (!new IsCorporatePassEnabled(college, context).get()) {
            logger.error("Attempt to use corporate pass, college id: ${college.id}")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'CorporatePass payment method is not enabled for this college.')).build())
        }
        
        if (!StringUtils.trimToNull(request.corporatePassId) ) {
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'Corporate pass required')).build())
        }

        request.checkoutModelRequest.corporatePassId = request.corporatePassId
        
        processModel = new ProcessCheckoutModel(context, college, request.checkoutModelRequest).process()
        
        if (processModel.model.error) {
            throw new BadRequestException(Response.status(400).entity(processModel.model).build())
        }
        
        if (new HasErrors(processModel.model).hasErrors()) {
            processModel.model.error = new CommonError(message: 'Purchase items are not valid')
            throw new BadRequestException(Response.status(400).entity(processModel.model).build())
        }

        if (!request.agreementFlag) {            
            ValidationError error  = new ValidationError()
            error.fieldsErrors << new FieldError(name: 'agreementFlag', error: 'You must agree to the policies before proceeding.')
            throw new BadRequestException(Response.status(400).entity(error).build())
        }
        
        return this
    }


    CorporatePass getPass() {
        return processModel.corporatePass
    }

    CheckoutModel getModel() {
        return processModel.model
    }
}
