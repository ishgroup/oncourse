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
import org.apache.commons.lang.StringUtils

import javax.servlet.http.Cookie
import static ish.oncourse.willow.editor.services.access.AuthenticationStatus.*

@Singleton
class AuthenticationService {

    private ICayenneService cayenneService
    private RequestService requestService
    private ZKSessionManager sessionManager
    
    private static final String SESSION_ID = 'ESESSIONID'
    
    private static final long MAX_AGE = 14400


    @Inject
    AuthenticationService(ICayenneService cayenneService, RequestService requestService, ZKSessionManager sessionManager) {
        this.cayenneService = cayenneService
        this.requestService = requestService
        this.sessionManager = sessionManager
    }
    
    private AuthenticationResult succedAuthentication(Class userType, PersistentObject user, boolean persist) {
        AuthenticationResult result = fillUserName(userType, user)
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
    
    private AuthenticationResult fillUserName(Class userType, PersistentObject user) {
        switch (userType) {
            case WillowUser:
                WillowUser willowUser = (user as WillowUser)
                return AuthenticationResult.valueOf(null, willowUser.firstName,  willowUser.lastName)
            case SystemUser:
                SystemUser systemUser =  (user as SystemUser)
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
        Cookie sessionCookie = requestService.request.cookies.find {it.name == SESSION_ID}
        
        if (sessionCookie
                && sessionCookie.value
                && sessionCookie.value.split('-')[0] == WillowUser.simpleName 
                && (!isPersist ||  sessionManager.sessionExist(sessionCookie.value.replace('&', '/')))) {
            SelectById.query(WillowUser, sessionCookie.value.split('-')[1]).selectOne(cayenneService.newContext())
        } else {
            return null
        }
    }

    SystemUser getSystemUser(boolean isPersist = true) {
        Cookie sessionCookie = requestService.request.cookies.find {it.name == SESSION_ID}

        if (sessionCookie 
                && sessionCookie.value
                && sessionCookie.value.split('-')[0] == SystemUser.simpleName
                && (!isPersist || sessionManager.sessionExist(sessionCookie.value.replace('&', '/')))) {
            ObjectContext context = cayenneService.newContext()
            SystemUser user = SelectById.query(SystemUser, sessionCookie.value.split('-')[1])
                    .selectOne(context)
            if (user && user.college == WebSiteFunctions.getCurrentCollege(requestService.request, context)) {
                return user
            } else {
                return null
            }
        } else {
            return null
        }
    }

    void logout() {
        requestService.response.addSetCookie(SESSION_ID,
                null,
                requestService.request.serverName,
                requestService.request.contextPath,
                0, null, false, false, 0)  
    }
    
    static class AuthenticationResult {
        private AuthenticationStatus status
        private String firstName
        private String lastName

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

        static AuthenticationResult valueOf(AuthenticationStatus status, String firstName = null, String lastName = null) {
            AuthenticationResult result = new AuthenticationResult()
            result.status = status
            result.firstName = firstName
            result.lastName = lastName
            result
        }
    }
}
