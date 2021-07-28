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
import org.apache.cayenne.annotation.PostPersist;
import org.apache.cayenne.annotation.PrePersist;

public class InvoiceLineLifecycleListener {

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
}
