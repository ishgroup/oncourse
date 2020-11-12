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

import ish.common.types.PaymentStatus;
import ish.oncourse.server.accounting.AccountTransactionService;
import ish.oncourse.server.accounting.builder.DepositTransactionsBuilder;
import ish.oncourse.server.accounting.builder.PaymentInTransactionsBuilder;
import ish.oncourse.server.accounting.builder.PaymentOutTransactionsBuilder;
import ish.oncourse.server.cayenne.*;
import ish.oncourse.server.services.TransactionLockedService;
import ish.util.AccountUtil;
import ish.validation.ValidationFailure;
import ish.validation.ValidationResult;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.annotation.PostPersist;
import org.apache.cayenne.annotation.PreUpdate;
import org.apache.cayenne.validation.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;

import static ish.oncourse.server.lifecycle.ChangeFilter.getAtrAttributeChange;

public class TransactionsLifecycleListener {

	private TransactionLockedService transactionLockedService;
	private AccountTransactionService accountTransactionService;
	private static final Logger logger = LogManager.getLogger();


	public TransactionsLifecycleListener (TransactionLockedService transactionLockedService, AccountTransactionService accountTransactionService) {
		this.transactionLockedService = transactionLockedService;
		this.accountTransactionService = accountTransactionService;
	}

	@PostPersist(value = PaymentIn.class)
	public void postPersist(PaymentIn payment) {

		if (PaymentStatus.SUCCESS.equals(payment.getStatus())) {
			payment.getPaymentInLines().forEach(this::createInitialTransactions);
			payment.getContext().commitChanges();
		}

	}

	@PostPersist(value = PaymentOut.class)
	public void postPersist(PaymentOut payment) {

		if (PaymentStatus.SUCCESS.equals(payment.getStatus())) {
			payment.getPaymentOutLines().forEach(this::createInitialTransactions);
			payment.getContext().commitChanges();
		}

	}

	@PreUpdate(value = PaymentIn.class)
	public void preUpdate(PaymentIn paymentIn) {
		var objectContext = paymentIn.getObjectContext();

		var statusChange = getAtrAttributeChange(objectContext, paymentIn.getObjectId(),PaymentIn.STATUS.getName());

		if (statusChange != null && PaymentStatus.SUCCESS.equals(statusChange.getNewValue())) {
			paymentIn.getPaymentInLines().forEach(this::createInitialTransactions);
		} else if (getAtrAttributeChange(objectContext, paymentIn.getObjectId(),PaymentIn.BANKING.getName()) != null) {

			var changeHelper = new BankingChangeHandler(paymentIn.getContext());
			ChangeFilter.preCommitGraphDiff(objectContext).apply(changeHelper);

			var oldValue = changeHelper.getOldValueFor(paymentIn.getObjectId());
			var newValue = changeHelper.getNewValueFor(paymentIn.getObjectId());

			validateBanking(oldValue, paymentIn);
			validateBanking(newValue, paymentIn);

			var oldSettlementDate = oldValue == null ? null : oldValue.getSettlementDate();
			var newSettlementDate = newValue == null ? null : newValue.getSettlementDate();

			paymentIn.getPaymentInLines()
					.forEach(line -> accountTransactionService.createTransactions(DepositTransactionsBuilder.valueOf(line, oldSettlementDate, newSettlementDate)));
		}
	}

	@PreUpdate(value = PaymentOut.class)
	public void preUpdate(PaymentOut paymentOut) {
		var objectContext = paymentOut.getObjectContext();

		var statusChange = getAtrAttributeChange(objectContext, paymentOut.getObjectId(),PaymentIn.STATUS.getName());

		if (statusChange != null && PaymentStatus.SUCCESS.equals(statusChange.getNewValue())) {
			paymentOut.getPaymentOutLines().forEach(this::createInitialTransactions);
		} else if (getAtrAttributeChange(objectContext, paymentOut.getObjectId(),PaymentIn.BANKING.getName()) != null) {

			var changeHalper = new BankingChangeHandler(paymentOut.getContext());
			ChangeFilter.preCommitGraphDiff(objectContext).apply(changeHalper);

			var oldValue = changeHalper.getOldValueFor(paymentOut.getObjectId());
			var newValue = changeHalper.getNewValueFor(paymentOut.getObjectId());

			validateBanking(oldValue,paymentOut);
			validateBanking(newValue,paymentOut);

			var oldSettlementDate = oldValue == null ? null : oldValue.getSettlementDate();
			var newSettlementDate = newValue == null ? null : newValue.getSettlementDate();

			paymentOut.getPaymentOutLines()
					.forEach(line -> {
						accountTransactionService.createTransactions(DepositTransactionsBuilder.valueOf(line, oldSettlementDate, newSettlementDate));
					});
		}
	}

	private void createInitialTransactions(PaymentOutLine line) {
		accountTransactionService.createTransactions(PaymentOutTransactionsBuilder.valueOf(line));
	}

	private void createInitialTransactions(PaymentInLine line) {
		var voucherExpense = AccountUtil.getDefaultVoucherExpenseAccount(line.getObjectContext(), Account.class);
		accountTransactionService.createTransactions(PaymentInTransactionsBuilder.valueOf(line, voucherExpense));
	}

	private void validateBanking(Banking banking, Persistent o) {
		var lockedTade = transactionLockedService.getTransactionLocked();
		if (banking != null && banking.getSettlementDate().compareTo(lockedTade) < 1) {
			logger.error("Attempt to change banking property for payment: {}. Banking: {}  has settlement date: {} before transaction locked date: {}",
					o.getObjectId(), banking.getObjectId(), banking.getSettlementDate(),  lockedTade);

			var result = new ValidationResult();
			result.addFailure(new ValidationFailure(o, PaymentIn.BANKING.getName(), "You can not modify banking which has settlement date before " + lockedTade.toString()));
			throw new ValidationException(result);
		}
	}
}
