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
import ish.oncourse.server.accounting.builder.DepositTransactionsBuilder;
import ish.oncourse.server.cayenne.Banking;
import ish.oncourse.server.cayenne.PaymentIn;
import ish.oncourse.server.cayenne.PaymentOut;
import org.apache.cayenne.annotation.PreUpdate;

import java.time.LocalDate;
import java.util.Collection;

public class BankingLifecycleListener {

	private AccountTransactionService accountTransactionService;

	public BankingLifecycleListener(AccountTransactionService accountTransactionService) {
		this.accountTransactionService = accountTransactionService;
	}

	@PreUpdate(value = Banking.class)
	public void preUpdate(Banking banking) {
		var change = ChangeFilter.getAtrAttributeChange(banking.getObjectContext(), banking.getObjectId(), Banking.SETTLEMENT_DATE.getName());
		if (change != null) {
			var oldSettlementDate = (LocalDate) change.getOldValue();
			var newSettlementDate = (LocalDate) change.getNewValue();

			createTransactions(banking, oldSettlementDate, newSettlementDate);
		}
	}


	private void createTransactions(Banking banking, LocalDate oldSettlementDate, LocalDate newSettlementDate) {
		banking.getPaymentsIn().stream()
				.map(PaymentIn::getPaymentInLines)
				.flatMap(Collection::stream)
				.forEach(line -> accountTransactionService.createTransactions(DepositTransactionsBuilder.valueOf(line, oldSettlementDate, newSettlementDate)));

		banking.getPaymentsOut().stream()
				.map(PaymentOut::getPaymentOutLines)
				.flatMap(Collection::stream)
				.forEach(line -> accountTransactionService.createTransactions(DepositTransactionsBuilder.valueOf(line, oldSettlementDate, newSettlementDate)));
	}
}
