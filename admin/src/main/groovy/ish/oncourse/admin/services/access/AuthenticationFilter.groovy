package ish.oncourse.admin.services.access

import com.google.inject.Inject
import ish.oncourse.services.authentication.AuthenticationResult
import ish.oncourse.services.authentication.AuthenticationStatus
import ish.oncourse.services.authentication.CheckBasicAuth
import org.apache.http.HttpStatus
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticationFilter implements Filter {
    private static final Logger logger = LogManager.logger
    private AuthenticationService authService

    static final String CONTEXT_PATH = "contextPath"

    @Inject
    AuthenticationFilter(AuthenticationService authenticationService) {
        this.authService = authenticationService
    }

    @Override
    void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String context = (request as HttpServletRequest).getContextPath()
        String uri = (request as HttpServletRequest).getRequestURI()

        if (!(uri in ["${context}/ISHHealthCheck".toString(), "${context}/ntis/cron".toString()])) {
            AuthenticationResult result = CheckBasicAuth.valueOf(authService, (HttpServletRequest) request).check()
            switch (result.status) {
                case AuthenticationStatus.SUCCESS:
                    if (authService.authorise(result.firstName, context, uri)) {
                        chain.doFilter(request, response)
                    } else {
                        ((HttpServletResponse) response).sendError(HttpStatus.SC_METHOD_NOT_ALLOWED, "Not allowed")
                    }
                    break
                case AuthenticationStatus.INVALID_CREDENTIALS:
                case AuthenticationStatus.MORE_THAN_ONE_USER:
                case AuthenticationStatus.NO_MATCHING_USER:
                    ((HttpServletResponse) response).setStatus(HttpStatus.SC_UNAUTHORIZED)
                    break
                case AuthenticationStatus.UNAUTHORIZED:
                    ((HttpServletResponse) response).setHeader("WWW-Authenticate", "Basic realm=ISH")
                    ((HttpServletResponse) response).sendError(HttpStatus.SC_UNAUTHORIZED, "Unauthorized")
                    break
                default :
                    ((HttpServletResponse) response).sendError(HttpStatus.SC_UNAUTHORIZED, "Unauthorized")
            }
        } else {
            chain.doFilter(request, response)
        }
    }

    @Override
    void destroy() {

    }
}