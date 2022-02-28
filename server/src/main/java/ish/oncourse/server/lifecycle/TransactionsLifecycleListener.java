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
import ish.oncourse.server.services.TransactionLockedService;
import org.apache.cayenne.DataChannelSyncFilter;
import org.apache.cayenne.DataChannelSyncFilterChain;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.graph.GraphDiff;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransactionsLifecycleListener implements DataChannelSyncFilter {

	private final TransactionLockedService transactionLockedService;
	private final AccountTransactionService accountTransactionService;
	private static final Logger logger = LogManager.getLogger();


	public TransactionsLifecycleListener (TransactionLockedService transactionLockedService, AccountTransactionService accountTransactionService) {
		this.transactionLockedService = transactionLockedService;
		this.accountTransactionService = accountTransactionService;
	}

	@Override
	public GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType, DataChannelSyncFilterChain filterChain) {
		var changesBeforeApply = filterChain.onSync(originatingContext, changes, syncType);
		changes.apply(new PaymentOutPostCreateHandler(originatingContext, accountTransactionService, changes, transactionLockedService));
		return changesBeforeApply;
	}
}
