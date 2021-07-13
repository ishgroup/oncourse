package ish.oncourse.willow.billing.filter

import com.google.inject.Inject
import groovy.transform.CompileDynamic
import ish.oncourse.api.access.GuestFilter
import ish.oncourse.api.access.SessionCookie
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.api.request.RequestService
import ish.oncourse.model.SystemUser
import ish.util.SecurityUtil
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.zookeeper.CreateMode


@GuestFilter
@CompileDynamic
class UserSessionFilter extends BillingSessionFilter {
    
    private CayenneService cayenneService
    
    private static final String AUTH_HEADER = 'Authorization'

    @Inject
    UserSessionFilter(RequestService requestService, ZKSessionManager sessionManager, CayenneService cayenneService) {
        super(requestService, sessionManager)
    }

    @Override
    protected String authentificate(String token) {
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
        String userId = "SystemUser-$requestService.systemUser.id"
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
