package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import ish.oncourse.model.College
import ish.oncourse.model.WebSite
import ish.oncourse.services.site.GetWebSite
import ish.oncourse.willow.filters.RequestFilter
import org.apache.cayenne.configuration.server.ServerRuntime

class CollegeService {

    private ServerRuntime runtime

    @Inject
    CollegeService(ServerRuntime runtime) {
        this.runtime = runtime
    }
    
    College getCollege() {
        getWebSite().college
    }

    WebSite getWebSite() {
        new GetWebSite(RequestFilter.ThreadLocalXOrigin.get(),runtime.newContext()).get()
    }
    
}
