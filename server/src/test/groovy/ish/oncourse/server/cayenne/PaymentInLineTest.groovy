/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.server.ICayenneService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
class PaymentInLineTest extends CayenneIshTestCase {

    private ICayenneService cayenneService

    
    @BeforeEach
    void setup() throws Exception {
        wipeTables()

        this.cayenneService = injector.getInstance(ICayenneService.class)

        InputStream st = PaymentInLineTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/cayenne/paymentInLineTest.xml")
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
        builder.setColumnSensing(true)

        FlatXmlDataSet dataSet = builder.build(st)
        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        Date start1 = DateUtils.addDays(new Date(), -4)
        Date start2 = DateUtils.addDays(new Date(), -2)
        Date start3 = DateUtils.addDays(new Date(), 2)
        Date start4 = DateUtils.addDays(new Date(), 4)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[start_date3]", start3)
        rDataSet.addReplacementObject("[start_date4]", start4)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))
        rDataSet.addReplacementObject("[end_date3]", DateUtils.addHours(start3, 2))
        rDataSet.addReplacementObject("[end_date4]", DateUtils.addHours(start4, 2))
        rDataSet.addReplacementObject("[null]", null)

        executeDatabaseOperation(rDataSet)
        super.setup()
    }

    
    @Test
    void testCreateMoneyVoucherTransactions() {
        ObjectContext context = cayenneService.getNewContext()

        Account debtorsAccount = SelectById.query(Account.class, 100).selectOne(context)
        Account voucherLiabilityAccount = SelectById.query(Account.class, 800).selectOne(context)
        Account voucherUnderpaymentAccount = SelectById.query(Account.class, 900).selectOne(context)

        PaymentIn payment1 = SelectById.query(PaymentIn.class, 200).selectOne(context)
        PaymentIn payment2 = SelectById.query(PaymentIn.class, 201).selectOne(context)

        Assertions.assertEquals(PaymentStatus.IN_TRANSACTION, payment1.getStatus())
        Assertions.assertEquals(PaymentStatus.IN_TRANSACTION, payment2.getStatus())

        Assertions.assertTrue(context.select(SelectQuery.query(AccountTransaction.class)).isEmpty())

        payment1.setStatus(PaymentStatus.SUCCESS)
        payment2.setStatus(PaymentStatus.SUCCESS)

        context.commitChanges()


        //VoucherProduct Purchase price: $100
        //Redemption : 4 enrolments (redeemed for 2 enrolments now)
        //Class cost for redemption : $110
        List<AccountTransaction> transactions = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.FOREIGN_RECORD_ID.eq(payment1.getPaymentInLines().get(0).getId()))
                .select(context)
        Assertions.assertEquals(4, transactions.size())

        List<AccountTransaction> assetTransactions = AccountTransaction.ACCOUNT.eq(debtorsAccount).filterObjects(transactions)
        Assertions.assertEquals(1, assetTransactions.size())
        Assertions.assertEquals(new Money("-110.00"), assetTransactions.get(0).getAmount())

        List<AccountTransaction> liabilityTransactions = AccountTransaction.ACCOUNT.eq(voucherLiabilityAccount).filterObjects(transactions)
        Assertions.assertEquals(1, liabilityTransactions.size())
        Assertions.assertEquals(new Money("-100.00"), liabilityTransactions.get(0).getAmount())

        List<AccountTransaction> expenseTransactions = AccountTransaction.ACCOUNT.eq(voucherUnderpaymentAccount).filterObjects(transactions)
        Assertions.assertEquals(2, expenseTransactions.size())

        AccountTransaction asset_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(1) : expenseTransactions.get(0)
        AccountTransaction liability_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(0) : expenseTransactions.get(1)

        Assertions.assertEquals(new Money("110.00"), asset_expense.getAmount())
        Assertions.assertEquals(new Money("-100.00"), liability_expense.getAmount())

        //VoucherProduct Purchase price: $100
        //Redemption : 4 enrolments (redeemed for 2 enrolments now)
        //Class cost for redemption : $110
        List<AccountTransaction> transactions2 = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.FOREIGN_RECORD_ID.eq(payment2.getPaymentInLines().get(0).getId()))
                .select(context)
        Assertions.assertEquals(4, transactions2.size())

        List<AccountTransaction> assetTransactions2 = AccountTransaction.ACCOUNT.eq(debtorsAccount).filterObjects(transactions2)
        Assertions.assertEquals(1, assetTransactions2.size())
        Assertions.assertEquals(new Money("-110.00"), assetTransactions2.get(0).getAmount())

        List<AccountTransaction> liabilityTransactions2 = AccountTransaction.ACCOUNT.eq(voucherLiabilityAccount).filterObjects(transactions2)
        Assertions.assertEquals(1, liabilityTransactions2.size())
        Assertions.assertEquals(new Money("-100.00"), liabilityTransactions2.get(0).getAmount())

        List<AccountTransaction> expenseTransactions2 = AccountTransaction.ACCOUNT.eq(voucherUnderpaymentAccount).filterObjects(transactions2)
        Assertions.assertEquals(2, expenseTransactions2.size())

        AccountTransaction asset_expense2 = expenseTransactions2.get(0).getAmount().isNegative() ? expenseTransactions2.get(1) : expenseTransactions2.get(0)
        AccountTransaction liability_expense2 = expenseTransactions2.get(0).getAmount().isNegative() ? expenseTransactions2.get(0) : expenseTransactions2.get(1)

        Assertions.assertEquals(new Money("110.00"), asset_expense2.getAmount())
        Assertions.assertEquals(new Money("-100.00"), liability_expense2.getAmount())


    }

    
    @Test
    void testCourseVoucherTransactions() {
        ObjectContext context = cayenneService.getNewContext()

        Account debtorsAccount = SelectById.query(Account.class, 100).selectOne(context)
        Account voucherLiabilityAccount = SelectById.query(Account.class, 800).selectOne(context)
        Account voucherUnderpaymentAccount = SelectById.query(Account.class, 900).selectOne(context)

        PaymentIn payment = SelectById.query(PaymentIn.class, 202).selectOne(context)

        Assertions.assertEquals(PaymentStatus.IN_TRANSACTION, payment.getStatus())
        Assertions.assertTrue(context.select(SelectQuery.query(AccountTransaction.class)).isEmpty())

        payment.setStatus(PaymentStatus.SUCCESS)

        context.commitChanges()

        //VoucherProduct Purchase price: $100
        //Redemption : 4 enrolments (redeemed for 2 enrolments now)
        //Class cost for redemption : $220
        List<AccountTransaction> transactions = ObjectSelect.query(AccountTransaction.class).select(context)
        Assertions.assertEquals(4, transactions.size())

        List<AccountTransaction> assetTransactions = AccountTransaction.ACCOUNT.eq(debtorsAccount).filterObjects(transactions)
        Assertions.assertEquals(1, assetTransactions.size())
        Assertions.assertEquals(new Money("-220.00"), assetTransactions.get(0).getAmount())

        List<AccountTransaction> liabilityTransactions = AccountTransaction.ACCOUNT.eq(voucherLiabilityAccount).filterObjects(transactions)
        Assertions.assertEquals(1, liabilityTransactions.size())
        Assertions.assertEquals(new Money("-100.00"), liabilityTransactions.get(0).getAmount())

        List<AccountTransaction> expenseTransactions = AccountTransaction.ACCOUNT.eq(voucherUnderpaymentAccount).filterObjects(transactions)
        Assertions.assertEquals(2, expenseTransactions.size())

        AccountTransaction asset_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(1) : expenseTransactions.get(0)
        AccountTransaction liability_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(0) : expenseTransactions.get(1)

        Assertions.assertEquals(new Money("220.00"), asset_expense.getAmount())
        Assertions.assertEquals(new Money("-100.00"), liability_expense.getAmount())
    }
}
