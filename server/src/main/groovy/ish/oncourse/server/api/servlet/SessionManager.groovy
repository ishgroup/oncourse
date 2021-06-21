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
import com.google.inject.Provider
import groovy.transform.CompileStatic
import io.bootique.jetty.servlet.DefaultServletEnvironment
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.ApiToken

import static ish.oncourse.cayenne.SystemUserInterface.DEFAULT_ISH_USER

import static ish.oncourse.server.api.servlet.AngelSessionDataStore.USER_ATTRIBUTE
import static ish.oncourse.server.api.servlet.AngelSessionDataStore.IS_LOGIN
import ish.oncourse.server.cayenne.SystemUser
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.SessionIdManager
import org.eclipse.jetty.server.session.Session

import javax.servlet.http.HttpServletRequest

@CompileStatic
class SessionManager implements ISessionManager {

    @Inject
    private Provider<Server> serverProvider

    @Inject
    private ICayenneService cayenneService

    @Inject
    DefaultServletEnvironment defaultServletEnvironment

    private static final Logger logger = LogManager.logger

    private static final Map<Long, Map<String, Object>> SESSION_ATTRIBUTES = [:]

    SystemUser getCurrentUser() {
        Request request = defaultServletEnvironment.request().orElse(null) as Request
        Object token = request?.getAttribute(ApiFilter.AUTHORIZATION)
        if (token) {
            return (token as ApiToken).systemUser
        }

        Session session = request?.session as Session
        if (session) {
            return (SystemUser) session.getAttribute(USER_ATTRIBUTE)
        } else {
            return null
        }
    }

    boolean checkConcurrentUsersLimit(SystemUser user, Integer count, Date timeoutThreshold) {
        if (count != null && DEFAULT_ISH_USER != user.login) {
            ObjectSelect.query(SystemUser)
                    .where(SystemUser.LOGIN.ne(DEFAULT_ISH_USER))
                    .and(SystemUser.SESSION_ID.isNotNull())
                    .and(SystemUser.LAST_ACCESS.gt(timeoutThreshold))
                    .count()
                    .selectFirst(cayenneService.newContext)
                    .intValue() >= count
        }
        false
    }

    Integer getLoggedInUsersCount(Date timeoutThreshold) {
       return ObjectSelect.query(SystemUser)
                .where(SystemUser.SESSION_ID.isNotNull())
                .and(SystemUser.LAST_ACCESS.gt(timeoutThreshold))
                .count()
                .selectFirst(cayenneService.newContext)
                .intValue()
    }

    void createUserSession(SystemUser user, Integer timeoutSec, HttpServletRequest request) {

        Session session = request.session as Session
        session.setAttribute(USER_ATTRIBUTE, user)
        session.setAttribute(IS_LOGIN, true)
        session.setMaxInactiveInterval(timeoutSec)

        logger.warn("User '${user.email?:user.login}' logged in from '$request.remoteAddr' via browser.")
    }

    void logout(HttpServletRequest request) {
        remove(request.session as Session)
    }

    void doKickOut(SystemUser user) {
        remove(user.sessionId)
    }

    private void remove(String sessionId) {
        if (sessionId) {
            SessionIdManager manager = serverProvider.get().sessionIdManager
            manager.sessionHandlers.each { handler ->
                Session session = handler.getSession(sessionId) as Session
                remove(session)
            }
        }
    }

    private void remove(Session session) {
        if (!session) {
            return
        }
        SessionIdManager manager = serverProvider.get().sessionIdManager
        SystemUser user = session.getAttribute(USER_ATTRIBUTE) as SystemUser
        if (user) {
            SESSION_ATTRIBUTES.remove(user.id)
            logger.warn("User {} logged out.", user?.email ?: user?.login)

            session.invalidate()
            manager.invalidateAll(session.id)
        }
    }

}
