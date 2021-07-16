package ish.oncourse.willow.billing.filter

import com.google.inject.Inject
import groovy.transform.CompileDynamic
import ish.oncourse.api.access.AuthFilter
import ish.oncourse.api.access.SessionCookie
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.api.request.RequestService
import ish.oncourse.model.SystemUser
import ish.oncourse.services.persistence.ICayenneService
import ish.util.SecurityUtil
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.zookeeper.CreateMode

import javax.ws.rs.container.ContainerRequestContext


@AuthFilter
@CompileDynamic
class UserSessionFilter extends BillingSessionFilter {
    
    private ICayenneService cayenneService
    
    private static final String AUTH_HEADER = 'Authorization'

    @Inject
    UserSessionFilter(RequestService requestService, ZKSessionManager sessionManager, ICayenneService cayenneService) {
        super(requestService, sessionManager)
        this.cayenneService = cayenneService
    }
    @Override
    final void filter(ContainerRequestContext requestContext) throws IOException {
        super.filter(requestContext)
        requestService.response.addHeader('Access-Control-Allow-Origin', '*')
        requestService.response.addHeader('Access-Control-Allow-Methods', 'PUT, GET, OPTIONS, POST, DELETE')
        requestService.response.addHeader('Access-Control-Allow-Headers', '*')
    }
    @Override
    protected String authentificate(String token) {
        if (token.contains('.')) {
            token = token.split("\\.")[0]
        }
        SystemUser user = ObjectSelect.query(SystemUser)
                .where(SystemUser.SESSION_ID.eq(token))
                .and(SystemUser.IS_ADMIN.isTrue())
                .selectFirst(cayenneService.newContext())
        if (!user) {
            return 'Login unsuccessful. Invalid authentification data'
        } else {
            requestService.ThreadLocalUser.set(user)
        }
        return null
    }

    @Override
    protected String getAuthToken() {
        return requestService.request.getHeader(AUTH_HEADER)
    }

    @Override
    protected void createSession() {
        String sessionId = SecurityUtil.generateRandomPassword(20)
        String userId = "$SystemUser.simpleName-$requestService.systemUser.id"
        String sessionToken = "$userId&$sessionId".toString()

        sessionManager.persistSession(userId, sessionId, CreateMode.EPHEMERAL)
        requestService.setSessionToken(sessionToken, SESSION_MAX_AGE)
    }
    
    @Override
    String verifySession(SessionCookie sessionCookie) {
        String result = super.verifySession(sessionCookie)
        if (result) {
            return result
        }
        SystemUser user = null
        
        if (sessionCookie.userType != SystemUser.simpleName) {
            return 'User session invalid'
        }
        
        Long id = sessionCookie.getUserId()
        if (id) {
            user = SelectById.query(SystemUser,  id).selectOne(cayenneService.newContext())
        } 
        
        if (user?.isAdmin) {
            requestService.ThreadLocalUser.set(user)
            return null
        } else {
            return 'User session invalid'
        }
        
    }
}
