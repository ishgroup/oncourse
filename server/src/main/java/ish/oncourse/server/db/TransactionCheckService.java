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

package ish.oncourse.server.db;

import com.google.inject.Inject;
import ish.common.types.AccountTransactionType;
import ish.math.Money;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Account;
import ish.oncourse.server.cayenne.AccountTransaction;
import ish.oncourse.server.cayenne.InvoiceLine;
import ish.oncourse.server.services.TransactionLockedService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SQLSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

import static ish.oncourse.server.lifecycle.EffectiveDateLifecycleListener.SKIP_EFFECTIVE_DATE_CHECK;

public class TransactionCheckService {

    private static final Logger logger = LogManager.getLogger();
    private ObjectContext context;

    private final static String ID ="ID";

    private final static String TAX_TRANSACTIONS_SQL = "select il.id as ID from InvoiceLine il  \n" +
            "join Invoice i on il.invoiceId = i.id\n" +
            "left join Tax t on t.id=il.taxId\n" +
            "left join Account taxAccount on t.receivableFromStudentAccountId = taxAccount.id\n" +
            "left join AccountTransaction tr on tr.tableName='I' and tr.foreignRecordId=il.id and tr.accountId = taxAccount.id\n" +
            "where tr.id is null and il.taxEach != 0 ";

    private final static String PREPAID_FEES_TRANSACTIONS_SQL = "select il.id as ID from InvoiceLine il \n" +
            "join Invoice i on i.id =  il.invoiceId\n" +
            "left join AccountTransaction tr on tr.tableName= 'I' \n" +
            "   and tr.foreignRecordId = il.id \n" +
            "   and tr.accountId = i.debtorsAccountId\n" +
            "   and tr.amount = (il.priceEachexTax - il.discountEachexTax)* il.quantity\n" +
            "where (il.priceEachexTax - il.discountEachexTax)* il.quantity != 0 \n" +
            "   and (il.enrolmentId is not null or il.courseClassId is not  null) \n" +
            "   and tr.id is null";

    private final static String INCOME_TRANSACTIONS_SQL = "select il.id as ID from InvoiceLine il \n" +
            "left join AccountTransaction tr on tr.tableName= 'I' \n" +
            "   and tr.foreignRecordId = il.id \n" +
            "   and tr.accountId = il.accountId\n" +
            "   and tr.amount = (il.priceEachexTax - il.discountEachexTax)* il.quantity\n" +
            "where (il.priceEachexTax - il.discountEachexTax)* il.quantity != 0 \n" +
            " and (il.enrolmentId is null and il.courseClassId is null) \n" +
            " and tr.id is null";

    private final static String DISCOUNT_TRANSACTIONS_SQL = "SELECT il.id as ID FROM InvoiceLine il \n" +
            "left JOIN AccountTransaction tr on tr.tableName='I' and tr.foreignRecordId=il.id and tr.accountId=il.cosAccountId\n" +
            "WHERE il.cosAccountId is not null and il.discountEachexTax != 0\n" +
            "and tr.id is null";

    private final static String PREPAID_FEES_WRONG_SQL = "select il.id as ID, tr.amount from InvoiceLine il\n" +
            "join AccountTransaction tr on tr.tableName='I' and tr.foreignRecordId=il.id and tr.accountId=il.prepaidFeesAccountId\n" +
            "where (il.courseClassId is not null or il.enrolmentId is not null) and il.prepaidFeesRemaining = 0 \n" +
            "group by il.id HAVING sum(tr.amount) !=0";

    private final static String PAYMENT_TRANSACTIONS_SQL = "select pl.id as ID, pl.amount, tr.id from PaymentInLine pl\n" +
            "join PaymentIn p on pl.paymentInId = p.id\n" +
            "join Invoice i on pl.invoiceId = i.id \n" +
            "left join AccountTransaction tr on tr.tableName='P' and tr.foreignRecordId=pl.id  \n" +
            "where pl.amount != 0 and p.status=3\n" +
            "HAVING tr.id is null";

    /**
     * @param cayenneService instance of CayenneService
     */
    @Inject
    public TransactionCheckService(ICayenneService cayenneService) {
        super();
        this.context = cayenneService.getNewNonReplicatingContext();
        context.setUserProperty(SKIP_EFFECTIVE_DATE_CHECK, Boolean.TRUE);
    }

    public final void performCheck() throws Exception {

        if (Boolean.valueOf(System.getProperty("disableTransactionCheck")) == Boolean.TRUE) {
            logger.warn("Transaction check DISABLED");
            return;
        }

        checkTransactions(TAX_TRANSACTIONS_SQL, "tax", il -> il.getTax().getReceivableFromAccount(), il -> il.getInvoice().getDebtorsAccount(), InvoiceLine::getTotalTax);

        checkTransactions(PREPAID_FEES_TRANSACTIONS_SQL, "prepaid fees", InvoiceLine::getPrepaidFeesAccount, il -> il.getInvoice().getDebtorsAccount(), InvoiceLine::getFinalPriceToPayExTax);

        checkTransactions(INCOME_TRANSACTIONS_SQL, "income", InvoiceLine::getAccount, il -> il.getInvoice().getDebtorsAccount(), InvoiceLine::getFinalPriceToPayExTax);

        checkTransactions(PREPAID_FEES_WRONG_SQL, "liability mismatch", InvoiceLine::getAccount, InvoiceLine::getPrepaidFeesAccount, this::getLiabilityMismatchAmount);

        checkTransactions(DISCOUNT_TRANSACTIONS_SQL, "discount", InvoiceLine::getAccount, InvoiceLine::getCosAccount, InvoiceLine::getDiscountTotalExTax);

        checkPaymentTransactions();
    }

