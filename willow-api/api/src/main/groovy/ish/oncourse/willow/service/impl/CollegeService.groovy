package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import ish.oncourse.model.College
import ish.oncourse.services.site.GetWebSite
import org.apache.cayenne.configuration.server.ServerRuntime

class CollegeService {

    private ServerRuntime runtime

    @Inject
    CollegeService(ServerRuntime runtime) {
        this.runtime = runtime
    }
    
    College getCollege() {
       new GetWebSite(RequestFilter.ThreadLocalXOrigin.get(),runtime.newContext()).get().college
    }
}
