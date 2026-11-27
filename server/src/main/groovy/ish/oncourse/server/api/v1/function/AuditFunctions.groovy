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
import ish.oncourse.server.api.v1.model.AuditFieldChangeDTO
import ish.oncourse.server.cayenne.Audit
import ish.util.LocalDateUtils

import java.time.ZoneOffset

class AuditFunctions {

    /**
     * @return the entry without its field changes, see {@link #toRestAuditWithFieldChanges}
     */
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
        restAudit.setSource(dbAudit.source?.databaseValue)
        restAudit.setRequestText(dbAudit.requestText)
        restAudit.setChangeSetRef(dbAudit.changeSetRef)
        // the list view does not read the before/after values
        restAudit.setFieldChanges([])
        return restAudit
    }

    /**
     * @return the entry with its field changes read from the before/after values; only the single entry view shows them
     */
    static AuditDTO toRestAuditWithFieldChanges(Audit dbAudit) {
        AuditDTO restAudit = toRestAudit(dbAudit)
        restAudit.setFieldChanges(toRestFieldChanges(dbAudit.before, dbAudit.after))
        return restAudit
    }

    private static List<AuditFieldChangeDTO> toRestFieldChanges(Map<String, Object> before, Map<String, Object> after) {
        (before.keySet() + after.keySet()).sort().collect { name ->
            new AuditFieldChangeDTO(name, toRestValue(before[name]), toRestValue(after[name]))
        }
    }

    private static String toRestValue(Object value) {
        value instanceof BigDecimal ? (value as BigDecimal).toPlainString() : value?.toString()
    }
}
