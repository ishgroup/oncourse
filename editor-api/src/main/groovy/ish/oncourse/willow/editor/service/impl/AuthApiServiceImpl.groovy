package ish.oncourse.willow.editor.service.impl

import com.google.inject.Inject
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.willow.editor.service.*;
import ish.oncourse.willow.editor.model.User;
import ish.oncourse.willow.editor.model.api.LoginRequest;
import ish.oncourse.willow.editor.model.common.CommonError;

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.access.AuthenticationService
import ish.oncourse.willow.editor.services.access.AuthenticationStatus
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.*
import javax.ws.rs.core.Response

@CompileStatic
class AuthApiServiceImpl implements AuthApi {
    
    private static final Logger logger = LogManager.logger

    private ICayenneService cayenneService
    private AuthenticationService authenticationService

    @Inject
    AuthorizationApiServiceImpl(ICayenneService cayenneService, AuthenticationService authenticationService) {
        this.authenticationService = authenticationService
        this.cayenneService = cayenneService
    }
    
    User getUser(LoginRequest loginRequest) { 
        AuthenticationService.AuthenticationResult result =  authenticationService.authenticate(loginRequest.email, loginRequest.password)
        switch (result.status) {
            case AuthenticationStatus.SUCCESS:
                return new User().firstName(result.firstName).lastName(result.lastName)
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
    
    void logout() {
        authenticationService.logout()
    }
    
}

