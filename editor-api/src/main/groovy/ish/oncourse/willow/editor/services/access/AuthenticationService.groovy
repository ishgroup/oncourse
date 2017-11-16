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
    
    private static final String SESSION_ID = 'SESSIONID'

    @Inject
    AuthenticationService(ICayenneService cayenneService, RequestService requestService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
    }
    
    private AuthenticationResult succedAuthentication(Class ssoClass, PersistentObject user) {
        
        String firstName
        String lastName
        
        switch (ssoClass) {
            case WillowUser:
                WillowUser willowUser = (user as WillowUser)
                firstName = willowUser.firstName
                lastName = willowUser.lastName
                break
            case SystemUser:
                SystemUser systemUser =  (user as SystemUser)
                firstName = systemUser.firstName
                lastName = systemUser.surname
                break
            default: throw new IllegalArgumentException("Unsupported user type:  $ssoClass, persistent object: $user")    
        }
        
        requestService.request.cookies = [new Cookie(SESSION_ID, "${ssoClass.simpleName}-${user.objectId.properties['id']}")] as Cookie[]
        return AuthenticationResult.valueOf(SUCCESS, firstName, lastName)
    }

    AuthenticationResult authenticate(String userName, String password) {
        if (StringUtils.trimToNull(userName) == null || StringUtils.trimToNull(password) == null) {
            return INVALID_CREDENTIALS
        }
        // try authenticate by email using SystemUser table
        AuthenticationResult response = authenticateByEmail(userName, password)

        // if failed then try authenticate SystemUser by login
        if (NO_MATCHING_USER == response.status) {
            response = authenticateByLogin(userName, password)
        }

        // if SystemUser authentication failed then try authenticate using WillowUser table
        if (NO_MATCHING_USER == response.status) {
            response = authenticateSuperUser(userName, password)
        }

        return response
    }

    private AuthenticationResult authenticateSuperUser(String userName, String password) {

        List<WillowUser> users = ObjectSelect.query(WillowUser).
                where(WillowUser.EMAIL.eq(userName)).
                select(cayenneService.newContext())

        if (users.empty) {
            return AuthenticationResult.valueOf(NO_MATCHING_USER)
        }

        if (users.size() > 1) {
            return MORE_THAN_ONE_USER
        }

        WillowUser user = users[0]

        if (password == user.password) {
            return succedAuthentication(WillowUser, user)
        } else {
            return AuthenticationResult.valueOf(INVALID_CREDENTIALS)
        }
    }

    private AuthenticationResult authenticateByEmail(String email, String password) {
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
            return succedAuthentication(SystemUser, user)
        } else {
            return AuthenticationResult.valueOf(INVALID_CREDENTIALS)
        }
    }

    private AuthenticationResult authenticateByLogin(String login, String password) {

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
            return succedAuthentication(SystemUser, user)
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
    
    WillowUser getUser() {
        Cookie sessionCookie = requestService.request.cookies.find {it.name == SESSION_ID}
        if (sessionCookie && sessionCookie.value.split('-')[0] == WillowUser.simpleName) {
            SelectById.query(WillowUser, sessionCookie.value.split('-')[1]).selectOne(cayenneService.newContext())
        } else {
            return null
        }
    }

    SystemUser getSystemUser() {
        Cookie sessionCookie = requestService.request.cookies.find {it.name == SESSION_ID}

        if (sessionCookie && sessionCookie.value.split('-')[0] == SystemUser.simpleName) {
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
        requestService.request.cookies = [] as Cookie[]
    }


    String getUserEmail() {
        return getSystemUser() ? getSystemUser().email:
                getUser() != null ? getUser().email : null
    }

    static class AuthenticationResult {
        
        private AuthenticationStatus status
        private String firstName
        private String lastName

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
