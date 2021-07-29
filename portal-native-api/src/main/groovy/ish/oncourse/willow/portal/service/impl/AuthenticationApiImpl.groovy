package ish.oncourse.willow.portal.service.impl

import com.google.inject.Inject
import ish.oncourse.api.request.RequestService
import ish.oncourse.model.User
import ish.oncourse.willow.portal.auth.GoogleOAuthProveder
import ish.oncourse.willow.portal.auth.SSOCredantials
import ish.oncourse.willow.portal.auth.ZKSessionManager
import ish.oncourse.willow.portal.service.UserService
import ish.oncourse.willow.portal.v1.model.ErrorResponse
import ish.oncourse.willow.portal.v1.model.LoginRequest
import ish.oncourse.willow.portal.v1.model.LoginResponse
import ish.oncourse.willow.portal.v1.model.SSOproviders
import ish.oncourse.willow.portal.v1.service.AuthenticationApi
import ish.security.AuthenticationUtil
import ish.util.SecurityUtil
import org.apache.zookeeper.CreateMode
import ish.oncourse.willow.portal.v1.model.User as UserDTO
import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class AuthenticationApiImpl implements AuthenticationApi{

    @Inject
    GoogleOAuthProveder googleOAuthProveder
    
    @Inject 
    RequestService requestService
    
    @Inject
    UserService userService
    
    @Inject
    ZKSessionManager sessionManager
    
    @Override
    void verifyEmail(String email) {
        String path 
        User user = userService.getUserByEmail(email)
        if (user) {
            path = '/password'
            user.emailVerified = false
            user.passwordHash = null
            user.objectContext.commitChanges()
        } else {
            path = '/create'
            user = userService.createUser(email)
        }
        String sessionToken = createSession(user)
        userService.sendVerificationEmail(email, sessionToken, path)
    }

    @Override
    LoginResponse login(LoginRequest details) {
        User user
        
        //authentificate by password
        if (details.email && details.password) {
            user = userService.getUserByEmail(details.email)
            if (user && 
                    user.emailVerified && 
                    user.passwordHash &&
                    AuthenticationUtil.checkPassword(details.password, user.passwordHash)) {
                user.setLastLogin(new Date())
                user.objectContext.commitChanges()
                createSession(user)
                return new LoginResponse(user: new UserDTO(id:user.id, email: user.email, profilePicture: user.profilePicture), token: createSession(user))

            } else {
                throw new BadRequestException(Response.status(400).entity(new ErrorResponse(message: 'Wrong email or password')).build())
            }
        }
        
        //SSO authentification
        if (details.ssOProvider) {
            SSOCredantials credantials
            switch (details.ssOProvider) {
                case SSOproviders.GOOGLE:
                    credantials = googleOAuthProveder.authorize(details.ssOToken, requestService.requestUrl)
                    break
                default:
                    throw new BadRequestException('Unsupported Authorization provider')
            }
            user = userService.getUserByEmail(credantials.email)?:userService.createUser(credantials.email)
            userService.updateCredantials(user, credantials)
            String sessionToken = createSession(user)
            
            if (user.emailVerified) // user already exist and email verified
            {
                user.lastLogin = new Date()
                user.objectContext.commitChanges()
                return new LoginResponse(user: new UserDTO(id:user.id, email: user.email, profilePicture: user.profilePicture), token: sessionToken)
            } else {
                userService.sendVerificationEmail(credantials.email, sessionToken)
                return new LoginResponse(vefiryEmail: true)
            }
        }

        return null
    }

    @Override
    void signOut() {
        
    }

    @Override
    Map<String, String> ssoClientIds() {
        return [SSOproviders.GOOGLE.toString() : googleOAuthProveder.clientId]
    }

    String createSession(User user) {
        sessionManager.removeSessions(user)
        String sessionId = SecurityUtil.generateRandomPassword(20)
        String userId = "$User.simpleName-$user.id"
        sessionManager.persistSession(userId, sessionId, CreateMode.PERSISTENT)
        return "$userId&$sessionId".toString()
    }
}
