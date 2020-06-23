package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.WebSite
import ish.oncourse.services.site.GetWebSite
import ish.oncourse.willow.filters.RequestFilter
import org.apache.cayenne.query.SelectById

class CollegeService {

    private CayenneService cayenneService

    @Inject
    CollegeService(CayenneService cayenneService) {
        this.cayenneService = cayenneService
    }
    
    College getCollege() {
        return webSite?.college ?: payer?.college
    }

    Contact getPayer() {
        Long payerId = RequestFilter.ThreadLocalPayerId.get()
        if (payerId) {
            SelectById.query(Contact, payerId).selectOne(cayenneService.sharedContext())
        } else {
            return null
        }
    }

    WebSite getWebSite() {
        String siteKey = RequestFilter.ThreadLocalSiteKey.get()
        if (siteKey) {
            return new GetWebSite(siteKey, cayenneService.sharedContext()).get()
        } else {
            return null
        }
    }
    
}
