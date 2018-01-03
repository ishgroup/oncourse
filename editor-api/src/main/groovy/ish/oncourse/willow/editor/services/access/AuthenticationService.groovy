package ish.oncourse.willow.editor.services.access

import com.google.inject.Inject
import com.google.inject.Singleton
import ish.oncourse.model.College
import ish.oncourse.model.SystemUser
import ish.oncourse.model.WillowUser
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.website.WebSiteFunctions
import ish.security.AuthenticationUtil
import ish.util.SecurityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistentObject
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.commons.codec.binary.Base64
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.jetty.server.Request

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

import static ish.oncourse.willow.editor.services.access.AuthenticationStatus.*

@Singleton
class AuthenticationService {

    private ICayenneService cayenneService
    private RequestService requestService
    private ZKSessionManager sessionManager
    
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
                WillowUser willowUser = (user as WillowUser)
                return AuthenticationResult.valueOf(null, willowUser.firstName,  willowUser.lastName, null, willowUser)
            case SystemUser:
                SystemUser systemUser =  (user as SystemUser)
                return AuthenticationResult.valueOf(null, systemUser.firstName,  systemUser.surname, systemUser, null)
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
            return checkBasicAuth().willowUser
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
            return checkBasicAuth().systemUser
        }
    }
    
    void logout() {
        requestService.response.addSetCookie(SESSION_ID,
                null,
                requestService.request.serverName,
                requestService.request.contextPath,
                0, null, false, false, 0)  
    }
    
    private AuthenticationResult checkBasicAuth() {
        String authHeader = requestService.request.getHeader('Authorization')
        if (org.apache.commons.lang3.StringUtils.trimToNull(authHeader)) {
            StringTokenizer st = new StringTokenizer(authHeader)
            if (st.hasMoreTokens()) {
                String basic = st.nextToken()
                if (basic.equalsIgnoreCase('Basic')) {
                    try {
                        String credentials = new String(Base64.decodeBase64(st.nextToken()), 'UTF-8')
                        int p = credentials.indexOf(':')
                        if (p != -1) {
                            String _username = credentials.substring(0, p).trim()
                            String _password = credentials.substring(p + 1).trim()
                            return authenticate(_username, _password, false)
                        }
                    } catch (UnsupportedEncodingException e) {
                        logger.catching(e)
                    }
                }
            }
        }
        return AuthenticationResult.valueOf(INVALID_CREDENTIALS)
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
    
    static class AuthenticationResult {
        private AuthenticationStatus status
        private String firstName
        private String lastName
        private SystemUser systemUser
        private WillowUser willowUser

        SystemUser getSystemUser() {
            return systemUser
        }

        void setSystemUser(SystemUser systemUser) {
            this.systemUser = systemUser
        }

        WillowUser getWillowUser() {
            return willowUser
        }

        void setWillowUser(WillowUser willowUser) {
            this.willowUser = willowUser
        }

        void setStatus(AuthenticationStatus status) {
            this.status = status
        }

        void setFirstName(String firstName) {
            this.firstName = firstName
        }

        void setLastName(String lastName) {
            this.lastName = lastName
        }

        AuthenticationStatus getStatus() {
            return status
        }

        String getFirstName() {
            return firstName
        }

        String getLastName() {
            return lastName
        }

        static AuthenticationResult valueOf(AuthenticationStatus status, String firstName = null, String lastName = null, SystemUser systemUser = null, WillowUser willowUser = null) {
            AuthenticationResult result = new AuthenticationResult()
            result.status = status
            result.firstName = firstName
            result.lastName = lastName
            result.systemUser = systemUser
            result.willowUser = willowUser
            result
        }
    }
}
