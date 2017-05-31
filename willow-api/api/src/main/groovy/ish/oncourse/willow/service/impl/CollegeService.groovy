package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import ish.oncourse.model.College
import ish.oncourse.model.WebSite
import ish.oncourse.services.site.GetWebSite
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.cayenne.CayenneService

class CollegeService {

    private CayenneService cayenneService

    @Inject
    CollegeService(CayenneService cayenneService) {
        this.cayenneService = cayenneService
    }
    
    College getCollege() {
        getWebSite().college
    }

    WebSite getWebSite() {
        new GetWebSite(RequestFilter.ThreadLocalXOrigin.get(), cayenneService.newContext()).get()
    }
    
}
