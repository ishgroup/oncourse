/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne


import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.common.types.*
import ish.math.Money
import ish.oncourse.common.BankingType
import ish.oncourse.entity.services.SetPaymentMethod
import ish.util.AccountUtil
import ish.util.PaymentMethodUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.time.LocalDate

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/cayenne/TransactionsCreationServertTestDataSet.xml")
class TransactionsCreationServerTest extends TestWithDatabase {
    
    @Test
    void testAutoBank() {
        Invoice invoice = SelectById.query(Invoice.class, 1L).selectOne(cayenneContext)

        PaymentIn paymentIn = cayenneContext.newObject(PaymentIn.class)
        paymentIn.setStatus(PaymentStatus.IN_TRANSACTION)
        paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getRealTimeCreditCardPaymentMethod(cayenneContext, PaymentMethod.class), paymentIn).set()
        paymentIn.setCreditCardName("test name")
        paymentIn.setCreditCardNumber("test number")
        paymentIn.setCreditCardExpiry("01/01")
        paymentIn.setCreditCardType(CreditCardType.VISA)
        paymentIn.setAmount(invoice.getAmountOwing())
        paymentIn.setPayer(invoice.getContact())
        paymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine line = cayenneContext.newObject(PaymentInLine.class)
        line.setAmount(invoice.getAmountOwing())
        line.setPayment(paymentIn)
        line.setInvoice(invoice)
        line.setAccountOut(invoice.getDebtorsAccount())

        cayenneContext.commitChanges()
        cayenneContext.invalidateObjects(line)


