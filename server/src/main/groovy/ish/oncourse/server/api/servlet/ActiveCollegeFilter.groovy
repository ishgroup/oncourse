/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.servlet

import com.google.inject.Inject
import ish.oncourse.server.PreferenceController

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.Response

class ActiveCollegeFilter implements Filter {
    private static final String FORBIDDEN_PAGE_NAME = "forbidden.html"
    private static final String FORBIDDEN_PAGE_PATH = "/pages/"
    private static final String REST_API_PREFIX = "/a/"

    private final PreferenceController preferenceController

    @Inject
    ActiveCollegeFilter(PreferenceController preferenceController) {
        this.preferenceController = preferenceController
    }

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!preferenceController.ifCollegeActive() && !(request as HttpServletRequest).getPathInfo().contains(FORBIDDEN_PAGE_NAME)) {
            if ((request as HttpServletRequest).getRequestURI().startsWith(REST_API_PREFIX)) {
                (response as HttpServletResponse).sendError(Response.Status.FORBIDDEN.statusCode, "College is inactive. Connect your administrator")
            } else {
                RequestDispatcher dispatcher = request.getRequestDispatcher(FORBIDDEN_PAGE_PATH + FORBIDDEN_PAGE_NAME)
                dispatcher.forward(request, response)
            }
            return
        }

        chain.doFilter(request, response)
    }
}
