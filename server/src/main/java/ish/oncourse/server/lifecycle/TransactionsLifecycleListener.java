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
			// C-NEW-2: pass entity context so AccountTransaction QueuedRecords share the same
			// QueuedTransaction as PaymentIn/PaymentInLine, preventing FK violations on willow.
			var ctx = payment.getObjectContext();
			payment.getPaymentInLines().forEach(line -> createInitialTransactions(line, ctx));
			ctx.commitChanges();
		}
	}

	@PostPersist(value = PaymentOut.class)
	public void postPersist(PaymentOut payment) {
		if (PaymentStatus.SUCCESS.equals(payment.getStatus())) {
			// C-NEW-3: pass entity context so AccountTransaction QueuedRecords share the same
			// QueuedTransaction as PaymentOut/PaymentOutLine, preventing FK violations on willow.
			var ctx = payment.getObjectContext();
			payment.getPaymentOutLines().forEach(line -> createInitialTransactions(line, ctx));
			ctx.commitChanges();
		}
	}

	@PreUpdate(value = PaymentIn.class)
	public void preUpdate(PaymentIn paymentIn) {
		var objectContext = paymentIn.getObjectContext();

		var statusChange = getAtrAttributeChange(objectContext, paymentIn.getObjectId(),PaymentIn.STATUS.getName());

		if (statusChange != null && PaymentStatus.SUCCESS.equals(statusChange.getNewValue())) {
			// H-1: pass entity context so AccountTransaction QueuedRecords share the same
			// QueuedTransaction as the PaymentIn status UPDATE, preventing semantic desync on willow.
			paymentIn.getPaymentInLines().forEach(line -> createInitialTransactions(line, objectContext));
		} else if (getAtrAttributeChange(objectContext, paymentIn.getObjectId(),PaymentIn.BANKING.getName()) != null) {

			var changeHelper = new BankingChangeHandler(paymentIn.getContext());
			ChangeFilter.preCommitGraphDiff(objectContext).apply(changeHelper);

			var oldValue = changeHelper.getOldValueFor(paymentIn.getObjectId());
			var newValue = changeHelper.getNewValueFor(paymentIn.getObjectId());

			validateBanking(oldValue, paymentIn);
			validateBanking(newValue, paymentIn);

			var oldSettlementDate = oldValue == null ? null : oldValue.getSettlementDate();
			var newSettlementDate = newValue == null ? null : newValue.getSettlementDate();

			// H-1: pass entity context for deposit transactions as well
			paymentIn.getPaymentInLines()
					.forEach(line -> accountTransactionService.createTransactions(DepositTransactionsBuilder.valueOf(line, oldSettlementDate, newSettlementDate), objectContext));
		}
	}

	@PreUpdate(value = PaymentOut.class)
	public void preUpdate(PaymentOut paymentOut) {
		var objectContext = paymentOut.getObjectContext();

		var statusChange = getAtrAttributeChange(objectContext, paymentOut.getObjectId(),PaymentIn.STATUS.getName());

		if (statusChange != null && PaymentStatus.SUCCESS.equals(statusChange.getNewValue())) {
			// H-1: pass entity context so AccountTransaction QueuedRecords share the same
			// QueuedTransaction as the PaymentOut status UPDATE, preventing semantic desync on willow.
			paymentOut.getPaymentOutLines().forEach(line -> createInitialTransactions(line, objectContext));
		} else if (getAtrAttributeChange(objectContext, paymentOut.getObjectId(),PaymentIn.BANKING.getName()) != null) {

			var changeHalper = new BankingChangeHandler(paymentOut.getContext());
			ChangeFilter.preCommitGraphDiff(objectContext).apply(changeHalper);

			var oldValue = changeHalper.getOldValueFor(paymentOut.getObjectId());
			var newValue = changeHalper.getNewValueFor(paymentOut.getObjectId());

			validateBanking(oldValue,paymentOut);
			validateBanking(newValue,paymentOut);

			var oldSettlementDate = oldValue == null ? null : oldValue.getSettlementDate();
			var newSettlementDate = newValue == null ? null : newValue.getSettlementDate();

			// H-1: pass entity context for deposit transactions
			paymentOut.getPaymentOutLines()
					.forEach(line -> accountTransactionService.createTransactions(DepositTransactionsBuilder.valueOf(line, oldSettlementDate, newSettlementDate), objectContext));
		}
	}

	private void createInitialTransactions(PaymentOutLine line, ObjectContext ctx) {
		accountTransactionService.createTransactions(PaymentOutTransactionsBuilder.valueOf(line), ctx);
	}

	private void createInitialTransactions(PaymentInLine line, ObjectContext ctx) {
		var voucherExpense = AccountUtil.getDefaultVoucherExpenseAccount(line.getObjectContext(), Account.class);
		accountTransactionService.createTransactions(PaymentInTransactionsBuilder.valueOf(line, voucherExpense), ctx);
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
