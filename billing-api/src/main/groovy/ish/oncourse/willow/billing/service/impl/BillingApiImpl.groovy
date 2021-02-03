package ish.oncourse.willow.billing.service.impl

import com.google.inject.Inject
import ish.oncourse.model.College
import ish.oncourse.model.WebSite
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.billing.v1.model.CollegeDTO
import ish.oncourse.willow.billing.v1.service.BillingApi
import org.apache.cayenne.query.ObjectSelect

class BillingApiImpl implements BillingApi {

    private ICayenneService cayenneService


    @Inject
    BillingApiImpl(ICayenneService cayenneService) {
        this.cayenneService = cayenneService
    }
    
    
    @Override
    void createCollege(CollegeDTO college) {
    }

    @Override
    Boolean verifyCollegeName(String name, String xGRecaptcha) {
        return ObjectSelect.query(College)
                .where(College.COLLEGE_KEY.eq(name))
                .or(College.WEB_SITES.dot(WebSite.NAME).eq(name))
                .select(cayenneService.newContext()).empty
    }
}
