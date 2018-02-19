package ish.oncourse.willow.editor.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.services.authentication.AuthenticationResult
import ish.oncourse.services.authentication.AuthenticationStatus
import ish.oncourse.services.persistence.ICayenneService;


import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.access.AuthenticationService
import ish.oncourse.willow.editor.services.access.UserService
import ish.oncourse.willow.editor.v1.model.LoginRequest
import ish.oncourse.willow.editor.v1.model.User
import ish.oncourse.willow.editor.v1.model.common.CommonError
import ish.oncourse.willow.editor.v1.service.AuthApi
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.*
import javax.ws.rs.core.Response

@CompileStatic
class AuthApiServiceImpl implements AuthApi {
    
    private static final Logger logger = LogManager.logger

    private ICayenneService cayenneService
    private AuthenticationService authenticationService
    private UserService userService

    @Inject
    AuthorizationApiServiceImpl(ICayenneService cayenneService, AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService
        this.cayenneService = cayenneService
        this.userService = userService
    }
    

    @Override
    User userGetGet() {
        if (userService.userFirstName) {
            return new User().firstName(userService.userFirstName).lastName(userService.userLastName)
        } else {
            logger.error('Unexpected error. There is no logged user.')
            throw new InternalServerErrorException('Unexpected error')
        }
    }

    @Override
    void userLoginPost(LoginRequest loginRequest) {
        AuthenticationResult result =  authenticationService.authenticate(loginRequest.email, loginRequest.password, true)
        switch (result.status) {
            case AuthenticationStatus.SUCCESS:
                break
            case AuthenticationStatus.INVALID_CREDENTIALS:
            case AuthenticationStatus.NO_MATCHING_USER:
                throw new ClientErrorException(Response.status(Response.Status.NOT_ACCEPTABLE).entity(new CommonError(message: 'Login unsuccessful. Invalid login name or password.')).build())
            case AuthenticationStatus.MORE_THAN_ONE_USER:
                throw new ClientErrorException(Response.status(Response.Status.CONFLICT).entity(new CommonError(message: 'Login unsuccessful. There are two users with the same login details. Please contact the college for help.')).build())
            default:
                String message = "Unknown authentication status: ${result.status}, login request: $loginRequest"
                logger.error(message)
                throw new IllegalArgumentException(message)
        }
    }

    void userLogoutPost() {
        authenticationService.logout()
    }
    
}

