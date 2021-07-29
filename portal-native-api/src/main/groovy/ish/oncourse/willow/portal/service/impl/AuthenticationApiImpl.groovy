package ish.oncourse.willow.portal.service.impl

import com.google.inject.Inject
import ish.oncourse.api.request.RequestService
import ish.oncourse.model.User
import ish.oncourse.willow.portal.auth.GoogleOAuthProveder
import ish.oncourse.willow.portal.auth.SSOCredantials
import ish.oncourse.willow.portal.auth.ZKSessionManager
import ish.oncourse.willow.portal.service.UserService
import ish.oncourse.willow.portal.v1.model.LoginRequest
import ish.oncourse.willow.portal.v1.model.LoginResponse
import ish.oncourse.willow.portal.v1.model.SSOproviders
import ish.oncourse.willow.portal.v1.service.AuthenticationApi
import ish.util.SecurityUtil
import org.apache.zookeeper.CreateMode
import ish.oncourse.willow.portal.v1.model.User as UserDTO
import javax.ws.rs.BadRequestException

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
    void forgotPassword(String email) {

    }

    @Override
    LoginResponse login(LoginRequest details) {
        if (details.ssOProvider) {
            SSOCredantials credantials
            switch (details.ssOProvider) {
                case SSOproviders.GOOGLE:
                    credantials = googleOAuthProveder.authorize(details.ssOToken, requestService.requestUrl)
                    break
                default:
                    throw new BadRequestException('Unsupported Authorization provider')
            }
            User user = userService.getUserByEmail(credantials.email)?:userService.createUser(credantials.email)

            userService.updateCredantials(user, credantials)
            String sessionToken = createSession(user)
            if (user.emailVerified) {
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



    String createSession(User user) {
        String sessionId = SecurityUtil.generateRandomPassword(20)
        String userId = "$User.simpleName-$user.id"
        sessionManager.persistSession(userId, sessionId, CreateMode.PERSISTENT)
        return "$userId&$sessionId".toString()
    }
}
