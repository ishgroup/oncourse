package ish.oncourse.willow.checkout.corporatepass

import com.google.inject.Inject
import ish.oncourse.services.preference.IsCorporatePassEnabled
import ish.oncourse.willow.cayenne.CayenneService
import ish.oncourse.willow.model.checkout.corporatepass.CorporatePass
import ish.oncourse.willow.model.checkout.corporatepass.GetCorporatePassRequest
import ish.oncourse.willow.model.checkout.corporatepass.MakeCorporatePassRequest
import ish.oncourse.willow.service.CorporatePassApi
import ish.oncourse.willow.service.impl.CollegeService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
        return null
    }

    @Override
    Boolean isCorporatePassEnabled() {
        new IsCorporatePassEnabled(collegeService.college, cayenneService.newContext())
    }

    @Override
    void makeCorporatePass(MakeCorporatePassRequest request) {

    }
}
