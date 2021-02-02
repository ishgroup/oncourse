package ish.oncourse.willow.billing.filter

import com.google.inject.Inject
import ish.oncourse.api.access.AuthFilter
import ish.oncourse.api.access.SessionCookie
import ish.oncourse.api.request.RequestService
import org.eclipse.jetty.server.Request

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter

@AuthFilter
class SessionFilter  implements ContainerRequestFilter {

    RequestService requestService
    @Inject
    AuthenticationFilter(
            RequestService requestService) {
        this.requestService = requestService
    }
    
    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
        SessionCookie sessionCookie = SessionCookie.valueOf(requestService.request as Request)
        


    }
}
