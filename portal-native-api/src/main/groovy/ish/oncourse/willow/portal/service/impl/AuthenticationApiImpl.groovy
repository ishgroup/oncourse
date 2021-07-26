package ish.oncourse.willow.portal.service.impl

import com.google.inject.Inject
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.portal.auth.GoogleOAuthProveder
import ish.oncourse.willow.portal.auth.SSOCredantials
import ish.oncourse.willow.portal.service.UserService
import ish.oncourse.willow.portal.v1.model.LoginRequest
import ish.oncourse.willow.portal.v1.model.LoginResponse
import ish.oncourse.willow.portal.v1.model.SSOproviders
import ish.oncourse.willow.portal.v1.service.AuthenticationApi

import javax.ws.rs.BadRequestException

class AuthenticationApiImpl implements AuthenticationApi{

    @Inject
    GoogleOAuthProveder googleOAuthProveder
    
    @Inject 
    RequestService requestService
    
    @Inject
    UserService user
    
    @Override
    void forgotPassword(String email) {

    }

    @Override
    LoginResponse signIn(LoginRequest details) {
        
        if (details.ssOProvider) {
            SSOCredantials credantials = null
            switch (details.ssOProvider) {
                case SSOproviders.GOOGLE:
                    credantials = googleOAuthProveder.authorize(details.ssOToken, requestService.requestUrl)
                    break
                default:
                    throw new BadRequestException('Unsupported Authorization provider')
            }
            
            
            
        }
        
        
        return null
    }

    @Override
    void signOut() {

    }

    @Override
    LoginResponse signUp(LoginRequest details) {
        return null
    }
}
