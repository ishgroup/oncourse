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

package ish.oncourse.server.users

import javax.inject.Inject
import ish.oncourse.API
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.servlet.ApiFilter
import ish.oncourse.server.api.servlet.ISessionManager
import ish.oncourse.server.cayenne.ACLRole
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.services.ISystemUserService
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@API
class SystemUserService implements ISystemUserService {

    static final Logger logger = LogManager.getLogger()

    private ICayenneService cayenneService

    private ISessionManager sessionManager


    @Inject
    SystemUserService(ICayenneService cayenneService, ISessionManager sessionManager) {
        this.cayenneService = cayenneService
        this.sessionManager = sessionManager
    }

    /**
     *
     * @param name of role
     * @return a list of all system users with certain access role
     */
    @API
    List<SystemUser> getSystemUsersByRole(String name) {
        return ObjectSelect.query(SystemUser).where(SystemUser.ACL_ROLES.dot(ACLRole.NAME).eq(name)).select(cayenneService.newContext)
    }

    SystemUser getCurrentUser() {
        if (ApiFilter.CLIENT_MODE)
            return SelectById.query(SystemUser, 1l).selectOne(cayenneService.newContext)

        return sessionManager.currentUser
    }
}