        List<AccountTransaction> select = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.PAYMENT_IN_LINE))
                .and(AccountTransaction.FOREIGN_RECORD_ID.eq(line.getId()))
                .select(cayenneContext)
        Assertions.assertTrue(select.isEmpty())
        Assertions.assertNull(paymentIn.getBanking())

        paymentIn.setStatus(PaymentStatus.SUCCESS)

        cayenneContext.commitChanges()

        Assertions.assertNotNull(paymentIn.getBanking())

        List<AccountTransaction> transactions = ObjectSelect.query(AccountTransaction.class).select(cayenneContext)
        Assertions.assertEquals(2, transactions.size())

        AccountTransaction deposit = ObjectSelect.query(AccountTransaction.class).where(AccountTransaction.ACCOUNT.eq(paymentIn.getAccountIn()))
                .and(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.PAYMENT_IN_LINE))
                .selectOne(cayenneContext)

        Assertions.assertNotNull(deposit)
        Assertions.assertEquals(new Money("100.00"), deposit.getAmount())


        AccountTransaction undeposit = ObjectSelect.query(AccountTransaction.class).where(AccountTransaction.ACCOUNT.eq(paymentIn.getUndepositedFundsAccount()))
                .and(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.PAYMENT_IN_LINE))
                .selectOne(cayenneContext)

        Assertions.assertNull(undeposit)

        AccountTransaction debtor = ObjectSelect.query(AccountTransaction.class).where(AccountTransaction.ACCOUNT.eq(line.getInvoice().getDebtorsAccount()))
                .and(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.PAYMENT_IN_LINE))
                .selectOne(cayenneContext)

        Assertions.assertNotNull(debtor)
        Assertions.assertEquals(new Money("-100.00"), debtor.getAmount())
    }


    
    @Test
    void testChangeBanking() {
        ObjectContext context = cayenneService.getNewContext()

        Invoice invoice = SelectById.query(Invoice.class, 1L).selectOne(context)

        PaymentIn paymentIn = context.newObject(PaymentIn.class)
        paymentIn.setStatus(PaymentStatus.SUCCESS)
        paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getRealTimeCreditCardPaymentMethod(context, PaymentMethod.class), paymentIn).set()
        paymentIn.setCreditCardName("test name")
        paymentIn.setCreditCardNumber("test number")
        paymentIn.setCreditCardExpiry("01/01")
        paymentIn.setCreditCardType(CreditCardType.VISA)
        paymentIn.setAmount(invoice.getAmountOwing())
        paymentIn.setPayer(invoice.getContact())
        paymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine line = context.newObject(PaymentInLine.class)
        line.setAmount(invoice.getAmountOwing())
        line.setPayment(paymentIn)
        line.setInvoice(invoice)
        line.setAccountOut(invoice.getDebtorsAccount())

        //bank automatically and check transactions
        context.commitChanges()

        List<AccountTransaction> select = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.PAYMENT_IN_LINE))
                .and(AccountTransaction.FOREIGN_RECORD_ID.eq(line.getId()))
                .select(context)
        Assertions.assertFalse(select.isEmpty())
        Assertions.assertNotNull(paymentIn.getBanking())

        Assertions.assertTrue(ObjectSelect.query(AccountTransaction.class).where(AccountTransaction.ACCOUNT.eq(paymentIn.getUndepositedFundsAccount())).select(context).isEmpty())

        AccountTransaction deposit = ObjectSelect.query(AccountTransaction.class).where(AccountTransaction.ACCOUNT.eq(paymentIn.getAccountIn()))
                .and(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.PAYMENT_IN_LINE))
                .selectOne(context)

        Assertions.assertNotNull(deposit)
        Assertions.assertEquals(new Money("100.00"), deposit.getAmount())

        AccountTransaction debtor = ObjectSelect.query(AccountTransaction.class).where(AccountTransaction.ACCOUNT.eq(line.getInvoice().getDebtorsAccount()))
                .and(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.PAYMENT_IN_LINE))
                .selectOne(context)

        Assertions.assertNotNull(debtor)
        Assertions.assertEquals(new Money("-100.00"), debtor.getAmount())

        //unbank and check transactions
        paymentIn.setBanking(null)
        context.commitChanges()

        AccountTransaction undeposit = ObjectSelect.query(AccountTransaction.class).where(AccountTransaction.ACCOUNT.eq(paymentIn.getUndepositedFundsAccount())).selectOne(context)
        Assertions.assertNotNull(undeposit)
        Assertions.assertEquals(new Money("100.00"), undeposit.getAmount())

        List<AccountTransaction> deposits = ObjectSelect.query(AccountTransaction.class).where(AccountTransaction.ACCOUNT.eq(paymentIn.getAccountIn())).select(context)
        Assertions.assertEquals(2, deposits.size())
        Money depositsSumm = deposits.collect { it.amount }.inject(Money.ZERO) { a, b -> a.add(b) }
        Assertions.assertEquals(Money.ZERO, depositsSumm)


        //bank again to another banking and check transactions (final state)
        LocalDate newSettlementDate = LocalDate.of(2016, 6, 12)
        Banking banking = context.newObject(Banking.class)
        banking.setSettlementDate(newSettlementDate)
        banking.setType(BankingType.AUTO_MCVISA)

        paymentIn.setBanking(banking)
        context.commitChanges()

        AccountTransaction undepositNew = ObjectSelect.query(AccountTransaction.class).where(AccountTransaction.ACCOUNT.eq(paymentIn.getUndepositedFundsAccount()))
                .and(AccountTransaction.TRANSACTION_DATE.eq(newSettlementDate))
                .selectOne(context)

        Assertions.assertNotNull(undepositNew)
        Assertions.assertEquals(new Money("-100.00"), undepositNew.getAmount())

        AccountTransaction depositNew = ObjectSelect.query(AccountTransaction.class).where(AccountTransaction.ACCOUNT.eq(paymentIn.getAccountIn()))
                .and(AccountTransaction.TRANSACTION_DATE.eq(newSettlementDate))
                .selectOne(context)

        Assertions.assertNotNull(depositNew)
        Assertions.assertEquals(new Money("100.00"), depositNew.getAmount())

        List<AccountTransaction> allUndeposit = ObjectSelect.query(AccountTransaction.class).where(AccountTransaction.ACCOUNT.eq(paymentIn.getUndepositedFundsAccount()))
                .select(context)
        List<AccountTransaction> allDeposit = ObjectSelect.query(AccountTransaction.class).where(AccountTransaction.ACCOUNT.eq(paymentIn.getAccountIn()))
                .select(context)

        Assertions.assertEquals(2, allUndeposit.size())

        Assertions.assertEquals(3, allDeposit.size())

        Assertions.assertEquals(Money.ZERO, allUndeposit.collect { it.amount }.inject(Money.ZERO) { a, b -> a.add(b) })
        Assertions.assertEquals(new Money("100.00"), allDeposit.collect { it.amount }.inject(Money.ZERO) { a, b -> a.add(b) })

    }

    
    @Test
    void testZeroPaymentInLineUseCase() {
        ObjectContext context = cayenneService.getNewContext()

        Invoice invoice = SelectById.query(Invoice.class, 1L).selectOne(context)
        Invoice invoice3 = SelectById.query(Invoice.class, 3L).selectOne(context)

        PaymentIn paymentIn = context.newObject(PaymentIn.class)
        paymentIn.setStatus(PaymentStatus.IN_TRANSACTION)
        paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getRealTimeCreditCardPaymentMethod(context, PaymentMethod.class), paymentIn).set()
        paymentIn.setCreditCardName("test name")
        paymentIn.setCreditCardNumber("test number")
        paymentIn.setCreditCardExpiry("01/01")
        paymentIn.setCreditCardType(CreditCardType.VISA)
        paymentIn.setAmount(invoice.getAmountOwing())
        paymentIn.setPayer(invoice.getContact())
        paymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine line = context.newObject(PaymentInLine.class)
        line.setAmount(invoice.getAmountOwing())
        line.setPayment(paymentIn)
        line.setInvoice(invoice)
        line.setAccountOut(invoice.getDebtorsAccount())

        PaymentInLine line2 = context.newObject(PaymentInLine.class)
        line2.setAmount(Money.ZERO)
        line2.setPayment(paymentIn)
        line2.setInvoice(invoice3)
        line2.setAccountOut(invoice3.getDebtorsAccount())

        context.commitChanges()
        context.invalidateObjects(line, line2)

        List<AccountTransaction> select1 = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.PAYMENT_IN_LINE))
                .and(AccountTransaction.FOREIGN_RECORD_ID.eq(line.getId()))
                .select(context)
        List<AccountTransaction> select2 = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.PAYMENT_IN_LINE))
                .and(AccountTransaction.FOREIGN_RECORD_ID.eq(line2.getId()))
                .select(context)
        Assertions.assertTrue(select1.isEmpty())
        Assertions.assertTrue(select2.isEmpty())
        Assertions.assertNull(paymentIn.getBanking())

        paymentIn.setStatus(PaymentStatus.SUCCESS)

        context.commitChanges()

        select1 = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.PAYMENT_IN_LINE))
                .and(AccountTransaction.FOREIGN_RECORD_ID.eq(line.getId()))
                .select(context)
        select2 = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.PAYMENT_IN_LINE))
                .and(AccountTransaction.FOREIGN_RECORD_ID.eq(line2.getId()))
                .select(context)
        Assertions.assertFalse(select1.isEmpty())
        Assertions.assertTrue(select2.isEmpty())
        Assertions.assertNotNull(paymentIn.getBanking())

        paymentIn.setBanking(null)

        context.commitChanges()

        select1 = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.PAYMENT_IN_LINE))
                .and(AccountTransaction.FOREIGN_RECORD_ID.eq(line.getId()))
                .select(context)
        select2 = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.PAYMENT_IN_LINE))
                .and(AccountTransaction.FOREIGN_RECORD_ID.eq(line2.getId()))
                .select(context)
        Assertions.assertFalse(select1.isEmpty())
        Assertions.assertTrue(select2.isEmpty())
    }


    //VoucherProduct Purchase price: $10
    //Redemption : 3 enrolments
    //1)Class cost for redemption : $100
    //2)Class cost for redemption : $30
    
    @Test
    void testPaymentTransactionWithVoucherEnrolment1() {
        ObjectContext context = cayenneService.getNewContext()

        Account assetAccount = SelectById.query(Account.class, 1000L).selectOne(context)
        Account liabilityAccount = AccountUtil.getDefaultVoucherLiabilityAccount(context, Account.class)
        Account expenseAccount = AccountUtil.getDefaultVoucherExpenseAccount(context, Account.class)

        Invoice invoice = SelectById.query(Invoice.class, 7L).selectOne(context)

        PaymentIn paymentIn = context.newObject(PaymentIn.class)
        paymentIn.setStatus(PaymentStatus.SUCCESS)
        paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(context, PaymentMethod.class), paymentIn).set()
        paymentIn.setAmount(Money.ZERO)
        paymentIn.setPayer(invoice.getContact())
        paymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine line = context.newObject(PaymentInLine.class)
        line.setAmount(Money.ZERO)
        line.setPayment(paymentIn)
        line.setInvoice(invoice)
        line.setAccountOut(invoice.getDebtorsAccount())

        PaymentIn paymentInVoucher = context.newObject(PaymentIn.class)
        paymentInVoucher.setStatus(PaymentStatus.SUCCESS)
        paymentInVoucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getVOUCHERPaymentMethods(context, PaymentMethod.class), paymentInVoucher).set()
        paymentInVoucher.setAmount(invoice.getAmountOwing())
        paymentInVoucher.setPayer(invoice.getContact())
        paymentInVoucher.setPaymentDate(LocalDate.now())
        paymentInVoucher.setAccountIn(liabilityAccount)

        PaymentInLine lineVoucher = context.newObject(PaymentInLine.class)
        lineVoucher.setAmount(invoice.getAmountOwing())
        lineVoucher.setPayment(paymentInVoucher)
        lineVoucher.setInvoice(invoice)
        lineVoucher.setAccountOut(invoice.getDebtorsAccount())

        Voucher voucher = SelectById.query(Voucher.class, 1L).selectOne(context)
        voucher.setRedeemedCourseCount(voucher.getRedeemedCourseCount() + 1)

        VoucherPaymentIn voucherPaymentIn = context.newObject(VoucherPaymentIn.class)
        voucherPaymentIn.setPaymentIn(paymentInVoucher)
        voucherPaymentIn.setVoucher(voucher)
        voucherPaymentIn.setStatus(VoucherPaymentStatus.APPROVED)
        voucherPaymentIn.setInvoiceLine(invoice.getInvoiceLines().get(0))


        context.commitChanges()

        List<AccountTransaction> transactions = ObjectSelect.query(AccountTransaction.class).select(context)
        Assertions.assertEquals(4, transactions.size())

        List<AccountTransaction> assetTransactions = AccountTransaction.ACCOUNT.eq(assetAccount).filterObjects(transactions)
        Assertions.assertEquals(1, assetTransactions.size())
        Assertions.assertEquals(new Money("-100.00"), assetTransactions.get(0).getAmount())

        List<AccountTransaction> liabilityTransactions = AccountTransaction.ACCOUNT.eq(liabilityAccount).filterObjects(transactions)
        Assertions.assertEquals(1, liabilityTransactions.size())
        Assertions.assertEquals(new Money("-10.00"), liabilityTransactions.get(0).getAmount())

        List<AccountTransaction> expenseTransactions = AccountTransaction.ACCOUNT.eq(expenseAccount).filterObjects(transactions)
        Assertions.assertEquals(2, expenseTransactions.size())

        AccountTransaction asset_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(1) : expenseTransactions.get(0)
        AccountTransaction liability_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(0) : expenseTransactions.get(1)

        Assertions.assertEquals(new Money("100.00"), asset_expense.getAmount())
        Assertions.assertEquals(new Money("-10.00"), liability_expense.getAmount())


        //redeem next time this voucher

        Invoice secondInvoice = SelectById.query(Invoice.class, 8L).selectOne(context)

        PaymentIn secondPaymentIn = context.newObject(PaymentIn.class)
        secondPaymentIn.setStatus(PaymentStatus.SUCCESS)
        secondPaymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(context, PaymentMethod.class), secondPaymentIn).set()
        secondPaymentIn.setAmount(Money.ZERO)
        secondPaymentIn.setPayer(secondInvoice.getContact())
        secondPaymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine secondLine = context.newObject(PaymentInLine.class)
        secondLine.setAmount(Money.ZERO)
        secondLine.setPayment(secondPaymentIn)
        secondLine.setInvoice(secondInvoice)
        secondLine.setAccountOut(secondInvoice.getDebtorsAccount())

        PaymentIn secondPaymentInVoucher = context.newObject(PaymentIn.class)
        secondPaymentInVoucher.setStatus(PaymentStatus.SUCCESS)
        secondPaymentInVoucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getVOUCHERPaymentMethods(context, PaymentMethod.class), secondPaymentInVoucher).set()
        secondPaymentInVoucher.setAmount(secondInvoice.getAmountOwing())
        secondPaymentInVoucher.setPayer(secondInvoice.getContact())
        secondPaymentInVoucher.setPaymentDate(LocalDate.now())
        secondPaymentInVoucher.setAccountIn(liabilityAccount)

        PaymentInLine secondLineVoucher = context.newObject(PaymentInLine.class)
        secondLineVoucher.setAmount(secondInvoice.getAmountOwing())
        secondLineVoucher.setPayment(secondPaymentInVoucher)
        secondLineVoucher.setInvoice(secondInvoice)
        secondLineVoucher.setAccountOut(secondInvoice.getDebtorsAccount())

        voucher.setRedeemedCourseCount(voucher.getRedeemedCourseCount() + 1)

        VoucherPaymentIn secondVoucherPaymentIn = context.newObject(VoucherPaymentIn.class)
        secondVoucherPaymentIn.setPaymentIn(secondPaymentInVoucher)
        secondVoucherPaymentIn.setVoucher(voucher)
        secondVoucherPaymentIn.setStatus(VoucherPaymentStatus.APPROVED)
        secondVoucherPaymentIn.setInvoiceLine(secondInvoice.getInvoiceLines().get(0))

        context.commitChanges()

        List<AccountTransaction> secondTransactions = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.FOREIGN_RECORD_ID.eq(secondLineVoucher.getId()))
                .select(context)
        Assertions.assertEquals(2, secondTransactions.size())

        List<AccountTransaction> asset2Transactions = AccountTransaction.ACCOUNT.eq(assetAccount).filterObjects(secondTransactions)
        Assertions.assertEquals(1, asset2Transactions.size())
        Assertions.assertEquals(new Money("-30.00"), asset2Transactions.get(0).getAmount())

        List<AccountTransaction> expense2Transactions = AccountTransaction.ACCOUNT.eq(expenseAccount).filterObjects(secondTransactions)
        Assertions.assertEquals(1, expense2Transactions.size())
        Assertions.assertEquals(new Money("30.00"), expense2Transactions.get(0).getAmount())
    }

    //VoucherProduct Purchase price: $120
    //Redemption : 3 enrolments
    //1)Class cost for redemption : $100
    //2)Class cost for redemption : $30
    
    @Test
    void testPaymentTransactionWithVoucherEnrolment2() {
        ObjectContext context = cayenneService.getNewContext()

        Account assetAccount = SelectById.query(Account.class, 1000L).selectOne(context)
        Account liabilityAccount = AccountUtil.getDefaultVoucherLiabilityAccount(context, Account.class)
        Account expenseAccount = AccountUtil.getDefaultVoucherExpenseAccount(context, Account.class)

        Invoice invoice = SelectById.query(Invoice.class, 7L).selectOne(context)

        PaymentIn paymentIn = context.newObject(PaymentIn.class)
        paymentIn.setStatus(PaymentStatus.SUCCESS)
        paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(context, PaymentMethod.class), paymentIn).set()
        paymentIn.setAmount(Money.ZERO)
        paymentIn.setPayer(invoice.getContact())
        paymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine line = context.newObject(PaymentInLine.class)
        line.setAmount(Money.ZERO)
        line.setPayment(paymentIn)
        line.setInvoice(invoice)
        line.setAccountOut(invoice.getDebtorsAccount())

        PaymentIn paymentInVoucher = context.newObject(PaymentIn.class)
        paymentInVoucher.setStatus(PaymentStatus.SUCCESS)
        paymentInVoucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getVOUCHERPaymentMethods(context, PaymentMethod.class), paymentInVoucher).set()
        paymentInVoucher.setAmount(invoice.getAmountOwing())
        paymentInVoucher.setPayer(invoice.getContact())
        paymentInVoucher.setPaymentDate(LocalDate.now())
        paymentInVoucher.setAccountIn(liabilityAccount)

        PaymentInLine lineVoucher = context.newObject(PaymentInLine.class)
        lineVoucher.setAmount(invoice.getAmountOwing())
        lineVoucher.setPayment(paymentInVoucher)
        lineVoucher.setInvoice(invoice)
        lineVoucher.setAccountOut(invoice.getDebtorsAccount())

        Voucher voucher = SelectById.query(Voucher.class, 4L).selectOne(context)
        voucher.setRedeemedCourseCount(voucher.getRedeemedCourseCount() + 1)

        VoucherPaymentIn voucherPaymentIn = context.newObject(VoucherPaymentIn.class)
        voucherPaymentIn.setPaymentIn(paymentInVoucher)
        voucherPaymentIn.setVoucher(voucher)
        voucherPaymentIn.setStatus(VoucherPaymentStatus.APPROVED)
        voucherPaymentIn.setInvoiceLine(invoice.getInvoiceLines().get(0))


        context.commitChanges()

        List<AccountTransaction> transactions = ObjectSelect.query(AccountTransaction.class).select(context)
        Assertions.assertEquals(4, transactions.size())

        List<AccountTransaction> assetTransactions = AccountTransaction.ACCOUNT.eq(assetAccount).filterObjects(transactions)
        Assertions.assertEquals(1, assetTransactions.size())
        Assertions.assertEquals(new Money("-100.00"), assetTransactions.get(0).getAmount())

        List<AccountTransaction> liabilityTransactions = AccountTransaction.ACCOUNT.eq(liabilityAccount).filterObjects(transactions)
        Assertions.assertEquals(1, liabilityTransactions.size())
        Assertions.assertEquals(new Money("-100.00"), liabilityTransactions.get(0).getAmount())

        List<AccountTransaction> expenseTransactions = AccountTransaction.ACCOUNT.eq(expenseAccount).filterObjects(transactions)
        Assertions.assertEquals(2, expenseTransactions.size())

        AccountTransaction asset_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(1) : expenseTransactions.get(0)
        AccountTransaction liability_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(0) : expenseTransactions.get(1)

        Assertions.assertEquals(new Money("100.00"), asset_expense.getAmount())
        Assertions.assertEquals(new Money("-100.00"), liability_expense.getAmount())


        //redeem next time this voucher

        Invoice secondInvoice = SelectById.query(Invoice.class, 8L).selectOne(context)

        PaymentIn secondPaymentIn = context.newObject(PaymentIn.class)
        secondPaymentIn.setStatus(PaymentStatus.SUCCESS)
        secondPaymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(context, PaymentMethod.class), secondPaymentIn).set()
        secondPaymentIn.setAmount(Money.ZERO)
        secondPaymentIn.setPayer(secondInvoice.getContact())
        secondPaymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine secondLine = context.newObject(PaymentInLine.class)
        secondLine.setAmount(Money.ZERO)
        secondLine.setPayment(secondPaymentIn)
        secondLine.setInvoice(secondInvoice)
        secondLine.setAccountOut(secondInvoice.getDebtorsAccount())

        PaymentIn secondPaymentInVoucher = context.newObject(PaymentIn.class)
        secondPaymentInVoucher.setStatus(PaymentStatus.SUCCESS)
        secondPaymentInVoucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getVOUCHERPaymentMethods(context, PaymentMethod.class), secondPaymentInVoucher).set()
        secondPaymentInVoucher.setAmount(secondInvoice.getAmountOwing())
        secondPaymentInVoucher.setPayer(secondInvoice.getContact())
        secondPaymentInVoucher.setPaymentDate(LocalDate.now())
        secondPaymentInVoucher.setAccountIn(liabilityAccount)

        PaymentInLine secondLineVoucher = context.newObject(PaymentInLine.class)
        secondLineVoucher.setAmount(secondInvoice.getAmountOwing())
        secondLineVoucher.setPayment(secondPaymentInVoucher)
        secondLineVoucher.setInvoice(secondInvoice)
        secondLineVoucher.setAccountOut(secondInvoice.getDebtorsAccount())

        voucher.setRedeemedCourseCount(voucher.getRedeemedCourseCount() + 1)

        VoucherPaymentIn secondVoucherPaymentIn = context.newObject(VoucherPaymentIn.class)
        secondVoucherPaymentIn.setPaymentIn(secondPaymentInVoucher)
        secondVoucherPaymentIn.setVoucher(voucher)
        secondVoucherPaymentIn.setStatus(VoucherPaymentStatus.APPROVED)
        secondVoucherPaymentIn.setInvoiceLine(secondInvoice.getInvoiceLines().get(0))

        context.commitChanges()

        List<AccountTransaction> secondTransactions = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.FOREIGN_RECORD_ID.eq(secondLineVoucher.getId()))
                .select(context)
        Assertions.assertEquals(4, secondTransactions.size())

        List<AccountTransaction> asset2Transactions = AccountTransaction.ACCOUNT.eq(assetAccount).filterObjects(secondTransactions)
        Assertions.assertEquals(1, asset2Transactions.size())
        Assertions.assertEquals(new Money("-30.00"), asset2Transactions.get(0).getAmount())

        List<AccountTransaction> liability2Transactions = AccountTransaction.ACCOUNT.eq(liabilityAccount).filterObjects(secondTransactions)
        Assertions.assertEquals(1, liability2Transactions.size())
        Assertions.assertEquals(new Money("-20.00"), liability2Transactions.get(0).getAmount())

        List<AccountTransaction> expense2Transactions = AccountTransaction.ACCOUNT.eq(expenseAccount).filterObjects(secondTransactions)
        Assertions.assertEquals(2, expense2Transactions.size())

        AccountTransaction asset_expense2 = expense2Transactions.get(0).getAmount().isNegative() ? expense2Transactions.get(1) : expense2Transactions.get(0)
        AccountTransaction liability_expense2 = expense2Transactions.get(0).getAmount().isNegative() ? expense2Transactions.get(0) : expense2Transactions.get(1)

        Assertions.assertEquals(new Money("30.00"), asset_expense2.getAmount())
        Assertions.assertEquals(new Money("-20.00"), liability_expense2.getAmount())
    }

    //VoucherProduct Purchase price: $120
    //Redemption : 1 enrolments
    //1)Class cost for redemption : $100
    
    @Test
    void testPaymentTransactionWithVoucherEnrolment3() {
        ObjectContext context = cayenneService.getNewContext()

        Account assetAccount = SelectById.query(Account.class, 1000L).selectOne(context)
        Account liabilityAccount = AccountUtil.getDefaultVoucherLiabilityAccount(context, Account.class)
        Account expenseAccount = AccountUtil.getDefaultVoucherExpenseAccount(context, Account.class)

        Invoice invoice = SelectById.query(Invoice.class, 7L).selectOne(context)

        PaymentIn paymentIn = context.newObject(PaymentIn.class)
        paymentIn.setStatus(PaymentStatus.SUCCESS)
        paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(context, PaymentMethod.class), paymentIn).set()
        paymentIn.setAmount(Money.ZERO)
        paymentIn.setPayer(invoice.getContact())
        paymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine line = context.newObject(PaymentInLine.class)
        line.setAmount(Money.ZERO)
        line.setPayment(paymentIn)
        line.setInvoice(invoice)
        line.setAccountOut(invoice.getDebtorsAccount())

        PaymentIn paymentInVoucher = context.newObject(PaymentIn.class)
        paymentInVoucher.setStatus(PaymentStatus.SUCCESS)
        paymentInVoucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getVOUCHERPaymentMethods(context, PaymentMethod.class), paymentInVoucher).set()
        paymentInVoucher.setAmount(invoice.getAmountOwing())
        paymentInVoucher.setPayer(invoice.getContact())
        paymentInVoucher.setPaymentDate(LocalDate.now())
        paymentInVoucher.setAccountIn(liabilityAccount)

        PaymentInLine lineVoucher = context.newObject(PaymentInLine.class)
        lineVoucher.setAmount(invoice.getAmountOwing())
        lineVoucher.setPayment(paymentInVoucher)
        lineVoucher.setInvoice(invoice)
        lineVoucher.setAccountOut(invoice.getDebtorsAccount())

        Voucher voucher = SelectById.query(Voucher.class, 6L).selectOne(context)
        voucher.setRedeemedCourseCount(voucher.getRedeemedCourseCount() + 1)

        VoucherPaymentIn voucherPaymentIn = context.newObject(VoucherPaymentIn.class)
        voucherPaymentIn.setPaymentIn(paymentInVoucher)
        voucherPaymentIn.setVoucher(voucher)
        voucherPaymentIn.setStatus(VoucherPaymentStatus.APPROVED)
        voucherPaymentIn.setInvoiceLine(invoice.getInvoiceLines().get(0))


        context.commitChanges()

        List<AccountTransaction> transactions = ObjectSelect.query(AccountTransaction.class).select(context)
        Assertions.assertEquals(4, transactions.size())

        List<AccountTransaction> assetTransactions = AccountTransaction.ACCOUNT.eq(assetAccount).filterObjects(transactions)
        Assertions.assertEquals(1, assetTransactions.size())
        Assertions.assertEquals(new Money("-100.00"), assetTransactions.get(0).getAmount())

        List<AccountTransaction> liabilityTransactions = AccountTransaction.ACCOUNT.eq(liabilityAccount).filterObjects(transactions)
        Assertions.assertEquals(1, liabilityTransactions.size())
        Assertions.assertEquals(new Money("-120.00"), liabilityTransactions.get(0).getAmount())

        List<AccountTransaction> expenseTransactions = AccountTransaction.ACCOUNT.eq(expenseAccount).filterObjects(transactions)
        Assertions.assertEquals(2, expenseTransactions.size())

        AccountTransaction asset_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(1) : expenseTransactions.get(0)
        AccountTransaction liability_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(0) : expenseTransactions.get(1)

        Assertions.assertEquals(new Money("100.00"), asset_expense.getAmount())
        Assertions.assertEquals(new Money("-120.00"), liability_expense.getAmount())
    }

    //VoucherProduct Purchase price: $30
    //Redemption : $150
    //1)Class cost for redemption : $100
    //2)Class cost for redemption : $30
    
    @Test
    void testPaymentTransactionWithVoucherValue1() {
        ObjectContext context = cayenneService.getNewContext()

        Account assetAccount = SelectById.query(Account.class, 1000L).selectOne(context)
        Account liabilityAccount = AccountUtil.getDefaultVoucherLiabilityAccount(context, Account.class)
        Account expenseAccount = AccountUtil.getDefaultVoucherExpenseAccount(context, Account.class)

        Invoice invoice = SelectById.query(Invoice.class, 7L).selectOne(context)

        PaymentIn paymentIn = context.newObject(PaymentIn.class)
        paymentIn.setStatus(PaymentStatus.SUCCESS)
        paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(context, PaymentMethod.class), paymentIn).set()
        paymentIn.setAmount(Money.ZERO)
        paymentIn.setPayer(invoice.getContact())
        paymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine line = context.newObject(PaymentInLine.class)
        line.setAmount(Money.ZERO)
        line.setPayment(paymentIn)
        line.setInvoice(invoice)
        line.setAccountOut(invoice.getDebtorsAccount())

        PaymentIn paymentInVoucher = context.newObject(PaymentIn.class)
        paymentInVoucher.setStatus(PaymentStatus.SUCCESS)
        paymentInVoucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getVOUCHERPaymentMethods(context, PaymentMethod.class), paymentInVoucher).set()
        paymentInVoucher.setAmount(invoice.getAmountOwing())
        paymentInVoucher.setPayer(invoice.getContact())
        paymentInVoucher.setPaymentDate(LocalDate.now())
        paymentInVoucher.setAccountIn(liabilityAccount)

        PaymentInLine lineVoucher = context.newObject(PaymentInLine.class)
        lineVoucher.setAmount(invoice.getAmountOwing())
        lineVoucher.setPayment(paymentInVoucher)
        lineVoucher.setInvoice(invoice)
        lineVoucher.setAccountOut(invoice.getDebtorsAccount())

        Voucher voucher = SelectById.query(Voucher.class, 2L).selectOne(context)
        Money balance = voucher.getRedemptionValue().subtract(invoice.getAmountOwing())
        voucher.setRedemptionValue(balance)

        VoucherPaymentIn voucherPaymentIn = context.newObject(VoucherPaymentIn.class)
        voucherPaymentIn.setPaymentIn(paymentInVoucher)
        voucherPaymentIn.setVoucher(voucher)
        voucherPaymentIn.setStatus(VoucherPaymentStatus.APPROVED)

        context.commitChanges()


        List<AccountTransaction> transactions = ObjectSelect.query(AccountTransaction.class).select(context)
        Assertions.assertEquals(4, transactions.size())

        List<AccountTransaction> assetTransactions = AccountTransaction.ACCOUNT.eq(assetAccount).filterObjects(transactions)
        Assertions.assertEquals(1, assetTransactions.size())
        Assertions.assertEquals(new Money("-100.00"), assetTransactions.get(0).getAmount())

        List<AccountTransaction> liabilityTransactions = AccountTransaction.ACCOUNT.eq(liabilityAccount).filterObjects(transactions)
        Assertions.assertEquals(1, liabilityTransactions.size())
        Assertions.assertEquals(new Money("-30.00"), liabilityTransactions.get(0).getAmount())

        List<AccountTransaction> expenseTransactions = AccountTransaction.ACCOUNT.eq(expenseAccount).filterObjects(transactions)
        Assertions.assertEquals(2, expenseTransactions.size())

        AccountTransaction asset_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(1) : expenseTransactions.get(0)
        AccountTransaction liability_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(0) : expenseTransactions.get(1)

        Assertions.assertEquals(new Money("100.00"), asset_expense.getAmount())
        Assertions.assertEquals(new Money("-30.00"), liability_expense.getAmount())

        //redeem next time this voucher

        Invoice secondInvoice = SelectById.query(Invoice.class, 8L).selectOne(context)

        PaymentIn secondPaymentIn = context.newObject(PaymentIn.class)
        secondPaymentIn.setStatus(PaymentStatus.SUCCESS)
        secondPaymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(context, PaymentMethod.class), secondPaymentIn).set()
        secondPaymentIn.setAmount(Money.ZERO)
        secondPaymentIn.setPayer(secondInvoice.getContact())
        secondPaymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine secondLine = context.newObject(PaymentInLine.class)
        secondLine.setAmount(Money.ZERO)
        secondLine.setPayment(secondPaymentIn)
        secondLine.setInvoice(secondInvoice)
        secondLine.setAccountOut(secondInvoice.getDebtorsAccount())

        PaymentIn secondPaymentInVoucher = context.newObject(PaymentIn.class)
        secondPaymentInVoucher.setStatus(PaymentStatus.SUCCESS)
        secondPaymentInVoucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getVOUCHERPaymentMethods(context, PaymentMethod.class), secondPaymentInVoucher).set()
        secondPaymentInVoucher.setAmount(secondInvoice.getAmountOwing())
        secondPaymentInVoucher.setPayer(secondInvoice.getContact())
        secondPaymentInVoucher.setPaymentDate(LocalDate.now())
        secondPaymentInVoucher.setAccountIn(liabilityAccount)

        PaymentInLine secondLineVoucher = context.newObject(PaymentInLine.class)
        secondLineVoucher.setAmount(secondInvoice.getAmountOwing())
        secondLineVoucher.setPayment(secondPaymentInVoucher)
        secondLineVoucher.setInvoice(secondInvoice)
        secondLineVoucher.setAccountOut(secondInvoice.getDebtorsAccount())

        Voucher secondVoucher = SelectById.query(Voucher.class, 2L).selectOne(context)

        VoucherPaymentIn secondVoucherPaymentIn = context.newObject(VoucherPaymentIn.class)
        secondVoucherPaymentIn.setPaymentIn(secondPaymentInVoucher)
        secondVoucherPaymentIn.setVoucher(secondVoucher)
        secondVoucherPaymentIn.setStatus(VoucherPaymentStatus.APPROVED)
        secondVoucherPaymentIn.setInvoiceLine(secondInvoice.getInvoiceLines().get(0))

        context.commitChanges()

        List<AccountTransaction> secondTransactions = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.FOREIGN_RECORD_ID.eq(secondLineVoucher.getId()))
                .select(context)
        Assertions.assertEquals(2, secondTransactions.size())

        List<AccountTransaction> asset2Transactions = AccountTransaction.ACCOUNT.eq(assetAccount).filterObjects(secondTransactions)
        Assertions.assertEquals(1, asset2Transactions.size())
        Assertions.assertEquals(new Money("-30.00"), asset2Transactions.get(0).getAmount())

        List<AccountTransaction> expense2Transactions = AccountTransaction.ACCOUNT.eq(expenseAccount).filterObjects(secondTransactions)
        Assertions.assertEquals(1, expense2Transactions.size())
        Assertions.assertEquals(new Money("30.00"), expense2Transactions.get(0).getAmount())
    }

    //VoucherProduct Purchase price: $120
    //Redemption : $120
    //1)Class cost for redemption : $100
    //2)Class cost for redemption : $30
    
    @Test
    void testPaymentTransactionWithVoucherValue2() {
        ObjectContext context = cayenneService.getNewContext()

        Account assetAccount = SelectById.query(Account.class, 1000L).selectOne(context)
        Account liabilityAccount = AccountUtil.getDefaultVoucherLiabilityAccount(context, Account.class)
        Account expenseAccount = AccountUtil.getDefaultVoucherExpenseAccount(context, Account.class)

        Invoice invoice = SelectById.query(Invoice.class, 7L).selectOne(context)

        PaymentIn paymentIn = context.newObject(PaymentIn.class)
        paymentIn.setStatus(PaymentStatus.SUCCESS)
        paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(context, PaymentMethod.class), paymentIn).set()
        paymentIn.setAmount(Money.ZERO)
        paymentIn.setPayer(invoice.getContact())
        paymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine line = context.newObject(PaymentInLine.class)
        line.setAmount(Money.ZERO)
        line.setPayment(paymentIn)
        line.setInvoice(invoice)
        line.setAccountOut(invoice.getDebtorsAccount())

        PaymentIn paymentInVoucher = context.newObject(PaymentIn.class)
        paymentInVoucher.setStatus(PaymentStatus.SUCCESS)
        paymentInVoucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getVOUCHERPaymentMethods(context, PaymentMethod.class), paymentInVoucher).set()
        paymentInVoucher.setAmount(invoice.getAmountOwing())
        paymentInVoucher.setPayer(invoice.getContact())
        paymentInVoucher.setPaymentDate(LocalDate.now())
        paymentInVoucher.setAccountIn(liabilityAccount)

        PaymentInLine lineVoucher = context.newObject(PaymentInLine.class)
        lineVoucher.setAmount(invoice.getAmountOwing())
        lineVoucher.setPayment(paymentInVoucher)
        lineVoucher.setInvoice(invoice)
        lineVoucher.setAccountOut(invoice.getDebtorsAccount())

        Voucher voucher = SelectById.query(Voucher.class, 5L).selectOne(context)
        Money balance = voucher.getRedemptionValue().subtract(invoice.getAmountOwing())
        voucher.setRedemptionValue(balance)

        VoucherPaymentIn voucherPaymentIn = context.newObject(VoucherPaymentIn.class)
        voucherPaymentIn.setPaymentIn(paymentInVoucher)
        voucherPaymentIn.setVoucher(voucher)
        voucherPaymentIn.setStatus(VoucherPaymentStatus.APPROVED)

        context.commitChanges()


        List<AccountTransaction> transactions = ObjectSelect.query(AccountTransaction.class).select(context)
        Assertions.assertEquals(4, transactions.size())

        List<AccountTransaction> assetTransactions = AccountTransaction.ACCOUNT.eq(assetAccount).filterObjects(transactions)
        Assertions.assertEquals(1, assetTransactions.size())
        Assertions.assertEquals(new Money("-100.00"), assetTransactions.get(0).getAmount())

        List<AccountTransaction> liabilityTransactions = AccountTransaction.ACCOUNT.eq(liabilityAccount).filterObjects(transactions)
        Assertions.assertEquals(1, liabilityTransactions.size())
        Assertions.assertEquals(new Money("-100.00"), liabilityTransactions.get(0).getAmount())

        List<AccountTransaction> expenseTransactions = AccountTransaction.ACCOUNT.eq(expenseAccount).filterObjects(transactions)
        Assertions.assertEquals(2, expenseTransactions.size())

        AccountTransaction asset_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(1) : expenseTransactions.get(0)
        AccountTransaction liability_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(0) : expenseTransactions.get(1)

        Assertions.assertEquals(new Money("100.00"), asset_expense.getAmount())
        Assertions.assertEquals(new Money("-100.00"), liability_expense.getAmount())

        //redeem next time this voucher

        Invoice secondInvoice = SelectById.query(Invoice.class, 8L).selectOne(context)

        PaymentIn secondPaymentIn = context.newObject(PaymentIn.class)
        secondPaymentIn.setStatus(PaymentStatus.SUCCESS)
        secondPaymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(context, PaymentMethod.class), secondPaymentIn).set()
        secondPaymentIn.setAmount(Money.ZERO)
        secondPaymentIn.setPayer(secondInvoice.getContact())
        secondPaymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine secondLine = context.newObject(PaymentInLine.class)
        secondLine.setAmount(Money.ZERO)
        secondLine.setPayment(secondPaymentIn)
        secondLine.setInvoice(secondInvoice)
        secondLine.setAccountOut(secondInvoice.getDebtorsAccount())

        PaymentIn secondPaymentInVoucher = context.newObject(PaymentIn.class)
        secondPaymentInVoucher.setStatus(PaymentStatus.SUCCESS)
        secondPaymentInVoucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getVOUCHERPaymentMethods(context, PaymentMethod.class), secondPaymentInVoucher).set()
        secondPaymentInVoucher.setAmount(secondInvoice.getAmountOwing())
        secondPaymentInVoucher.setPayer(secondInvoice.getContact())
        secondPaymentInVoucher.setPaymentDate(LocalDate.now())
        secondPaymentInVoucher.setAccountIn(liabilityAccount)

        PaymentInLine secondLineVoucher = context.newObject(PaymentInLine.class)
        secondLineVoucher.setAmount(secondInvoice.getAmountOwing())
        secondLineVoucher.setPayment(secondPaymentInVoucher)
        secondLineVoucher.setInvoice(secondInvoice)
        secondLineVoucher.setAccountOut(secondInvoice.getDebtorsAccount())

        VoucherPaymentIn secondVoucherPaymentIn = context.newObject(VoucherPaymentIn.class)
        secondVoucherPaymentIn.setPaymentIn(secondPaymentInVoucher)
        secondVoucherPaymentIn.setVoucher(voucher)
        secondVoucherPaymentIn.setStatus(VoucherPaymentStatus.APPROVED)
        secondVoucherPaymentIn.setInvoiceLine(secondInvoice.getInvoiceLines().get(0))

        context.commitChanges()

        List<AccountTransaction> secondTransactions = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.FOREIGN_RECORD_ID.eq(secondLineVoucher.getId()))
                .select(context)
        Assertions.assertEquals(4, secondTransactions.size())

        List<AccountTransaction> asset2Transactions = AccountTransaction.ACCOUNT.eq(assetAccount).filterObjects(secondTransactions)
        Assertions.assertEquals(1, asset2Transactions.size())
        Assertions.assertEquals(new Money("-30.00"), asset2Transactions.get(0).getAmount())

        List<AccountTransaction> liability2Transactions = AccountTransaction.ACCOUNT.eq(liabilityAccount).filterObjects(secondTransactions)
        Assertions.assertEquals(1, liability2Transactions.size())
        Assertions.assertEquals(new Money("-20.00"), liability2Transactions.get(0).getAmount())

        List<AccountTransaction> expense2Transactions = AccountTransaction.ACCOUNT.eq(expenseAccount).filterObjects(secondTransactions)
        Assertions.assertEquals(2, expense2Transactions.size())

        AccountTransaction asset_expense2 = expense2Transactions.get(0).getAmount().isNegative() ? expense2Transactions.get(1) : expense2Transactions.get(0)
        AccountTransaction liability_expense2 = expense2Transactions.get(0).getAmount().isNegative() ? expense2Transactions.get(0) : expense2Transactions.get(1)

        Assertions.assertEquals(new Money("30.00"), asset_expense2.getAmount())
        Assertions.assertEquals(new Money("-20.00"), liability_expense2.getAmount())

    }

    //VoucherProduct Purchase price: $180
    //Redemption : $100
    //1)Class cost for redemption : $100
    
    @Test
    void testPaymentTransactionWithVoucherValue3() {
        ObjectContext context = cayenneService.getNewContext()

        Account assetAccount = SelectById.query(Account.class, 1000L).selectOne(context)
        Account liabilityAccount = AccountUtil.getDefaultVoucherLiabilityAccount(context, Account.class)
        Account expenseAccount = AccountUtil.getDefaultVoucherExpenseAccount(context, Account.class)

        Invoice invoice = SelectById.query(Invoice.class, 7L).selectOne(context)

        PaymentIn paymentIn = context.newObject(PaymentIn.class)
        paymentIn.setStatus(PaymentStatus.SUCCESS)
        paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(context, PaymentMethod.class), paymentIn).set()
        paymentIn.setAmount(Money.ZERO)
        paymentIn.setPayer(invoice.getContact())
        paymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine line = context.newObject(PaymentInLine.class)
        line.setAmount(Money.ZERO)
        line.setPayment(paymentIn)
        line.setInvoice(invoice)
        line.setAccountOut(invoice.getDebtorsAccount())

        PaymentIn paymentInVoucher = context.newObject(PaymentIn.class)
        paymentInVoucher.setStatus(PaymentStatus.SUCCESS)
        paymentInVoucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getVOUCHERPaymentMethods(context, PaymentMethod.class), paymentInVoucher).set()
        paymentInVoucher.setAmount(invoice.getAmountOwing())
        paymentInVoucher.setPayer(invoice.getContact())
        paymentInVoucher.setPaymentDate(LocalDate.now())
        paymentInVoucher.setAccountIn(liabilityAccount)

        PaymentInLine lineVoucher = context.newObject(PaymentInLine.class)
        lineVoucher.setAmount(invoice.getAmountOwing())
        lineVoucher.setPayment(paymentInVoucher)
        lineVoucher.setInvoice(invoice)
        lineVoucher.setAccountOut(invoice.getDebtorsAccount())

        Voucher voucher = SelectById.query(Voucher.class, 7L).selectOne(context)
        Money balance = voucher.getRedemptionValue().subtract(invoice.getAmountOwing())
        voucher.setRedemptionValue(balance)

        VoucherPaymentIn voucherPaymentIn = context.newObject(VoucherPaymentIn.class)
        voucherPaymentIn.setPaymentIn(paymentInVoucher)
        voucherPaymentIn.setVoucher(voucher)
        voucherPaymentIn.setStatus(VoucherPaymentStatus.APPROVED)

        context.commitChanges()


        List<AccountTransaction> transactions = ObjectSelect.query(AccountTransaction.class).select(context)
        Assertions.assertEquals(4, transactions.size())

        List<AccountTransaction> assetTransactions = AccountTransaction.ACCOUNT.eq(assetAccount).filterObjects(transactions)
        Assertions.assertEquals(1, assetTransactions.size())
        Assertions.assertEquals(new Money("-100.00"), assetTransactions.get(0).getAmount())

        List<AccountTransaction> liabilityTransactions = AccountTransaction.ACCOUNT.eq(liabilityAccount).filterObjects(transactions)
        Assertions.assertEquals(1, liabilityTransactions.size())
        Assertions.assertEquals(new Money("-180.00"), liabilityTransactions.get(0).getAmount())

        List<AccountTransaction> expenseTransactions = AccountTransaction.ACCOUNT.eq(expenseAccount).filterObjects(transactions)
        Assertions.assertEquals(2, expenseTransactions.size())

        AccountTransaction asset_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(1) : expenseTransactions.get(0)
        AccountTransaction liability_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(0) : expenseTransactions.get(1)

        Assertions.assertEquals(new Money("100.00"), asset_expense.getAmount())
        Assertions.assertEquals(new Money("-180.00"), liability_expense.getAmount())
    }

    //VoucherProduct Purchase price: $200
    //Redemption : $200
    //1)Class cost for redemption : $100
    //2)Class cost for redemption : $30
    
    @Test
    void testPaymentTransactionWithVoucherPurchaseValue() {
        ObjectContext context = cayenneService.getNewContext()

        Account assetAccount = SelectById.query(Account.class, 1000L).selectOne(context)
        Account liabilityAccount = AccountUtil.getDefaultVoucherLiabilityAccount(context, Account.class)
        Account expenseAccount = AccountUtil.getDefaultVoucherExpenseAccount(context, Account.class)

        Invoice invoice = SelectById.query(Invoice.class, 7L).selectOne(context)

        PaymentIn paymentIn = context.newObject(PaymentIn.class)
        paymentIn.setStatus(PaymentStatus.SUCCESS)
        paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(context, PaymentMethod.class), paymentIn).set()
        paymentIn.setAmount(Money.ZERO)
        paymentIn.setPayer(invoice.getContact())
        paymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine line = context.newObject(PaymentInLine.class)
        line.setAmount(Money.ZERO)
        line.setPayment(paymentIn)
        line.setInvoice(invoice)
        line.setAccountOut(invoice.getDebtorsAccount())

        PaymentIn paymentInVoucher = context.newObject(PaymentIn.class)
        paymentInVoucher.setStatus(PaymentStatus.SUCCESS)
        paymentInVoucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getVOUCHERPaymentMethods(context, PaymentMethod.class), paymentInVoucher).set()
        paymentInVoucher.setAmount(invoice.getAmountOwing())
        paymentInVoucher.setPayer(invoice.getContact())
        paymentInVoucher.setPaymentDate(LocalDate.now())
        paymentInVoucher.setAccountIn(liabilityAccount)

        PaymentInLine lineVoucher = context.newObject(PaymentInLine.class)
        lineVoucher.setAmount(invoice.getAmountOwing())
        lineVoucher.setPayment(paymentInVoucher)
        lineVoucher.setInvoice(invoice)
        lineVoucher.setAccountOut(invoice.getDebtorsAccount())

        Voucher voucher = SelectById.query(Voucher.class, 3L).selectOne(context)
        Money balance = voucher.getRedemptionValue().subtract(invoice.getAmountOwing())
        voucher.setRedemptionValue(balance)

        VoucherPaymentIn voucherPaymentIn = context.newObject(VoucherPaymentIn.class)
        voucherPaymentIn.setPaymentIn(paymentInVoucher)
        voucherPaymentIn.setVoucher(voucher)
        voucherPaymentIn.setStatus(VoucherPaymentStatus.APPROVED)

        context.commitChanges()


        List<AccountTransaction> transactions = ObjectSelect.query(AccountTransaction.class).select(context)
        Assertions.assertEquals(4, transactions.size())

        List<AccountTransaction> assetTransactions = AccountTransaction.ACCOUNT.eq(assetAccount).filterObjects(transactions)
        Assertions.assertEquals(1, assetTransactions.size())
        Assertions.assertEquals(new Money("-100.00"), assetTransactions.get(0).getAmount())

        List<AccountTransaction> liabilityTransactions = AccountTransaction.ACCOUNT.eq(liabilityAccount).filterObjects(transactions)
        Assertions.assertEquals(1, liabilityTransactions.size())
        Assertions.assertEquals(new Money("-100.00"), liabilityTransactions.get(0).getAmount())

        List<AccountTransaction> expenseTransactions = AccountTransaction.ACCOUNT.eq(expenseAccount).filterObjects(transactions)
        Assertions.assertEquals(2, expenseTransactions.size())

        AccountTransaction asset_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(1) : expenseTransactions.get(0)
        AccountTransaction liability_expense = expenseTransactions.get(0).getAmount().isNegative() ? expenseTransactions.get(0) : expenseTransactions.get(1)

        Assertions.assertEquals(new Money("100.00"), asset_expense.getAmount())
        Assertions.assertEquals(new Money("-100.00"), liability_expense.getAmount())


        //redeem next time this voucher

        Invoice secondInvoice = SelectById.query(Invoice.class, 8L).selectOne(context)

        PaymentIn secondPaymentIn = context.newObject(PaymentIn.class)
        secondPaymentIn.setStatus(PaymentStatus.SUCCESS)
        secondPaymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(context, PaymentMethod.class), secondPaymentIn).set()
        secondPaymentIn.setAmount(Money.ZERO)
        secondPaymentIn.setPayer(secondInvoice.getContact())
        secondPaymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine secondLine = context.newObject(PaymentInLine.class)
        secondLine.setAmount(Money.ZERO)
        secondLine.setPayment(secondPaymentIn)
        secondLine.setInvoice(secondInvoice)
        secondLine.setAccountOut(secondInvoice.getDebtorsAccount())

        PaymentIn secondPaymentInVoucher = context.newObject(PaymentIn.class)
        secondPaymentInVoucher.setStatus(PaymentStatus.SUCCESS)
        secondPaymentInVoucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getVOUCHERPaymentMethods(context, PaymentMethod.class), secondPaymentInVoucher).set()
        secondPaymentInVoucher.setAmount(secondInvoice.getAmountOwing())
        secondPaymentInVoucher.setPayer(secondInvoice.getContact())
        secondPaymentInVoucher.setPaymentDate(LocalDate.now())
        secondPaymentInVoucher.setAccountIn(liabilityAccount)

        PaymentInLine secondLineVoucher = context.newObject(PaymentInLine.class)
        secondLineVoucher.setAmount(secondInvoice.getAmountOwing())
        secondLineVoucher.setPayment(secondPaymentInVoucher)
        secondLineVoucher.setInvoice(secondInvoice)
        secondLineVoucher.setAccountOut(secondInvoice.getDebtorsAccount())

        Voucher secondVoucher = SelectById.query(Voucher.class, 3L).selectOne(context)
        Money balance2 = voucher.getRedemptionValue().subtract(invoice.getAmountOwing())
        voucher.setRedemptionValue(balance2)

        VoucherPaymentIn secondVoucherPaymentIn = context.newObject(VoucherPaymentIn.class)
        secondVoucherPaymentIn.setPaymentIn(secondPaymentInVoucher)
        secondVoucherPaymentIn.setVoucher(secondVoucher)
        secondVoucherPaymentIn.setStatus(VoucherPaymentStatus.APPROVED)
        secondVoucherPaymentIn.setInvoiceLine(secondInvoice.getInvoiceLines().get(0))

        context.commitChanges()

        List<AccountTransaction> secondTransactions = ObjectSelect.query(AccountTransaction.class)
                .where(AccountTransaction.FOREIGN_RECORD_ID.eq(secondLineVoucher.getId()))
                .select(context)
        Assertions.assertEquals(4, secondTransactions.size())

        List<AccountTransaction> asset2Transactions = AccountTransaction.ACCOUNT.eq(assetAccount).filterObjects(secondTransactions)
        Assertions.assertEquals(1, asset2Transactions.size())
        Assertions.assertEquals(new Money("-30.00"), asset2Transactions.get(0).getAmount())

        List<AccountTransaction> liability2Transactions = AccountTransaction.ACCOUNT.eq(liabilityAccount).filterObjects(secondTransactions)
        Assertions.assertEquals(1, liability2Transactions.size())
        Assertions.assertEquals(new Money("-30.00"), liability2Transactions.get(0).getAmount())

        List<AccountTransaction> expense2Transactions = AccountTransaction.ACCOUNT.eq(expenseAccount).filterObjects(secondTransactions)
        Assertions.assertEquals(2, expense2Transactions.size())

        AccountTransaction asset_expense2 = expense2Transactions.get(0).getAmount().isNegative() ? expense2Transactions.get(1) : expense2Transactions.get(0)
        AccountTransaction liability_expense2 = expense2Transactions.get(0).getAmount().isNegative() ? expense2Transactions.get(0) : expense2Transactions.get(1)

        Assertions.assertEquals(new Money("30.00"), asset_expense2.getAmount())
        Assertions.assertEquals(new Money("-30.00"), liability_expense2.getAmount())
    }
}
