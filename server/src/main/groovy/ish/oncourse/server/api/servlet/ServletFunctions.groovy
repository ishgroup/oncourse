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

import static ish.oncourse.server.api.servlet.AngelSessionDataStore.USER_ATTRIBUTE
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

class ServletFunctions {

    private static final Logger logger = LogManager.getLogger(ServletFunctions)

    // as compared to heartbeat

    static boolean hasErrors(HttpServletRequest request, HttpServletResponse response) {
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

        false
    }
}
