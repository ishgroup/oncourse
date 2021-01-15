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
import static ish.oncourse.server.api.servlet.ServletFunctions.hasErrors
import ish.oncourse.server.security.api.IPermissionService
import ish.oncourse.server.users.SystemUserService
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Response

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

class ApiFilter implements Filter {

    private static final String LOGIN_PATH_INFO = 'login'
    private static final String CHECK_PASSWORD_INFO = 'user/checkPassword/'
    private static final String INVITATION = 'invite/'
    private static final String X_VALIDATE_ONLY = 'x-validate-only'
    private static final String XVALIDATEONLY = 'XValidateOnly'

    private FilterConfig filterConfig

    private final SystemUserService systemUserService
    private final IPermissionService permissionService
    public static final ThreadLocal<Boolean> validateOnly = new ThreadLocal<>()

    @Inject
    ApiFilter( SystemUserService systemUserService, IPermissionService permissionService) {
        this.systemUserService = systemUserService
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
        } else if (hasErrors(request, response) || !permissionService.authorize(request, response)) {
            return
        }

        chain.doFilter(request, response)
    }

    @Override
    void destroy() {
        //doNothing
    }

    private static allowCrossOriginRequest(Response response) {
        response.addHeader('Access-Control-Allow-Origin', '*')
        response.addHeader('Access-Control-Allow-Methods', 'PUT, GET, OPTIONS, PATCH')
        response.addHeader('Access-Control-Allow-Headers', 'Content-Type, Ish-JXBrowser-Header')
    }
}

