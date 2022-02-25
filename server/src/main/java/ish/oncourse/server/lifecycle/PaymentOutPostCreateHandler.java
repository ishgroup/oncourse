/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
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
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.graph.GraphChangeHandler;
import org.apache.cayenne.graph.GraphDiff;
import org.apache.cayenne.validation.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class PaymentOutPostCreateHandler implements GraphChangeHandler {
    private static final Logger logger = LogManager.getLogger();

    private ObjectContext currentContext;
    private AccountTransactionService accountTransactionService;
    private TransactionLockedService transactionLockedService;
    private GraphDiff graphDiff;


    public PaymentOutPostCreateHandler(ObjectContext currentContext, AccountTransactionService accountTransactionService,
                                       GraphDiff graphDiff, TransactionLockedService transactionLockedService){
        this.currentContext = currentContext;
        this.accountTransactionService = accountTransactionService;
        this.graphDiff = graphDiff;
        this.transactionLockedService = transactionLockedService;
    }

    @Override
    public void nodeIdChanged(Object nodeId, Object newId) {
        
    }

    @Override
    public void nodeCreated(Object nodeId) {
        if(nodeId instanceof ObjectId){
            Object o = Cayenne.objectForPK(currentContext, (ObjectId) nodeId);
            if(o instanceof PaymentOut){
                PaymentOut payment = (PaymentOut) o;
                if (PaymentStatus.SUCCESS.equals(payment.getStatus())) {
                    payment.getPaymentOutLines().forEach(this::createInitialTransactions);
                    payment.getContext().commitChanges();
                }
            }
        }
    }

    private void createInitialTransactions(PaymentOutLine line) {
        accountTransactionService.createTransactions(PaymentOutTransactionsBuilder.valueOf(line));
    }


    private void createInitialTransactions(PaymentInLine line) {
        var voucherExpense = AccountUtil.getDefaultVoucherExpenseAccount(line.getObjectContext(), Account.class);
        accountTransactionService.createTransactions(PaymentInTransactionsBuilder.valueOf(line, voucherExpense));
    }

    @Override
    public void nodeRemoved(Object nodeId) {

    }

    @Override
    public void nodePropertyChanged(Object nodeId, String property, Object oldValue, Object newValue) {
        if (nodeId instanceof ObjectId) {
            Object o = Cayenne.objectForPK(currentContext, (ObjectId) nodeId);
            if (o instanceof PaymentOut) {
                PaymentOut paymentOut = (PaymentOut) o;
                if (property.equals(PaymentOut.STATUS.getName()) && PaymentStatus.SUCCESS.equals(newValue))
                    paymentOut.getPaymentOutLines().forEach(this::createInitialTransactions);
                else if (property.equals(PaymentIn.BANKING.getName()) && !successStatusPicked((ObjectId) nodeId)) {
                    var changeHalper = new BankingChangeHandler(currentContext);
                    ChangeFilter.preCommitGraphDiff(currentContext).apply(changeHalper);

                    var oldBankingValue = changeHalper.getOldValueFor(paymentOut.getObjectId());
                    var newBankingValue = changeHalper.getNewValueFor(paymentOut.getObjectId());

                    validateBanking(oldBankingValue, paymentOut);
                    validateBanking(newBankingValue, paymentOut);

                    var oldSettlementDate = oldBankingValue == null ? null : oldBankingValue.getSettlementDate();
                    var newSettlementDate = newBankingValue == null ? null : newBankingValue.getSettlementDate();

                    paymentOut.getPaymentOutLines()
                            .forEach(line -> {
                                accountTransactionService.createTransactions(DepositTransactionsBuilder.valueOf(line, oldSettlementDate, newSettlementDate));
                            });
                }
            }
        }
    }

    private boolean successStatusPicked(ObjectId objectId){
        Map<String, PropertyChange> changes = new GraphDiffParser(graphDiff).getChanges(objectId);
        var statusChange = changes.get(PaymentOut.STATUS.getName());
        if(statusChange == null)
            return false;

        return statusChange.getNewValue().equals(PaymentStatus.SUCCESS);
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

    @Override
    public void arcCreated(Object nodeId, Object targetNodeId, Object arcId) {

    }

    @Override
    public void arcDeleted(Object nodeId, Object targetNodeId, Object arcId) {

    }
}
