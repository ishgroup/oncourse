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

package ish.oncourse.server.api.v1.function

import ish.oncourse.server.api.v1.model.AuditDTO
import ish.oncourse.server.cayenne.Audit
import ish.util.LocalDateUtils

import java.time.ZoneOffset

class AuditFunctions {

    static AuditDTO toRestAudit(Audit dbAudit) {
        AuditDTO restAudit = new AuditDTO()
        restAudit.setId(dbAudit.id)
        restAudit.setAction(dbAudit.action.databaseValue)
        restAudit.setMessage(dbAudit.message)
        restAudit.setEntityId(String.valueOf(dbAudit.entityId))
        restAudit.setEntityIdentifier(dbAudit.entityIdentifier)
        restAudit.setCreated(LocalDateUtils.dateToTimeValue(dbAudit.created))
        if (dbAudit.systemUser != null) {
            restAudit.setSystemUser(dbAudit.systemUser.getFirstName() + " " + dbAudit.getSystemUser().getLastName())
        }
        return restAudit
    }
}
