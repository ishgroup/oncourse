package ish.oncourse.willow.editor.services.access

import com.google.inject.Inject
import com.google.inject.Singleton
import ish.oncourse.model.College
import ish.oncourse.model.SystemUser
import ish.oncourse.model.WillowUser
import ish.oncourse.services.authentication.AuthenticationResult
import ish.oncourse.services.authentication.CheckBasicAuth
import ish.oncourse.services.authentication.IAuthenticationService
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.website.WebSiteFunctions
import ish.security.AuthenticationUtil
import ish.util.SecurityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistentObject
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.jetty.server.Request

import javax.servlet.http.Cookie

import static ish.oncourse.services.authentication.AuthenticationStatus.*

@Singleton
class AuthenticationService implements IAuthenticationService {

    private ICayenneService cayenneService
    private RequestService requestService
    private ZKSessionManager sessionManager

    private SystemUser systemUser = null
    private WillowUser willowUser = null
    
    private static final String SESSION_ID = 'ESESSIONID'
    
    private static final long MAX_AGE = 14400
    private static final Logger logger = LogManager.logger

    @Inject
    AuthenticationService(ICayenneService cayenneService, RequestService requestService, ZKSessionManager sessionManager) {
        this.cayenneService = cayenneService
        this.requestService = requestService
        this.sessionManager = sessionManager
    }
    
    private AuthenticationResult succedAuthentication(Class userType, PersistentObject user, boolean persist) {
        AuthenticationResult result = fillUser(userType, user)
        String userId = "${userType.simpleName}-${user.objectId.idSnapshot['id']}"
        String sessionId = SecurityUtil.generateRandomPassword(20)
        
        if (persist) {
            sessionManager.persistSession(userId, sessionId)
        }
        
        requestService.response.addSetCookie(SESSION_ID, "$userId&$sessionId",
                requestService.request.serverName, 
                requestService.request.contextPath,
                MAX_AGE, 'Session identifier', false, false, 0)

        result.status = SUCCESS
        return result
    }
    
    private AuthenticationResult fillUser(Class userType, PersistentObject user) {
        switch (userType) {
            case WillowUser:
                willowUser = (user as WillowUser)
                return AuthenticationResult.valueOf(null, willowUser.firstName,  willowUser.lastName)
            case SystemUser:
                systemUser =  (user as SystemUser)
                return AuthenticationResult.valueOf(null, systemUser.firstName,  systemUser.surname)
            default: 
                throw new IllegalArgumentException("Unsupported user type:  $userType, persistent object: $user")
        }
    }

    AuthenticationResult authenticate(String userName, String password, boolean persist) {
        
        if (StringUtils.trimToNull(userName) == null || StringUtils.trimToNull(password) == null) {
            return AuthenticationResult.valueOf(INVALID_CREDENTIALS)
        }
        // try authenticate by email using SystemUser table
        AuthenticationResult response = authenticateByEmail(userName, password, persist)

        // if failed then try authenticate SystemUser by login
        if (NO_MATCHING_USER == response.status) {
            response = authenticateByLogin(userName, password, persist)
        }

        // if SystemUser authentication failed then try authenticate using WillowUser table
        if (NO_MATCHING_USER == response.status) {
            response = authenticateSuperUser(userName, password, persist)
        }
        return response
    }

    private AuthenticationResult authenticateSuperUser(String userName, String password, boolean persist) {

        List<WillowUser> users = ObjectSelect.query(WillowUser).
                where(WillowUser.EMAIL.eq(userName)).
                select(cayenneService.newContext())

        if (users.empty) {
            return AuthenticationResult.valueOf(NO_MATCHING_USER)
        }

        if (users.size() > 1) {
            return AuthenticationResult.valueOf(MORE_THAN_ONE_USER)
        }

        WillowUser user = users[0]

        if (password == user.password) {
            return succedAuthentication(WillowUser, user, persist)
        } else {
            return AuthenticationResult.valueOf(INVALID_CREDENTIALS)
        }
    }

    private AuthenticationResult authenticateByEmail(String email, String password, boolean persist) {
        ObjectContext context = cayenneService.newContext()
        College college = WebSiteFunctions.getCurrentCollege(requestService.request,context)

        List<SystemUser> systemUsers = (ObjectSelect.query(SystemUser).
                where(SystemUser.COLLEGE.eq(college)) & SystemUser.EMAIL.eq(email)).
                select(context)

        if (systemUsers.empty) {
            return AuthenticationResult.valueOf(NO_MATCHING_USER)
        }

        if (systemUsers.size() > 1) {
            return AuthenticationResult.valueOf(MORE_THAN_ONE_USER)
        }

        SystemUser user = systemUsers[0]

        if (tryAuthenticate(user, password)) {
            return succedAuthentication(SystemUser, user, persist)
        } else {
            return AuthenticationResult.valueOf(INVALID_CREDENTIALS)
        }
    }

