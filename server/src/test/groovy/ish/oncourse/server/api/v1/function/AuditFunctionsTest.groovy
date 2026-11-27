/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import groovy.transform.CompileStatic
import ish.oncourse.server.api.v1.model.AuditDTO
import ish.oncourse.server.api.v1.model.AuditFieldChangeDTO
import ish.oncourse.server.cayenne.Audit
import ish.oncourse.types.AuditAction
import ish.oncourse.types.AuditSource
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

@CompileStatic
class AuditFunctionsTest {

    @Test
    void testToRestAuditLeavesFieldChangesEmpty() {
        AuditDTO dto = AuditFunctions.toRestAudit(audit('{"description":"Deposits"}', '{"description":"Renamed"}'))

        assertEquals('ASSISTANT', dto.source)
        assertEquals('change-set-1', dto.changeSetRef)
        assertTrue(dto.fieldChanges.isEmpty())
    }

    @Test
    void testToRestAuditWithFieldChanges() {
        AuditDTO dto = AuditFunctions.toRestAuditWithFieldChanges(
                audit('{"amount":10.50,"description":"Deposits"}', '{"amount":12.00,"room":12}'))

        assertEquals('ASSISTANT', dto.source)
        assertEquals(['amount', 'description', 'room'], dto.fieldChanges*.name)
        AuditFieldChangeDTO amount = dto.fieldChanges[0]
        assertEquals('10.50', amount.before)
        assertEquals('12.00', amount.after)
        assertEquals(['Deposits', null], [dto.fieldChanges[1].before, dto.fieldChanges[1].after])
        assertEquals([null, '12'], [dto.fieldChanges[2].before, dto.fieldChanges[2].after])
    }

    @Test
    void testFieldChangesEmptyWithoutValuesOrWithBrokenJson() {
        assertTrue(AuditFunctions.toRestAuditWithFieldChanges(audit(null, null)).fieldChanges.isEmpty())
        assertTrue(AuditFunctions.toRestAuditWithFieldChanges(audit('{"description":', 'not json')).fieldChanges.isEmpty())
    }

    private static Audit audit(String beforeValues, String afterValues) {
        Audit audit = new Audit()
        audit.action = AuditAction.UPDATE
        audit.message = 'Updated fields: description'
        audit.entityId = 1L
        audit.entityIdentifier = 'Account'
        audit.created = new Date()
        audit.source = AuditSource.ASSISTANT
        audit.requestText = 'Rename the deposit account'
        audit.changeSetRef = 'change-set-1'
        audit.beforeValues = beforeValues
        audit.afterValues = afterValues
        return audit
    }
}
