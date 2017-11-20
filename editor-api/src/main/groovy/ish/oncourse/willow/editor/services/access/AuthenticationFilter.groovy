package ish.oncourse.willow.editor.services.access

import com.google.inject.Inject
import ish.oncourse.willow.editor.model.common.CommonError
import ish.oncourse.willow.editor.service.AuthFilter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.Response


@AuthFilter
class AuthenticationFilter implements ContainerRequestFilter {

    private static final Logger logger = LogManager.logger
    private AuthenticationService service

    @Inject
    AuthenticationFilter(AuthenticationService authenticationService) {
        this.service = authenticationService
    }
    
    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
        if (!service.getSystemUser(true) && !service.getWillowUser(true)) {
            logger.error("Login required, request metadata: $requestContext")
            throw new ClientErrorException(Response.status(Response.Status.UNAUTHORIZED).entity(new CommonError(message: 'Login required')).build())
        }
    }
}