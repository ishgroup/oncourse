package ish.oncourse.admin.services.access

import com.google.inject.Inject
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.servlet.*

class AuthenticationFilter implements Filter {

    private static final Logger logger = LogManager.logger
    private AuthenticationService authService

    @Inject
    AuthenticationFilter(AuthenticationService authenticationService) {
        this.authService = authenticationService
    }

    @Override
    void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (authService.getUser()) {
            chain.doFilter(request, response)
        } else {
            logger.error("Login required.")
        }
    }

    @Override
    void destroy() {

    }
}