    private AuthenticationResult authenticateByLogin(String login, String password, boolean persist) {

        ObjectContext context = cayenneService.newContext()
        College college = WebSiteFunctions.getCurrentCollege(requestService.request, context)


        List<SystemUser> systemUsers = (ObjectSelect.query(SystemUser).
                where(SystemUser.COLLEGE.eq(college))
                & SystemUser.LOGIN.eq(login)).
                select(context)

        if (systemUsers.empty) {
            return AuthenticationResult.valueOf(NO_MATCHING_USER)
        }

        if (systemUsers.size() > 1) {
            return AuthenticationResult.valueOf(MORE_THAN_ONE_USER)
        }

        SystemUser user = systemUsers[0]

        if (tryAuthenticate(user, password)) {
            return succedAuthentication(SystemUser, user, persist)
        } else {
            return AuthenticationResult.valueOf(INVALID_CREDENTIALS)
        }
    }

    private boolean tryAuthenticate(SystemUser user, String password) {
        if (AuthenticationUtil.isValidPasswordHash(user.password)) {
            // normal authenticatioin procedure
            return AuthenticationUtil.checkPassword(password, user.password)
        }

        // fallback to old password hashing mechanism
        return AuthenticationUtil.checkOldPassword(password, user.password)
    }
    
    String getUserEmail() {
        SystemUser sysUser = getSystemUser(false)
        if (sysUser) {
            return sysUser.email
        } else {
            WillowUser wilUser = getWillowUser(false)
            if (wilUser) {
                return wilUser.email
            }
            return null
        }
    }
    
    WillowUser getWillowUser(boolean isPersist = true) {
        SessionCookie sessionCookie = SessionCookie.valueOf(requestService.request)
        
        if (sessionCookie.exist
                && sessionCookie.userType == WillowUser.simpleName 
                && (!isPersist || sessionManager.sessionExist(sessionCookie.sessionNode))) {
            SelectById.query(WillowUser, sessionCookie.userId).selectOne(cayenneService.newContext())
        } else {
            CheckBasicAuth.valueOf(this, requestService.request).check()
            return willowUser
        }
    }

    SystemUser getSystemUser(boolean isPersist = true) {
        SessionCookie sessionCookie = SessionCookie.valueOf(requestService.request)

        if (sessionCookie.exist 
                && sessionCookie.userType == SystemUser.simpleName
                && (!isPersist || sessionManager.sessionExist(sessionCookie.sessionNode))) {
            ObjectContext context = cayenneService.newContext()
            SystemUser user = SelectById.query(SystemUser, sessionCookie.userId)
                    .selectOne(context)
            if (user && user.college == WebSiteFunctions.getCurrentCollege(requestService.request, context)) {
                return user
            } else {
                return null
            }
        } else {
            CheckBasicAuth.valueOf(this, requestService.request).check()
            return systemUser
        }
    }
    
    void logout() {
        requestService.response.addSetCookie(SESSION_ID,
                null,
                requestService.request.serverName,
                requestService.request.contextPath,
                0, null, false, false, 0)  
    }

    static class SessionCookie {
        
        private String userType
        private Long userId
        private String sessionNode
        private boolean exist = true

        private SessionCookie(){}

        static SessionCookie valueOf(Request request) {
            SessionCookie sessionCookie = new SessionCookie()
            
            Cookie cookie = request.cookies.find { it.name == SESSION_ID }
            if (cookie && cookie.value && StringUtils.trimToNull(cookie.value)) {
                String value = cookie.value
                sessionCookie.sessionNode = "/${value.replace('&', '/')}"
                sessionCookie.userId = Long.valueOf(value.split('&')[0].split('-')[1])
                sessionCookie.userType = value.split('&')[0].split('-')[0]
            } else {
                sessionCookie.exist = false
            }
            return sessionCookie
        }

        
        String getUserType() {
            return userType
        }

        void setUserType(String useType) {
            this.userType = useType
        }

        Long getUserId() {
            return userId
        }

        void setUserId(Long useId) {
            this.userId = useId
        }

        String getSessionNode() {
            return sessionNode
        }

        void setSessionNode(String sessionNode) {
            this.sessionNode = sessionNode
        }

        boolean getExist() {
            return exist
        }

        void setExist(boolean exist) {
            this.exist = exist
        }
    }
}
