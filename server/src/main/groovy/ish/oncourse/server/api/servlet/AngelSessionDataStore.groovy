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


import groovy.transform.CompileStatic
import ish.common.types.SystemEventType
import ish.oncourse.common.SystemEvent
import ish.oncourse.server.CayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.ACLRole
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.integration.EventService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.jetty.server.session.AbstractSessionDataStore
import org.eclipse.jetty.server.session.SessionData

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Handles the movement of the user and session data from the database to memory and back again.
 */
@CompileStatic
class AngelSessionDataStore extends AbstractSessionDataStore {

    public static final String USER_ATTRIBUTE = "USER_ATTRIBUTE"
    public static final String IS_LOGIN = "login"

    private static final ExecutorService executor = Executors.newSingleThreadExecutor()
    private static final Logger logger = LogManager.getLogger()

    private CayenneService cayenneService
    private PreferenceController preferenceController
    private ObjectContext context
    private EventService eventService

    AngelSessionDataStore(CayenneService cayenneService, PreferenceController preferenceController, EventService eventService) {
        this.cayenneService = cayenneService
        this.context = cayenneService.getNewNonReplicatingContext()
        this.preferenceController = preferenceController
        this.eventService = eventService
        this._savePeriodSec = 60
    }

    /**
     * Check if a session for the given id exists.
     *
     * @param id the session id to check
     * @return true if the session exists in the persistent store, false otherwise
     */
//    @Override
    boolean doExists(String id) throws Exception {
        return ObjectSelect.query(SystemUser)
                .where(SystemUser.SESSION_ID.eq(id))
                .and(SystemUser.LAST_ACCESS.gt(preferenceController.timeoutThreshold))
                .selectCount(context) == 1
    }

    /**
     * Update the user in the database. This is done periodically to update the last access time
     * so we can track inactivity logout.
     * @param id session id
     * @param data session data to store
     * @param lastSaveTime
     * @throws Exception
     */
    @Override
    void doStore(String id, SessionData data, long lastSaveTime) throws Exception {
        def user = data.getAttribute(USER_ATTRIBUTE) as SystemUser
        def isLogin = data.getAttribute(IS_LOGIN) as Boolean

        if (!user) {
            return  // user already logged out
        }
        user = context.localObject(user)

        Runnable runnable = { ->
            try {
                // this is the first time the user session is being saved to the DB
                if (isLogin || lastSaveTime == 0L) {
                    data.setAttribute(IS_LOGIN, false)
                    user.setSessionId(id)
                } else {
                    //check if user was logged out from some other application instance
                    if (!exists(id)) {
                        return
                    }
                }
                user.setLastAccess(new Date())
                context.commitChanges()
                if (isLogin) {
                    eventService.postEvent(SystemEvent.valueOf(SystemEventType.USER_LOGGED_IN, user))
                }
            } catch (Exception e) {
                logger.catching(e)
            }
        }
        executor.submit(runnable)

    }

    /**
     * Load session data such as preferences and access control into memory
     *
     * @param id session id
     * @return session data if session found, otherwise null
     * @throws Exception
     */
    @Override
    SessionData doLoad(String id) throws Exception {
        SystemUser user = ObjectSelect.query(SystemUser)
                .where(SystemUser.SESSION_ID.eq(id))
                .and(SystemUser.LAST_ACCESS.gt(preferenceController.timeoutThreshold))
                .prefetch(SystemUser.PREFERENCES.disjointById())
                .prefetch(SystemUser.ACL_ROLES.disjointById())
                .prefetch(SystemUser.ACL_ROLES.dot(ACLRole.ACCESS_KEYS).disjointById())
                .selectFirst(context)
        if (user) {
            Map<String, Object> attributes = new HashMap<>()
            attributes.put(USER_ATTRIBUTE, user)
            SessionData data = new SessionData(id, '', '',  user.lastLoginOn.time, new Date().time, user.lastAccess.time, preferenceController.timeoutMs, attributes)
            return data
        } else {
            logger.debug("Tried to load user from database, but already logged out or session expired.")
            return null
        }

    }

    /**
     * Get all expired sessions from the session ids provided
     *
     * @param candidates collection of session ids to check
     * @return
     */
    @Override
    Set<String> doGetExpired(Set<String> candidates) {
        return ObjectSelect
                .columnQuery(SystemUser, SystemUser.SESSION_ID)
                .where(SystemUser.SESSION_ID.in(candidates))
                .and(SystemUser.LAST_ACCESS.lt(preferenceController.timeoutThreshold))
                .select(context).toSet()
    }

    /**
     * Implemented by subclasses to find sessions for this context in the store
     * that expired at or before the time limit and thus not being actively
     * managed by any node. This method is only called periodically (the period
     * is configurable) to avoid putting too much load on the store.
     *
     * @param before the upper limit of expiry times to check. Sessions expired
     *            at or before this timestamp will match.
     *
     * @return the empty set if there are no sessions expired as at the time, or
     *         otherwise a set of session ids.
     */
//    @Override
    Set<String> doGetExpired(long before) {
        return ObjectSelect
                .columnQuery(SystemUser, SystemUser.SESSION_ID)
                .where(SystemUser.LAST_ACCESS.lt(preferenceController.getTimeoutThreshold(before)))
                .select(context).toSet()
    }

    /**
     * Implemented by subclasses to delete sessions for other contexts that
     * expired at or before the timeLimit. These are 'orphaned' sessions that
     * are no longer being actively managed by any node. These are explicitly
     * sessions that do NOT belong to this context (other mechanisms such as
     * doGetExpired take care of those). As they don't belong to this context,
     * they cannot be loaded by us.
     *
     * This is called only periodically to avoid placing excessive load on the
     * store.
     *
     * @param time the upper limit of the expiry time to check in msec
     */
//    @Override
    void doCleanOrphans(long time) {
        // nothing to do here
    }

    @Override
    boolean isPassivating() {
        return true
    }

    /**
     * Check whether the session exists and has not timed out.
     *
     * @param id session id
     * @return
     * @throws Exception
     */
    @Override
    boolean exists(String id) throws Exception {
        return ObjectSelect.query(SystemUser)
                .where(SystemUser.SESSION_ID.eq(id))
                .and(SystemUser.LAST_ACCESS.gt(preferenceController.timeoutThreshold))
                .selectFirst(context) != null
    }

    /**
     * Log out the user.
     *
     * @param id session id
     * @return true if the user was logged out. False if the session did not exist.
     * @throws Exception
     */
    @Override
    boolean delete(String id) throws Exception {
        List<SystemUser> users = ObjectSelect.query(SystemUser)
                .where(SystemUser.SESSION_ID.eq(id))
                .select(context)

        if (!users.empty) {
            users.each { user ->
                user.sessionId = null
                user.lastAccess = null
                context.commitChanges()
                eventService.postEvent(SystemEvent.valueOf(SystemEventType.USER_LOGGED_OUT, user))
            }
            return true
        }
        logger.info("Tried to log out session which was already gone.")
        return false
    }
}
