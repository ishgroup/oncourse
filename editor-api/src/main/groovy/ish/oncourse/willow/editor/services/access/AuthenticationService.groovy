package ish.oncourse.willow.editor.services.access

import com.google.inject.Inject
import com.google.inject.Singleton
import groovy.transform.CompileStatic
import ish.oncourse.api.access.SessionCookie
import ish.oncourse.model.College
import ish.oncourse.model.SystemUser
import ish.oncourse.model.WillowUser
import ish.oncourse.services.authentication.AuthenticationResult
import ish.oncourse.services.authentication.IAuthenticationService
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.editor.website.WebSiteFunctions
import ish.security.AuthenticationUtil
import ish.util.SecurityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistentObject
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang.StringUtils

import javax.servlet.http.Cookie

import static ish.oncourse.services.authentication.AuthenticationStatus.*

@Singleton
@CompileStatic
class AuthenticationService implements IAuthenticationService {

    private ICayenneService cayenneService
    private RequestService requestService
    private ZKSessionManager sessionManager
    private UserService userService

    
    private static final int MAX_AGE = 14400

    @Inject
    AuthenticationService(ICayenneService cayenneService, RequestService requestService, ZKSessionManager sessionManager, UserService userService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
        this.sessionManager = sessionManager
        this.userService = userService
    }
    
    private AuthenticationResult succedAuthentication(Class userType, PersistentObject user, boolean persist) {
        AuthenticationResult result = fillUser(userType, user)
        String userId = "${userType.simpleName}-${user.objectId.idSnapshot['id']}"
        String sessionId = SecurityUtil.generateRandomPassword(20)
        
        if (persist) {
            sessionManager.persistSession(userId, sessionId)
        }
        String sessionToken = "$userId&$sessionId".toString()
        setSessionToken(sessionToken, MAX_AGE)
        result.status = SUCCESS
        result.sessionToken = sessionToken
        return result
    }
    
    private AuthenticationResult fillUser(Class userType, PersistentObject user) {
        switch (userType) {
            case WillowUser:
                WillowUser willowUser = (user as WillowUser)
                userService.userFirstName = willowUser.firstName
                userService.userLastName = willowUser.lastName
                userService.userEmail = willowUser.email
                return AuthenticationResult.valueOf(null, willowUser.firstName,  willowUser.lastName)
            case SystemUser:
                SystemUser systemUser = (user as SystemUser)
                userService.userFirstName = systemUser.firstName
                userService.userLastName = systemUser.surname
                userService.userEmail = systemUser.email
                userService.systemUser = systemUser
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
        return AuthenticationUtil.checkPassword(password,user.password)
    }
    
    
    void logout() {
        setSessionToken(null, 0) 
    }
    
    private void setSessionToken(String value, int maxAge) {
        Cookie cookie = new Cookie(SessionCookie.SESSION_ID, value)
        cookie.domain =  requestService.request.serverName
        cookie.path = requestService.request.contextPath
        cookie.maxAge = maxAge
        cookie.comment = 'Session identifier'
        cookie.httpOnly = false
        cookie.secure = false
        cookie.version = 0
        
        requestService.response.addCookie(cookie)
    }
}
