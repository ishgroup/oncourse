package ish.oncourse.willow.checkout.corporatepass

import com.google.inject.Inject
import ish.oncourse.model.College
import ish.oncourse.model.WebSite
import ish.oncourse.services.preference.IsCorporatePassEnabled
import ish.oncourse.willow.cayenne.CayenneService
import ish.oncourse.willow.model.checkout.corporatepass.CorporatePass
import ish.oncourse.willow.model.checkout.corporatepass.GetCorporatePassRequest
import ish.oncourse.willow.model.checkout.corporatepass.MakeCorporatePassRequest
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.service.CorporatePassApi
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class CorporatePassApiImpl implements CorporatePassApi {
    
    final static  Logger logger = LoggerFactory.getLogger(CorporatePassApiImpl)
    
    private CayenneService cayenneService
    private CollegeService collegeService

    @Inject
    CheckoutApiImpl(CayenneService cayenneService, CollegeService collegeService) {
        this.cayenneService = cayenneService
        this.collegeService = collegeService
    }
    
    @Override
    CorporatePass getCorporatePass(GetCorporatePassRequest request) {
        if (!corporatePassEnabled) {
            logger.error("Attempt to use corporate pass, college id: ${collegeService.college.id}")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'CorporatePass payment method is not enabled for this college.')).build())
        }

        SearchByPass searchByPass = new SearchByPass(cayenneService.newContext(), collegeService.college, request).get()
        
        if (searchByPass.validationError.fieldsErrors.empty && searchByPass.validationError.formErrors.empty) {
            return searchByPass.result   
        } else {
            throw new BadRequestException(Response.status(400).entity(searchByPass.validationError).build())
        }
    }

    @Override
    Boolean isCorporatePassEnabled() {
        new IsCorporatePassEnabled(collegeService.college, cayenneService.newContext())
    }

    @Override
    void makeCorporatePass(MakeCorporatePassRequest request) {
        if (!corporatePassEnabled) {
            logger.error("Attempt to use corporate pass, college id: ${collegeService.college.id}")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'CorporatePass payment method is not enabled for this college.')).build())
        }
        
        ObjectContext context = cayenneService.newContext()
        WebSite webSite = collegeService.webSite
        College college = webSite.college

    }
}
