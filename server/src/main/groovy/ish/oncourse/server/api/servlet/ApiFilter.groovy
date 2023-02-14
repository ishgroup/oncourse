/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.servlet

import com.google.inject.Inject
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.ApiToken
import ish.oncourse.server.security.api.IPermissionService
import ish.oncourse.server.services.AuditService
import ish.oncourse.types.AuditAction
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Response

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

import static ish.oncourse.server.api.servlet.AngelSessionDataStore.USER_ATTRIBUTE

class ApiFilter implements Filter {
    
    private static final Logger logger = LogManager.getLogger()

    private static final String LOGIN_PATH_INFO = 'login'
    private static final String CHECK_PASSWORD_INFO = 'user/checkPassword/'
    private static final String INVITATION = 'invite/'
    private static final String X_VALIDATE_ONLY = 'x-validate-only'
    private static final String XVALIDATEONLY = 'XValidateOnly'

    public static final String AUTHORIZATION = 'Authorization'
    
    private FilterConfig filterConfig

    private final ICayenneService cayenneService
    private final IPermissionService permissionService
    private final AuditService auditService

    public static final ThreadLocal<Boolean> validateOnly = new ThreadLocal<>()

    @Inject
    ApiFilter(AuditService auditService, ICayenneService cayenneService, IPermissionService permissionService) {
        this.auditService = auditService
        this.cayenneService = cayenneService
        this.permissionService = permissionService
    }

    @Override
    void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig
    }

    @Override
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {
        Request request = (Request) servletRequest
        Response response = (Response) servletResponse

        validateOnly.set(Boolean.valueOf(request.getHeader(X_VALIDATE_ONLY)) || Boolean.valueOf(request.getHeader(XVALIDATEONLY)))

        if (request.pathInfo.contains(LOGIN_PATH_INFO) || request.pathInfo.contains(CHECK_PASSWORD_INFO) || request.pathInfo.contains(INVITATION)) {
            allowCrossOriginRequest(response)
        } else if (unautorized(request, response) || !permissionService.authorize(request, response)) {
            return
        }

        chain.doFilter(request, response)
    }

    @Override
    void destroy() {
        //doNothing
    }

    private static void allowCrossOriginRequest(Response response) {
        response.addHeader('Access-Control-Allow-Origin', '*')
        response.addHeader('Access-Control-Allow-Methods', 'PUT, GET, OPTIONS, PATCH')
        response.addHeader('Access-Control-Allow-Headers', 'Content-Type, Ish-JXBrowser-Header')
    }

    private boolean unautorized(HttpServletRequest request, HttpServletResponse response) {

        String authHeader = request.getHeader(AUTHORIZATION)
        if (authHeader) {
            ApiToken token = ObjectSelect.query(ApiToken).where(ApiToken.SECRET.eq(authHeader)).selectFirst(cayenneService.newContext)
            
            if (token && token.systemUser.isActive) {               

                token.lastAccess = new Date()
                token.context.commitChanges()
                request.setAttribute(AUTHORIZATION, token)
                auditService.submit(token, AuditAction.API_TOKEN, "Api key: $token.name used for $request.requestURL")

                return false
            }
        }
        
        if (!request.requestedSessionIdValid) {
            // session forged or expired or user kicked out
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            return true
        }

        // there is already an existing valid session
        HttpSession session = request.getSession(false)

        if (!session) {
            logger.error('Session missing.')
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            return true
        }

        // at this stage the session should have the extra properties set,
        // finding a session without one could be an indication of a security breach
        if (!session.getAttribute(USER_ATTRIBUTE)) {
            logger.error('Session invalid (without required custom attributes).')
            session.invalidate()
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            return true
        }

        logger.debug('Serving response, session valid.')

        return false
    }
}

