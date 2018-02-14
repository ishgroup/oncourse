package ish.oncourse.willow.editor.service.impl

import com.google.inject.Inject
import ish.oncourse.model.SystemUser
import ish.oncourse.model.WillowUser
import ish.oncourse.services.authentication.AuthenticationResult
import ish.oncourse.services.authentication.AuthenticationStatus
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.willow.editor.service.*;
import ish.oncourse.willow.editor.model.User;
import ish.oncourse.willow.editor.model.api.LoginRequest;
import ish.oncourse.willow.editor.model.common.CommonError;

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.access.AuthenticationService
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
    

    @Override
    User getUser() {
        WillowUser willowUser = authenticationService.getWillowUser()
        SystemUser systemUser = authenticationService.getSystemUser()
        if (willowUser) {
            return new User().firstName(willowUser.firstName).lastName(willowUser.lastName)
        } else if (systemUser) {
            return new User().firstName(systemUser.firstName).lastName(systemUser.surname)
        } else {
            logger.error('Unexpected error. There is no logged user.')
            throw new InternalServerErrorException('Unexpected error')
        }
    }

    @Override
    void login(LoginRequest loginRequest) {
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

    void logout() {
        authenticationService.logout()
    }
    
}

