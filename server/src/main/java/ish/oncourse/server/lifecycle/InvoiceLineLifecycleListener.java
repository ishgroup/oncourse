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
package ish.oncourse.server.lifecycle;

import ish.oncourse.server.accounting.AccountTransactionService;
import ish.oncourse.server.accounting.builder.InvoiceLineTransactionsBuilder;
import ish.oncourse.server.cayenne.InvoiceLine;
import ish.oncourse.server.scripting.GroovyScriptService;
import ish.oncourse.server.scripting.ScriptTriggeringListener;
import org.apache.cayenne.DataChannelSyncFilter;
import org.apache.cayenne.DataChannelSyncFilterChain;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.annotation.PostPersist;
import org.apache.cayenne.annotation.PrePersist;
import org.apache.cayenne.graph.GraphDiff;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class InvoiceLineLifecycleListener implements DataChannelSyncFilter {

	private AccountTransactionService accountTransactionService;

	private InvoiceLineInitHelper invoiceLineInitHelper;

	public InvoiceLineLifecycleListener(
			InvoiceLineInitHelper invoiceLineInitHelper, AccountTransactionService accountTransactionService) {
		super();
		this.invoiceLineInitHelper = invoiceLineInitHelper;
		this.accountTransactionService = accountTransactionService;
	}

	@PrePersist(value = InvoiceLine.class)
	public void prePersist(InvoiceLine invoiceLine) {
		if (invoiceLine.getAccount() == null) {
			invoiceLineInitHelper.processInvoiceLine(invoiceLine);
		}
	}

	@PostPersist(value = InvoiceLine.class)
	public void postPersist(InvoiceLine invoiceLine) {
		accountTransactionService.createTransactions(InvoiceLineTransactionsBuilder.valueOf(invoiceLine));
	}

	@Override
	public GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType, DataChannelSyncFilterChain filterChain) {
		changes.apply(new InvoiceLinePreCreateHandler(originatingContext));
		return filterChain.onSync(originatingContext, changes, syncType);
	}
}
