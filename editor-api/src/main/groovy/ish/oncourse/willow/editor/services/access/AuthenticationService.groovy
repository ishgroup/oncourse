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

    @Inject
    AuthenticationService(ICayenneService cayenneService, RequestService requestService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
    }
    
    private AuthenticationStatus succedAuthentication(Class ssoClass, PersistentObject user) {
        requestService.request.cookies = [new Cookie('SESSIONID', "${ssoClass.simpleName}-${user.objectId.properties['id']}")] as Cookie[]
        return SUCCESS
    }

    AuthenticationStatus authenticate(String userName, String password) {
        if (StringUtils.trimToNull(userName) == null || StringUtils.trimToNull(password) == null) {
            return INVALID_CREDENTIALS
        }
        // try authenticate by email using SystemUser table
        AuthenticationStatus status = authenticateByEmail(userName, password)

        // if failed then try authenticate SystemUser by login
        if (NO_MATCHING_USER == status) {
            status = authenticateByLogin(userName, password)
        }

        // if SystemUser authentication failed then try authenticate using WillowUser table
        if (NO_MATCHING_USER == status) {
            status = authenticateSuperUser(userName, password)
        }

        return status
    }

    private AuthenticationStatus authenticateSuperUser(String userName, String password) {

        List<WillowUser> users = ObjectSelect.query(WillowUser).
                where(WillowUser.EMAIL.eq(userName)).
                select(cayenneService.newContext())

        if (users.empty) {
            return NO_MATCHING_USER
        }

        if (users.size() > 1) {
            return MORE_THAN_ONE_USER
        }

        WillowUser user = users[0]

        if (password == user.password) {
            return succedAuthentication(WillowUser, user)
        } else {
            return INVALID_CREDENTIALS
        }
    }

    private AuthenticationStatus authenticateByEmail(String email, String password) {

        College college = WebSiteFunctions.getCurrentCollege(requestService.request, cayenneService.sharedContext())

        List<SystemUser> systemUsers = (ObjectSelect.query(SystemUser).
                where(SystemUser.COLLEGE.eq(college)) & SystemUser.EMAIL.eq(email)).
                select(cayenneService.newContext())

        if (systemUsers.empty) {
            return NO_MATCHING_USER
        }

        if (systemUsers.size() > 1) {
            return MORE_THAN_ONE_USER
        }

        SystemUser user = systemUsers[0]

        if (tryAuthenticate(user, password)) {
            return succedAuthentication(SystemUser, user)
        } else {
            return INVALID_CREDENTIALS
        }
    }

    private AuthenticationStatus authenticateByLogin(String login, String password) {

        College college = WebSiteFunctions.getCurrentCollege(requestService.request, cayenneService.sharedContext())


        List<SystemUser> systemUsers = (ObjectSelect.query(SystemUser).
                where(SystemUser.COLLEGE.eq(college))
                & SystemUser.LOGIN.eq(login)).
                select(cayenneService.newContext())

        if (systemUsers.empty) {
            return NO_MATCHING_USER
        }

        if (systemUsers.size() > 1) {
            return MORE_THAN_ONE_USER
        }

        SystemUser user = systemUsers[0]

        if (tryAuthenticate(user, password)) {
            return succedAuthentication(SystemUser, user)
        } else {
            return INVALID_CREDENTIALS
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
        Cookie sessionCookie = requestService.request.cookies.find {it.name == 'SESSIONID'}
        if (sessionCookie && sessionCookie.value.split('-')[0] == WillowUser.simpleName) {
            SelectById.query(WillowUser, sessionCookie.value.split('-')[1]).selectOne(cayenneService.sharedContext())
        } else {
            return null
        }
    }

    SystemUser getSystemUser() {
        Cookie sessionCookie = requestService.request.cookies.find {it.name == 'SESSIONID'}

        if (sessionCookie && sessionCookie.value.split('-')[0] == SystemUser.simpleName) {
            ObjectContext context = cayenneService.sharedContext()
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
}
