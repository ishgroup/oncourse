package ish.oncourse.willow.portal.service.impl

import com.google.inject.Inject
import ish.oncourse.willow.portal.auth.GoogleOAuthProveder
import ish.oncourse.willow.portal.v1.model.LoginRequest
import ish.oncourse.willow.portal.v1.model.LoginResponse
import ish.oncourse.willow.portal.v1.model.SSOproviders
import ish.oncourse.willow.portal.v1.service.AuthenticationApi

class AuthenticationApiImpl implements AuthenticationApi{

    @Inject
    GoogleOAuthProveder googleOAuthProveder
    
    
    
    @Override
    void forgotPassword(String email) {

    }

    @Override
    LoginResponse signIn(LoginRequest details) {
        
        if (details.ssOProvider) {
            
            switch (details.ssOProvider) {
                case SSOproviders.GOOGLE:
                    break
                case SSOproviders.FACEBOOK:
                    break
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
