package ish.oncourse.willow.billing.filter

import com.google.inject.Inject
import ish.oncourse.api.access.SessionCookie
import ish.oncourse.api.request.RequestService

import javax.ws.rs.ClientErrorException
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.Response

abstract class BillingSessionFilter implements ContainerRequestFilter {
    
    static final Integer SESSION_MAX_AGE = 3600

    protected RequestService requestService
    protected ZKSessionManager sessionManager

    @Inject
    BillingSessionFilter(RequestService requestService, ZKSessionManager sessionManager) {
        this.requestService = requestService
        this.sessionManager = sessionManager
    }
    
    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
        SessionCookie sessionCookie = SessionCookie.valueOf(requestService.request)
        if (sessionCookie.exist) {
            String error = verifySession(sessionCookie)
            if (error) {
                throw new ClientErrorException(error, Response.Status.UNAUTHORIZED)
            }
        } else if (authToken)  {
            String error = authentificate(authToken)
            if (error) {
                throw new ClientErrorException(error, Response.Status.NOT_ACCEPTABLE)
            }
            createSession()
        } else {
            throw new ClientErrorException('Request rejected, no auth token provided', Response.Status.UNAUTHORIZED)
        }
        
    }

    protected String verifySession(SessionCookie sessionCookie) {
        if (!sessionManager.sessionExist(sessionCookie.sessionNode)) {
            requestService.setSessionToken(null,0)
            return "User session invalid"
        } else {
            return null
        }
    }
    
    abstract protected String authentificate(String token)

    abstract protected String getAuthToken()
    
    abstract protected void createSession()
    
}
