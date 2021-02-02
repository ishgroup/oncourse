package ish.oncourse.willow.editor.services.access

import com.google.inject.Inject
import ish.oncourse.api.access.AuthFilter
import ish.oncourse.api.access.SessionCookie
import ish.oncourse.model.College
import ish.oncourse.model.SystemUser
import ish.oncourse.model.WillowUser
import ish.oncourse.services.authentication.AuthenticationResult
import ish.oncourse.services.authentication.AuthenticationStatus
import ish.oncourse.services.authentication.CheckBasicAuth
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.editor.website.WebSiteFunctions
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
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
    private RequestService requestService
    private ZKSessionManager sessionManager
    private ICayenneService cayenneService
    private UserService userService
    
    @Inject
    AuthenticationFilter(AuthenticationService authenticationService, 
                         RequestService requestService,
                         ZKSessionManager sessionManager,
                         ICayenneService cayenneService,
                         UserService userService) {
        this.service = authenticationService
        this.requestService = requestService
        this.sessionManager = sessionManager
        this.cayenneService = cayenneService
        this.userService = userService
    }
    
    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
        
        boolean isAuthorised =  checkSessionCookie()

        if (!isAuthorised) {
            AuthenticationResult result = CheckBasicAuth.valueOf(service, requestService.request).check()

            switch (result.status) {
                case AuthenticationStatus.SUCCESS:
                    isAuthorised = true
                    break
                case AuthenticationStatus.UNAUTHORIZED:
                    isAuthorised = false
                    break
                case AuthenticationStatus.INVALID_CREDENTIALS:
                case AuthenticationStatus.NO_MATCHING_USER:
                    throw new ClientErrorException('Login unsuccessful. Invalid login name or password.', Response.Status.NOT_ACCEPTABLE)
                case AuthenticationStatus.MORE_THAN_ONE_USER:
                    throw new ClientErrorException('Login unsuccessful. There are two users with the same login details. Please contact the college for help.', Response.Status.CONFLICT)
                default:
                    String message = "Unknown authentication status: ${result.status}, login request: ${requestService.request}"
                    logger.error(message)
                    throw new IllegalArgumentException(message)
            }
        }
        
        if (!isAuthorised) {
            logger.error("Login required, request metadata: $requestContext")
            throw new ClientErrorException(Response.Status.UNAUTHORIZED)
        }
    }
    
    private boolean checkSessionCookie() {
        ObjectContext context = cayenneService.newContext()
        College college = WebSiteFunctions.getCurrentCollege(requestService.request, context)
        SessionCookie sessionCookie = SessionCookie.valueOf(requestService.request)

        switch (sessionCookie.userType) {
            case SystemUser.simpleName:
                SystemUser user = (ObjectSelect.query(SystemUser)
                        .where(ExpressionFactory.matchDbExp(SystemUser.ID_PK_COLUMN,sessionCookie.userId))
                        & SystemUser.COLLEGE.eq(college))
                        .selectOne(context)
                if (user) {
                    userService.userFirstName = user.firstName
                    userService.userLastName = user.surname
                    userService.userEmail = user.email
                    userService.systemUser = user
                    return true
                }
                break
            case WillowUser.simpleName:

                WillowUser user = ObjectSelect.query(WillowUser)
                        .where(ExpressionFactory.matchDbExp(SystemUser.ID_PK_COLUMN,sessionCookie.userId))
                        .selectOne(context)
                if (user) {
                    userService.userFirstName = user.firstName
                    userService.userLastName = user.lastName
                    userService.userEmail = user.email
                    return true
                }
                break
            default:
                break
        }
        return false
    }
}