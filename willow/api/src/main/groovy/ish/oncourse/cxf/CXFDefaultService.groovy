package ish.oncourse.cxf

import com.google.inject.Inject

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

class CXFDefaultService {
    private CXFModuleConfig config

    @Inject
    CXFDefaultService(CXFModuleConfig config) {
        this.config = config
    }

    @GET()
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    String get() {
        return config.welcomeText
    }
}