    private void checkPaymentTransactions() {
        logger.warn("Looking for missing payment transactions");

        var rows  = SQLSelect.dataRowQuery(PAYMENT_TRANSACTIONS_SQL).select(context);
        if (!rows.isEmpty()) {

            logger.error("Found {} payment in lines with missing transactions", rows.size());
            rows.forEach(row -> {
                logger.error("Payment line Id {}", row.get(ID));

//                try {
//                    var pl = SelectById.query(PaymentInLine.class, row.get(ID)).selectOne(context);
//
//                    if (ObjectSelect.query(AccountTransaction.class)
//                            .where(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.PAYMENT_IN_LINE))
//                            .and(AccountTransaction.FOREIGN_RECORD_ID.eq(pl.getId()))
//                            .select(context).isEmpty()) {
//                        var voucherExpense = AccountUtil.getDefaultVoucherExpenseAccount(context, Account.class);
//
//                        var settings = PaymentInTransactionsBuilder.valueOf(pl, voucherExpense, transactionLockedService.getTransactionDate(pl.getPaymentIn().getPaymentDate())).build();
//                        settings.getDetails().forEach(d -> {
//                            d.setDescription("Created by TransactionCheckService, type: initial payment transactions");
//
//                            logger.error("Create initial transactions, paymentInLineId: {}, amount: {}", pl.getId().toString(), pl.getAmount().toString());
//                            var ids = CreateAccountTransactions.valueOf(context, d).create();
//                            if (ids != null && !ids.isEmpty()) {
//                                logger.error("Initial transactions for paymentInLine({}) created: {},{}", pl.getId().toString(), ids.get(0), ids.get(1));
//                            }
//                        });
//                    } else {
//                        logger.error("Attention! Initial transactions for paymentInLine({}) already exist, SKIP}", pl.getId().toString());
//
//                    }
//                } catch (Exception e) {
//                    context.commitChanges();
//                    logger.error("Fail to create initial transactions, paymentInLine: {}", row.get(ID));
//                    logger.catching(e);
//                }

            });
        }


    }

    private Money getLiabilityMismatchAmount(InvoiceLine il) {

        if (!il.getPrepaidFeesRemaining().equals(Money.ZERO())) {
            return Money.ZERO();
        }
        var liabilityTransactions = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.INVOICE_LINE))
                .and(AccountTransaction.FOREIGN_RECORD_ID.eq(il.getId()))
                .and(AccountTransaction.ACCOUNT.eq(il.getPrepaidFeesAccount())).select(context);


        return liabilityTransactions.stream().map(AccountTransaction::getAmount).reduce(Money.ZERO(), Money::add);
    }

    private void checkTransactions(String sql, String title,
                                    Function<InvoiceLine, Account> primaryAccount,
                                    Function<InvoiceLine, Account> secondaryAccount,
                                    Function<InvoiceLine, Money> transactionAmount) {

        logger.warn("Looking for missing {} transactions", title);


        var rows = SQLSelect.dataRowQuery(sql).select(context);

        if (!rows.isEmpty()) {
            logger.error("Found {} invoice lines with missing {} transactions", rows.size(), title);

            rows.forEach(row -> {
                logger.error("Invoice line Id {}",row.get(ID));
//            try {
//                var il = SelectById.query(InvoiceLine.class, row.get(ID)).selectOne(context);
//                var primary = primaryAccount.apply(il);
//                var secondary = secondaryAccount.apply(il);
//                var amount = transactionAmount.apply(il);
//
//                var detail = AccountTransactionDetail.valueOf(il, amount, primary, secondary, transactionLockedService.getTransactionDate(il.getInvoice().getInvoiceDate()));
//                logger.error("Create {} transactions, invoiceLineId = {}, discount total ex tax= {}", title, il.getId().toString(), amount.toString());
//                detail.setDescription("Created by TransactionCheckService, type: " + title);
//                var ids = CreateAccountTransactions.valueOf(context, detail).create();
//                if (ids != null && !ids.isEmpty()) {
//                    logger.error("{} transactions for invoiceLine({}) created: {},{}", title, il.getId().toString(), ids.get(0), ids.get(1));
//                }
//            } catch (Exception e) {
//                context.rollbackChanges();
//                logger.error("Fail to create {} transactions, invoiceLineId = {}", title, row.get(ID));
//                logger.catching(e);
//            }
            });
        }
    }

}
