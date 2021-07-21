package ish.oncourse.willow.portal.service.impl

import ish.oncourse.willow.billing.v1.service.AuthenticationApi
import ish.oncourse.willow.portal.v1.model.LoginRequest
import ish.oncourse.willow.portal.v1.model.LoginResponse

class AuthenticationApiImpl implements AuthenticationApi{

    @Override
    void forgotPassword(String email) {

    }

    @Override
    LoginResponse signIn(LoginRequest details) {
        
        if (details.ssOProvider) {
            
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
