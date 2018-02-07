package ish.oncourse.admin.services.access

import ish.oncourse.services.authentication.AuthenticationResult
import ish.oncourse.services.authentication.IAuthenticationService

import static ish.oncourse.services.authentication.AuthenticationStatus.SUCCESS

class AuthenticationService implements IAuthenticationService {

    AuthenticationResult authenticate(String userName, String password, boolean persist) {
        AuthenticationResult.valueOf(SUCCESS)
    }

    void logout() {
    }
}
