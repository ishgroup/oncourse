/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
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
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.graph.GraphChangeHandler;
import org.apache.cayenne.graph.GraphDiff;
import org.apache.cayenne.validation.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaymentOutPostCreateHandler implements GraphChangeHandler {
    private static final Logger logger = LogManager.getLogger();

    private final ObjectContext currentContext;
    private final AccountTransactionService accountTransactionService;
    private final TransactionLockedService transactionLockedService;
    private final GraphDiff graphDiff;


    public PaymentOutPostCreateHandler(ObjectContext currentContext, AccountTransactionService accountTransactionService,
                                       GraphDiff graphDiff, TransactionLockedService transactionLockedService) {
        this.currentContext = currentContext;
        this.accountTransactionService = accountTransactionService;
        this.graphDiff = graphDiff;
        this.transactionLockedService = transactionLockedService;
    }

    @Override
    public void nodeIdChanged(Object nodeId, Object newId) {

    }

    private Property<PaymentStatus> paymentStatusPropertyOf(PaymentInterface paymentInterface) {
        return paymentInterface instanceof PaymentIn ? PaymentIn.STATUS : PaymentOut.STATUS;
    }

    private List<? extends PaymentLineInterface> linesOf(PaymentInterface payment) {
        if (payment instanceof PaymentIn)
            return ((PaymentIn) payment).getPaymentInLines();
        else if (payment instanceof PaymentOut)
            return ((PaymentOut) payment).getPaymentOutLines();
        else
            return new ArrayList<>();
    }

    @Override
    public void nodeCreated(Object nodeId) {
        if (nodeId instanceof ObjectId) {
            Object o = Cayenne.objectForPK(currentContext, (ObjectId) nodeId);
            if (o instanceof PaymentInterface) {
                PaymentInterface payment = (PaymentInterface) o;
                if (PaymentStatus.SUCCESS.equals(payment.getStatus())) {
                    linesOf(payment).forEach(this::createInitialTransactions);
                    currentContext.commitChanges();
                }
            }
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

    @Override
    public void nodeRemoved(Object nodeId) {

    }

    @Override
    public void nodePropertyChanged(Object nodeId, String property, Object oldValue, Object newValue) {
        if (nodeId instanceof ObjectId) {
            Object o = Cayenne.objectForPK(currentContext, (ObjectId) nodeId);
            if (o instanceof PaymentInterface) {
                PaymentInterface payment = (PaymentInterface) o;
                if (property.equals(paymentStatusPropertyOf(payment).getName()) && PaymentStatus.SUCCESS.equals(newValue))
                    linesOf(payment).forEach(this::createInitialTransactions);
                else if (property.equals(PaymentIn.BANKING.getName()) && !successStatusPicked((ObjectId) nodeId, PaymentOut.STATUS)) {
                    processBankingChanges(payment);
                }
            }
        }
    }

    private void processBankingChanges(PaymentInterface payment) {
        var changeHelper = new BankingChangeHandler(currentContext);
        ChangeFilter.preCommitGraphDiff(currentContext).apply(changeHelper);

        var oldBankingValue = changeHelper.getOldValueFor(payment.getObjectId());
        var newBankingValue = changeHelper.getNewValueFor(payment.getObjectId());

        validateBanking(oldBankingValue, payment);
        validateBanking(newBankingValue, payment);

        var oldSettlementDate = oldBankingValue == null ? null : oldBankingValue.getSettlementDate();
        var newSettlementDate = newBankingValue == null ? null : newBankingValue.getSettlementDate();
        linesOf(payment).forEach(line -> accountTransactionService.createTransactions(DepositTransactionsBuilder.valueOf(line, oldSettlementDate, newSettlementDate)));
    }

    private boolean successStatusPicked(ObjectId objectId, Property<PaymentStatus> statusProperty) {
        Map<String, PropertyChange> changes = new GraphDiffParser(graphDiff).getChanges(objectId);
        var statusChange = changes.get(statusProperty.getName());
        if (statusChange == null)
            return false;

        return statusChange.getNewValue().equals(PaymentStatus.SUCCESS);
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

    @Override
    public void arcCreated(Object nodeId, Object targetNodeId, Object arcId) {

    }

    @Override
    public void arcDeleted(Object nodeId, Object targetNodeId, Object arcId) {

    }
}
