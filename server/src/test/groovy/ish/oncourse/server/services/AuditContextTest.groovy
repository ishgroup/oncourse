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

package ish.oncourse.server.services

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.common.types.AccountType
import ish.common.types.PaymentType
import ish.oncourse.aql.CompilationResult
import ish.oncourse.aql.impl.AntlrAqlService
import ish.oncourse.server.AuditListener
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.Audit
import ish.oncourse.server.cayenne.PaymentMethod
import ish.oncourse.server.license.LicenseService
import ish.oncourse.types.AuditAction
import ish.oncourse.types.AuditSource
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Test

import java.util.concurrent.TimeUnit

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@CompileStatic
class AuditContextTest extends TestWithDatabase {

    private static final String REQUEST_TEXT = 'Rename the deposit account'
    private static final String CHANGE_SET = 'change-set-1'

    private int accountSequence = 0

    @Test
    void testUpdateInAssistantContextHasEveryNewField() {
        Account account = createAccount('Deposits')
        Account oldUndeposited = createAccount('Undeposited funds')
        Account otherAccount = createAccount('Other deposits')
        // own record: payment methods left by other test classes may have no undeposited funds account
        PaymentMethod method = createPaymentMethod(oldUndeposited)

        AuditContext.open(AuditContext.assistant(REQUEST_TEXT, CHANGE_SET)).withCloseable {
            account.description = 'Renamed by assistant'
            method.undepositedFundsAccount = otherAccount
            cayenneContext.commitChanges()
        }

        Audit audit = lastAudit(account, AuditAction.UPDATE)
        assertEquals(AuditSource.ASSISTANT, audit.source)
        assertEquals(REQUEST_TEXT, audit.requestText)
        assertEquals(CHANGE_SET, audit.changeSetRef)
        assertEquals([description: 'Deposits'] as Map<String, Object>, audit.before)
        assertEquals([description: 'Renamed by assistant'] as Map<String, Object>, audit.after)
        assertEquals('{"description":"Deposits"}', audit.beforeValues)

        // to-one relationship changes are recorded as ids of the related records
        Audit methodAudit = lastAudit(method, AuditAction.UPDATE)
        assertEquals(CHANGE_SET, methodAudit.changeSetRef)
        assertEquals([undepositedFundsAccount: oldUndeposited.id] as Map<String, Object>, methodAudit.before)
        assertEquals([undepositedFundsAccount: otherAccount.id] as Map<String, Object>, methodAudit.after)
    }

    @Test
    void testUpdateWithoutContextHasNewFieldsEmpty() {
        Account account = createAccount('Fees')

        account.description = 'Renamed by user'
        cayenneContext.commitChanges()

        Audit audit = lastAudit(account, AuditAction.UPDATE)
        assertTrue(audit.message.startsWith('Updated fields: '))
        assertTrue(audit.message.endsWith('description'))
        assertNull(audit.source)
        assertNull(audit.requestText)
        assertNull(audit.changeSetRef)
        assertNull(audit.beforeValues)
        assertNull(audit.afterValues)
    }

    @Test
    void testSourceOnlyContextDoesNotRecordValues() {
        Account account = createAccount('Token account')

        AuditContext.open(AuditContext.of(AuditSource.API_TOKEN)).withCloseable {
            account.description = 'Renamed through API'
            cayenneContext.commitChanges()
        }

        Audit audit = lastAudit(account, AuditAction.UPDATE)
        assertEquals(AuditSource.API_TOKEN, audit.source)
        assertNull(audit.requestText)
        assertNull(audit.changeSetRef)
        assertNull(audit.beforeValues)
        assertNull(audit.afterValues)

        // the audit list filter by source
        CompilationResult filter = new AntlrAqlService().compile('source == API_TOKEN', Audit, cayenneContext)
        assertTrue(filter.cayenneExpression.present)
        List<Audit> filtered = ObjectSelect.query(Audit).where(filter.cayenneExpression.get()).select(cayenneService.newContext)
        assertTrue(filtered*.id.contains(audit.id))
        assertTrue(filtered.every { Audit it -> it.source == AuditSource.API_TOKEN })
    }

    @Test
    void testCreateAndDeleteInAssistantContextRecordValues() {
        AuditService auditService = injector.getInstance(AuditService)
        AuditListener listener = new AuditListener(auditService)
        Account account = createAccount('Created by assistant')
        Long id = account.id

        AuditContext.open(AuditContext.assistant(REQUEST_TEXT, CHANGE_SET)).withCloseable {
            listener.postPersist(account)
            cayenneContext.deleteObject(account)
            cayenneContext.commitChanges()
            listener.postRemove(account)
        }

        Audit created = lastAudit('Account', id, AuditAction.CREATE)
        assertEquals(AuditSource.ASSISTANT, created.source)
        assertNull(created.beforeValues)
        assertEquals('Created by assistant', created.after['description'])
        assertEquals(AccountType.ASSET.name(), created.after['type'])
        assertNotNull(created.after['createdOn'])
        assertTrue(!created.after.containsKey('id') && !created.after.containsKey('modifiedOn'))

        Audit deleted = lastAudit('Account', id, AuditAction.DELETE)
        assertEquals(CHANGE_SET, deleted.changeSetRef)
        assertNull(deleted.afterValues)
        assertEquals(created.after, deleted.before)
    }

