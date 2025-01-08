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

package ish.oncourse.server

import javax.inject.Inject
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.server.services.AuditService
import ish.oncourse.types.AuditAction
import org.apache.cayenne.annotation.PostPersist
import org.apache.cayenne.annotation.PostRemove

class AuditListener {

    private AuditService auditService

    @Inject
    AuditListener (AuditService auditService) {
        this.auditService = auditService
    }

    @PostPersist(value = CayenneDataObject)
    void postPersist(CayenneDataObject object) {
        auditService.submit(object, AuditAction.CREATE)
    }

    @PostRemove(value = CayenneDataObject)
    void postRemove(CayenneDataObject object) {
        auditService.submit(object, AuditAction.DELETE)
    }
}
