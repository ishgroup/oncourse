package ish.oncourse.admin.services.access

import com.google.inject.Inject
import com.google.inject.Singleton
import ish.oncourse.admin.services.RequestService
import ish.oncourse.services.access.AuthenticationStatus
import ish.oncourse.services.authentication.AuthenticationResult
import ish.oncourse.services.authentication.CheckBasicAuth
import ish.oncourse.services.authentication.IAuthenticationService

import static ish.oncourse.services.authentication.AuthenticationStatus.*

@Singleton
class AuthenticationService implements IAuthenticationService {

    private RequestService requestService

    @Inject
    AuthenticationService(RequestService requestService) {
        requestService = requestService
    }

    AuthenticationResult authenticate(String userName, String password, boolean persist) {
        AuthenticationResult.valueOf(SUCCESS)
    }

    void logout() {

    }

    boolean getUser() {
        return AuthenticationStatus.SUCCESS == CheckBasicAuth.valueOf(this, requestService.request.getHeader('Authorization')).check()
    }
}