    @Test
    void testCreateWithoutContextHasNewFieldsEmpty() {
        Account account = createAccount('Created by user')

        new AuditListener(injector.getInstance(AuditService)).postPersist(account)

        Audit audit = lastAudit(account, AuditAction.CREATE)
        assertNull(audit.source)
        assertNull(audit.requestText)
        assertNull(audit.changeSetRef)
        assertNull(audit.beforeValues)
        assertNull(audit.afterValues)
    }

    @Test
    void testContextsNest() {
        AuditContext user = AuditContext.of(AuditSource.USER)
        AuditContext assistant = AuditContext.assistant(REQUEST_TEXT, CHANGE_SET)

        AuditContext.open(user).withCloseable {
            AuditContext.open(assistant).withCloseable {
                assertEquals(assistant, AuditContext.current())
            }
            assertEquals(user, AuditContext.current())
        }
        assertNull(AuditContext.current())
    }

    @Test
    void testPurgeDeletesOldEntriesWithNewFields() {
        Account account = createAccount('Purge')
        AuditContext.open(AuditContext.assistant(REQUEST_TEXT, CHANGE_SET)).withCloseable {
            account.description = 'Old change'
            cayenneContext.commitChanges()
            account.description = 'Recent change'
            cayenneContext.commitChanges()
        }
        flushAudits()

        List<Audit> audits = ObjectSelect.query(Audit)
                .where(Audit.ENTITY_IDENTIFIER.eq('Account'))
                .and(Audit.ENTITY_ID.eq(account.id))
                .orderBy(Audit.ID.asc())
                .select(cayenneService.newContext)
        assertEquals(2, audits.size())
        Audit old = audits[0]
        old.created = DateUtils.addDays(new Date(), -40)
        old.context.commitChanges()

        LicenseService licenseService = mock(LicenseService)
        when(licenseService.purge_audit_after_days).thenReturn(30)
        new AuditPurgeJob(cayenneService, licenseService).execute(null)

        List<Audit> remaining = ObjectSelect.query(Audit)
                .where(Audit.ENTITY_IDENTIFIER.eq('Account'))
                .and(Audit.ENTITY_ID.eq(account.id))
                .select(cayenneService.newContext)
        assertEquals([audits[1].id], remaining*.id)
        assertEquals(CHANGE_SET, remaining[0].changeSetRef)
    }

    private Account createAccount(String description) {
        Account account = cayenneContext.newObject(Account)
        account.type = AccountType.ASSET
        account.accountCode = "SST276-${++accountSequence}".toString()
        account.description = description
        account.isEnabled = true
        cayenneContext.commitChanges()
        return account
    }

    private PaymentMethod createPaymentMethod(Account undepositedFundsAccount) {
        PaymentMethod method = cayenneContext.newObject(PaymentMethod)
        method.name = "SST276 method ${++accountSequence}".toString()
        method.type = PaymentType.OTHER
        method.account = undepositedFundsAccount
        method.undepositedFundsAccount = undepositedFundsAccount
        method.active = true
        method.reconcilable = true
        method.bankedAutomatically = false
        cayenneContext.commitChanges()
        return method
    }

    private Audit lastAudit(Account account, AuditAction action) {
        return lastAudit('Account', account.id, action)
    }

    private Audit lastAudit(PaymentMethod method, AuditAction action) {
        return lastAudit('PaymentMethod', method.id, action)
    }

    private Audit lastAudit(String entityName, Long entityId, AuditAction action) {
        flushAudits()
        Audit audit = ObjectSelect.query(Audit)
                .where(Audit.ENTITY_IDENTIFIER.eq(entityName))
                .and(Audit.ENTITY_ID.eq(entityId))
                .and(Audit.ACTION.eq(action))
                .orderBy(Audit.ID.desc())
                .selectFirst(cayenneService.newContext)
        assertNotNull(audit, "No $action audit for $entityName $entityId".toString())
        return audit
    }

    /**
     * Audit entries are inserted by a single thread executor: once a task submitted now has run, all earlier ones have too.
     */
    private void flushAudits() {
        injector.getInstance(AuditService).executorService.submit({} as Runnable).get(10, TimeUnit.SECONDS)
    }
}
