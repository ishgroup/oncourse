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

package ish.oncourse.server.api.v1.service.impl

import javax.inject.Inject
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.function.CayenneFunctions
import static ish.oncourse.server.api.v1.function.UserRoleFunctions.toDbACLRole
import static ish.oncourse.server.api.v1.function.UserRoleFunctions.toRestUserRole
import static ish.oncourse.server.api.v1.function.UserRoleFunctions.validateForDelete
import static ish.oncourse.server.api.v1.function.UserRoleFunctions.validateForUpdate
import ish.oncourse.server.api.v1.model.UserRoleDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.UserRoleApi
import ish.oncourse.server.cayenne.ACLRole
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

class UserRoleApiImpl implements UserRoleApi {

    @Inject
    private ICayenneService cayenneService

    @Override
    List<UserRoleDTO> get() {
        ObjectSelect.query(ACLRole)
                .prefetch(ACLRole.ACCESS_KEYS.joint())
                .orderBy(ACLRole.NAME.asc())
                .select(cayenneService.newContext)
                .collect { toRestUserRole(it) }
    }

    @Override
    void remove(Long id) {
        ObjectContext context = cayenneService.newContext
        ACLRole aclRole = CayenneFunctions.getRecordById(context, ACLRole, id, ACLRole.SYSTEM_USERS.joint())

        ValidationErrorDTO error = validateForDelete(aclRole, id)
        if (error) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }

        context.deleteObjects(aclRole)
        context.commitChanges()
    }

    @Override
    void update(UserRoleDTO userRole) {
        ObjectContext context = cayenneService.newContext

        ValidationErrorDTO error = validateForUpdate(context, userRole)
        if (error) {
            context.rollbackChanges()
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }
        toDbACLRole(context, userRole)

        context.commitChanges()
    }
}
