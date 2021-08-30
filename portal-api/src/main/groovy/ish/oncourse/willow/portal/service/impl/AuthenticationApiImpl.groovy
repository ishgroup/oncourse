package ish.oncourse.willow.portal.service.impl

import com.google.inject.Inject
import com.nulabinc.zxcvbn.Strength
import com.nulabinc.zxcvbn.Zxcvbn
import ish.oncourse.api.request.RequestService
import ish.oncourse.model.User
import ish.oncourse.willow.portal.auth.FacebookOAuthProvider
import ish.oncourse.willow.portal.auth.GoogleOAuthProveder
import ish.oncourse.willow.portal.auth.LoginException
import ish.oncourse.willow.portal.auth.MicrosoftOAuthProvider
import ish.oncourse.willow.portal.auth.OAuthProvider
import ish.oncourse.willow.portal.auth.SSOCredantials
import ish.oncourse.willow.portal.auth.ZKSessionManager
import ish.oncourse.willow.portal.service.UserService
import ish.oncourse.willow.portal.v1.model.LoginRequest
import ish.oncourse.willow.portal.v1.model.LoginResponse
import ish.oncourse.willow.portal.v1.model.LoginStage
import ish.oncourse.willow.portal.v1.model.PasswordComplexity
import ish.oncourse.willow.portal.v1.model.SSOproviders
import ish.oncourse.willow.portal.v1.service.AuthenticationApi
import ish.security.AuthenticationUtil
import ish.util.SecurityUtil
import org.apache.zookeeper.CreateMode
import ish.oncourse.willow.portal.v1.model.User as UserDTO
import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

import static ish.oncourse.willow.portal.v1.model.LoginStage.*

class AuthenticationApiImpl implements AuthenticationApi{

    @Inject
    GoogleOAuthProveder googleOAuthProveder
    
    @Inject
    MicrosoftOAuthProvider microsoftOAuthProvider
    
    @Inject
    FacebookOAuthProvider facebookOAuthProvider


    @Inject 
    RequestService requestService
    
    @Inject
    UserService userService
    
    @Inject
    ZKSessionManager sessionManager
    
    @Override
    void signOut() {
        sessionManager.removeSessions(requestService.user)
    }

    @Override
    Map<String, String> ssoClientIds() {
        return [(SSOproviders.GOOGLE.toString()) : googleOAuthProveder.clientId,
                (SSOproviders.MICROSOFT.toString()): microsoftOAuthProvider.clientId,
                (SSOproviders.FACEBOOK.toString()): facebookOAuthProvider.clientId]
    }
    
    @Override
    void verifyEmail(String email) {
        LoginStage stage 
        User user = userService.getUserByEmail(email)
        if (user) {
            stage = PASSWORD
            user.emailVerified = false
            user.passwordHash = null
            user.objectContext.commitChanges()
        } else {
            stage = CREATE
            user = userService.createUser(email)
        }
        String sessionToken = createSession(user)
        userService.sendVerificationEmail(email, sessionToken, stage)
    }

    @Override
    PasswordComplexity checkPassword(String password) {
        Strength strength = new Zxcvbn().measure(password)

        return new PasswordComplexity().with { pc ->
            pc.score = strength.score
            if (score <= 2) {
                pc.feedback = strength.feedback.warning ?: strength.feedback.suggestions[0]
            }
            pc
        }
    }

    @Override
    LoginResponse login(LoginRequest details) {
        User user
        
        //authentificate by email/password
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
                throw new LoginException('Wrong email or password')
            }
        }
        
        //user procced by verification URL 
        else if (details.verificationUrl) {
            user = userService.getUserByVerificationUrl(details.verificationUrl)
            if (!user) {
                throw new LoginException('Login link has expired')
            }
            
            // reset password
            if (details.password) {
                if (checkPassword(details.password).score >= 2) {
                    user.passwordHash = AuthenticationUtil.generatePasswordHash(details.password)
                } else {
                    throw new LoginException('Password does not satisfy complexity restrictions.')
                }
            } 
            // sign up via SSO provider     
            else if (details.ssOProvider) {
                SSOCredantials credantials = getSSOCredantials(details)
                if (user.email == credantials.email) {
                    userService.updateCredantials(user, credantials)
                } else {
                    //do not allowe to change User email
                    throw new LoginException('Wrong login details')
                }
            }
            
            user.emailVerified = true
            user.lastLogin = new Date()
            user.objectContext.commitChanges()
            
            return new LoginResponse(user: new UserDTO(id:user.id, email: user.email, profilePicture: user.profilePicture), token: createSession(user))
        }
        
        //SSO authentification
        else if (details.ssOProvider) {
            SSOCredantials credantials = getSSOCredantials(details)
            user = userService.getUserByEmail(credantials.email)?:userService.createUser(credantials.email)
            userService.updateCredantials(user, credantials)
            String sessionToken = createSession(user)
            
            if (user.emailVerified) // user already exist and email verified
            {
                user.lastLogin = new Date()
                user.objectContext.commitChanges()
                return new LoginResponse(user: new UserDTO(id:user.id, email: user.email, profilePicture: user.profilePicture), token: sessionToken)
            } else {
                userService.sendVerificationEmail(credantials.email, sessionToken, AUTHORIZE)
                return new LoginResponse(vefiryEmail: true)
            }
        }

        throw new LoginException('Wrong login details')
    }

  
    private SSOCredantials getSSOCredantials(LoginRequest details) {
        SSOCredantials credantials
        OAuthProvider authProvider
        switch (details.ssOProvider) {
            case SSOproviders.GOOGLE:
                authProvider = googleOAuthProveder
                break
            case SSOproviders.MICROSOFT:
                authProvider = microsoftOAuthProvider
                break
            case SSOproviders.FACEBOOK:
                authProvider = facebookOAuthProvider
                break
            default:
                throw new LoginException('Unsupported Authorization provider')
        }
        credantials = authProvider.authorize(details.ssOToken, requestService.requestUrl, details.codeVerifier)
        return credantials
    }

    String createSession(User user) {
        sessionManager.removeSessions(user)
        String sessionId = SecurityUtil.generateRandomPassword(20)
        return sessionManager.persistSession(user, sessionId, CreateMode.PERSISTENT)
    }
}
