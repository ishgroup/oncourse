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
package ish.oncourse.server;

import com.google.inject.Inject;
import ish.oncourse.entity.services.OutcomeService;
import ish.oncourse.server.accounting.AccountTransactionService;
import ish.oncourse.server.integration.EventService;
import ish.oncourse.server.lifecycle.*;
import ish.oncourse.server.scripting.GroovyScriptService;
import ish.oncourse.server.scripting.ScriptTriggeringListener;
import ish.oncourse.server.services.TransactionLockedService;
import org.apache.cayenne.access.ValidationFilter;

/**
 */
public class CayenneListenersService {

    @Inject
    public CayenneListenersService(ICayenneService cayenneService, IPreferenceController pref,
                                   InvoiceLineInitHelper invoiceLineInitHelper,
                                   GroovyScriptService scriptService, OutcomeService outcomeService, EventService eventService,
                                   TransactionLockedService transactionLockedService, AuditListener auditListener, AccountTransactionService accountTransactionService) {
        // add listeners
        cayenneService.addListener(new PaymentInLifecycleListener(pref));
        cayenneService.addListener(new PaymentInLineLifecycleListener());
        cayenneService.addListener(new InvoiceLifecycleListener());
        cayenneService.addListener(new InvoiceLineLifecycleListener(invoiceLineInitHelper, accountTransactionService));
        cayenneService.addListener(new PaymentOutLifecycleListener());
        cayenneService.addListener(new EnrolmentLifecycleListener(cayenneService, outcomeService, eventService));
        cayenneService.addListener(new TransactionsLifecycleListener(transactionLockedService, accountTransactionService));
        cayenneService.addListener(new EffectiveDateLifecycleListener(transactionLockedService));
        cayenneService.addListener(new BankingLifecycleListener(accountTransactionService));
        cayenneService.addListener(auditListener);
        cayenneService.addListener(new MembershipLifecycleListener(cayenneService));
        cayenneService.addListener(new AttendanceLifecycleListener());
        cayenneService.addListener(new TagLifecycleListener());

        // Disable QualityCheckListener until we don't have a working STOMP
//		cayenneService.addListener(new QualityCheckListener(qualityService, cayenneService));

        // add filters
        cayenneService.addSyncFilter(new ChangeFilter());
        cayenneService.addSyncFilter(new ScriptTriggeringListener(scriptService, cayenneService));
        cayenneService.addQueryFilter(new RelationshipQueryInvalidatingFilter());
        cayenneService.addSyncFilter(new ValidationFilter());

    }
}
