package ish.oncourse.services.authentication

interface IAuthenticationService {
    AuthenticationResult authenticate(String userName, String password, boolean persist)
}