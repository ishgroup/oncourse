package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.College
import ish.oncourse.model.WebSite
import ish.oncourse.services.site.GetWebSite
import ish.oncourse.willow.filters.RequestFilter

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
        new GetWebSite(RequestFilter.ThreadLocalXOrigin.get(), cayenneService.sharedContext()).get()
    }
    
}
