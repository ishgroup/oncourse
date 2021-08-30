package ish.oncourse.willow.portal.filter

import com.google.inject.Inject
import ish.oncourse.api.access.AuthFilter
import ish.oncourse.api.request.RequestService
import ish.oncourse.model.User
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.portal.auth.ZKSessionManager
import org.apache.cayenne.query.SelectById

import javax.ws.rs.ClientErrorException
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.Response
import java.util.regex.Matcher

@AuthFilter
class SessionFilter  implements ContainerRequestFilter {
    
    @Inject
    private ICayenneService cayenneService

    @Inject
    private ZKSessionManager sessionManager

    @Inject
    private RequestService requestService
    
    private static final String AUTH_HEADER = 'Authorization'


    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
        String sessionToken = requestService.request.getHeader(AUTH_HEADER)
        if (!sessionToken) {
            unauthorized()
        }
        Matcher tokenMatcher = (~/User-([0-9]+)&(.*)/).matcher(sessionToken)
        if (!tokenMatcher.find()) {
            unauthorized()
        }
        String id =  tokenMatcher.group(1)
        String sessionId = tokenMatcher.group(2)
        
        User user =  SelectById.query(User, Long.valueOf(id)).selectOne(cayenneService.newContext())
        if (!user || !user.emailVerified) {
            unauthorized()
        }
        if (sessionManager.sessionExist("/User-$id/${sessionId}")) {
            requestService.setUser(user)
        } else {
            unauthorized()
        }
        
    }
    
    static void unauthorized() {
        throw new ClientErrorException('Unauthorized', Response.Status.UNAUTHORIZED)
    }
}
