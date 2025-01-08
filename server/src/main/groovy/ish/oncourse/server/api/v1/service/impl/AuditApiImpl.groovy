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
import static ish.oncourse.server.api.function.EntityFunctions.checkForBadRequest
import static ish.oncourse.server.api.function.EntityFunctions.validateIdParam
import static ish.oncourse.server.api.v1.function.AuditFunctions.toRestAudit
import ish.oncourse.server.api.v1.model.AuditDTO
import ish.oncourse.server.api.v1.service.AuditApi
import ish.oncourse.server.cayenne.Audit
import org.apache.cayenne.query.SelectById

class AuditApiImpl implements AuditApi {

    @Inject private ICayenneService cayenneService

    @Override
    AuditDTO get(Long id) {
        checkForBadRequest(validateIdParam(id))

        return toRestAudit(SelectById.query(Audit, id)
                .selectOne(cayenneService.newContext))
    }
}
