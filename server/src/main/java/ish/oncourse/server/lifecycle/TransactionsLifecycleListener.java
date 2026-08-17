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
import ish.oncourse.cayenne.PaymentInterface;
import ish.oncourse.cayenne.PaymentLineInterface;
import ish.oncourse.server.accounting.AccountTransactionService;
import ish.oncourse.server.accounting.builder.DepositTransactionsBuilder;
import ish.oncourse.server.accounting.builder.PaymentInTransactionsBuilder;
import ish.oncourse.server.accounting.builder.PaymentOutTransactionsBuilder;
import ish.oncourse.server.cayenne.*;
import ish.oncourse.server.services.TransactionLockedService;
import ish.util.AccountUtil;
import ish.validation.ValidationFailure;
import ish.validation.ValidationResult;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.annotation.PostPersist;
import org.apache.cayenne.annotation.PreUpdate;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.validation.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static ish.oncourse.server.lifecycle.ChangeFilter.getAtrAttributeChange;

public class TransactionsLifecycleListener {

    private static final Logger logger = LogManager.getLogger();
    private final TransactionLockedService transactionLockedService;
    private final AccountTransactionService accountTransactionService;


    public TransactionsLifecycleListener(TransactionLockedService transactionLockedService, AccountTransactionService accountTransactionService) {
        this.transactionLockedService = transactionLockedService;
        this.accountTransactionService = accountTransactionService;
    }

    @PostPersist(value = PaymentIn.class)
    public void postPersist(PaymentIn payment) {
        processPaymentPostPersist(payment);
    }

    @PostPersist(value = PaymentOut.class)
    public void postPersist(PaymentOut payment) {
        processPaymentPostPersist(payment);
    }

    private void processPaymentPostPersist(PaymentInterface payment) {
        if (PaymentStatus.SUCCESS.equals(payment.getStatus())) {
            linesOf(payment).forEach(this::createInitialTransactions);
            payment.getContext().commitChanges();
        }
    }


    private void createInitialTransactions(PaymentLineInterface line) {
        if (line instanceof PaymentOutLine)
            accountTransactionService.createTransactions(PaymentOutTransactionsBuilder.valueOf((PaymentOutLine) line));
        else if (line instanceof PaymentInLine) {
            var voucherExpense = AccountUtil.getDefaultVoucherExpenseAccount(line.getObjectContext(), Account.class);
            accountTransactionService.createTransactions(PaymentInTransactionsBuilder.valueOf((PaymentInLine) line, voucherExpense));
        }
    }

    @PreUpdate(value = PaymentIn.class)
    public void preUpdate(PaymentIn paymentIn) {
        processPaymentPreUpdate(paymentIn);
    }

    @PreUpdate(value = PaymentOut.class)
    public void preUpdate(PaymentOut paymentOut) {
        processPaymentPreUpdate(paymentOut);
    }


    private void validateBanking(Banking banking, Persistent o) {
        var lockedTade = transactionLockedService.getTransactionLocked();
        if (banking != null && banking.getSettlementDate().compareTo(lockedTade) < 1) {
            logger.error("Attempt to change banking property for payment: {}. Banking: {}  has settlement date: {} before transaction locked date: {}",
                    o.getObjectId(), banking.getObjectId(), banking.getSettlementDate(), lockedTade);

            var result = new ValidationResult();
            result.addFailure(new ValidationFailure(o, PaymentIn.BANKING.getName(), "You can not modify banking which has settlement date before " + lockedTade.toString()));
            throw new ValidationException(result);
        }
    }


    private void processPaymentPreUpdate(PaymentInterface payment) {
        var statusProperty = paymentStatusPropertyOf(payment);
        var bankingProperty = bankingPropertyOf(payment);
        var statusChange = getAtrAttributeChange(payment.getContext(), payment.getObjectId(), statusProperty.getName());

        if (statusChange != null && PaymentStatus.SUCCESS.equals(statusChange.getNewValue())) {
            linesOf(payment).forEach(this::createInitialTransactions);
        } else if (getAtrAttributeChange(payment.getContext(), payment.getObjectId(), bankingProperty.getName()) != null) {
            processBankingChanges(payment);
        }
    }


    private Property<PaymentStatus> paymentStatusPropertyOf(PaymentInterface paymentInterface) {
        return paymentInterface instanceof PaymentIn ? PaymentIn.STATUS : PaymentOut.STATUS;
    }


    private Property<Banking> bankingPropertyOf(PaymentInterface paymentInterface) {
        return paymentInterface instanceof PaymentIn ? PaymentIn.BANKING : PaymentOut.BANKING;
    }


    private void processBankingChanges(PaymentInterface payment) {
        var changeHelper = new BankingChangeHandler(payment.getContext());
        ChangeFilter.preCommitGraphDiff(payment.getContext()).apply(changeHelper);

        var oldBankingValue = changeHelper.getOldValueFor(payment.getObjectId());
        var newBankingValue = changeHelper.getNewValueFor(payment.getObjectId());

        validateBanking(oldBankingValue, payment);
        validateBanking(newBankingValue, payment);

        var oldSettlementDate = oldBankingValue == null ? null : oldBankingValue.getSettlementDate();
        var newSettlementDate = newBankingValue == null ? null : newBankingValue.getSettlementDate();
        linesOf(payment).forEach(line -> accountTransactionService.createTransactions(DepositTransactionsBuilder.valueOf(line, oldSettlementDate, newSettlementDate)));
    }

    private List<? extends PaymentLineInterface> linesOf(PaymentInterface payment) {
        if (payment instanceof PaymentIn)
            return ((PaymentIn) payment).getPaymentInLines();
        else if (payment instanceof PaymentOut)
            return ((PaymentOut) payment).getPaymentOutLines();
        else
            return new ArrayList<>();
    }
}
