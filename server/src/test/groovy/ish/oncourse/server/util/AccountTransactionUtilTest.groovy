/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.util

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.common.types.PaymentSource
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.cayenne.*
import ish.util.PaymentMethodUtil
import org.apache.cayenne.query.QueryCacheStrategy
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.time.LocalDate

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/cayenne/ishDataContextTestDataSet.xml")
class AccountTransactionUtilTest extends TestWithDatabase {

    @Override
    protected void dataSourceReplaceValues(ReplacementDataSet rDataSet) {
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)

        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))
    }

    /**
     * as tests include here rely on the number of transactions created during some processes, we must make sure that there are no transactions in the database.
     */
    @AfterEach
    void deleteTransactions() {
        List<AccountTransaction> list = cayenneContext.select(SelectQuery.query(AccountTransaction.class))
        cayenneContext.deleteObjects(list)
        cayenneContext.commitChanges()
    }

    
    @Test
    void testInvoiceTransactions() {
        Account account = SelectById.query(Account.class, 50).selectOne(cayenneContext)
        Contact payer = SelectById.query(Contact.class, 1).selectOne(cayenneContext)
        Tax tax = SelectById.query(Tax.class, 1).selectOne(cayenneContext)

        Invoice invoice = cayenneContext.newObject(Invoice.class)
        invoice.setInvoiceDate(LocalDate.now())
        invoice.setInvoiceNumber(1L)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setContact(payer)
        invoice.setDebtorsAccount(account)
        invoice.setAmountOwing(new Money("100.00"))

        InvoiceLine line = cayenneContext.newObject(InvoiceLine.class)
        line.setAccount(account)
        line.setTax(tax)
        line.setDiscountEachExTax(Money.ZERO)
        line.setInvoice(invoice)
        line.setPrepaidFeesAccount(account)
        line.setPrepaidFeesRemaining(Money.ZERO)
        line.setPriceEachExTax(new Money("100.00"))
        line.setTaxEach(new Money("10"))
        line.setQuantity(BigDecimal.ONE)
        line.setTitle("test line")

        SelectQuery<AccountTransaction> q = SelectQuery.query(AccountTransaction.class)
        q.setCacheStrategy(QueryCacheStrategy.NO_CACHE)

        List<AccountTransaction> transactions = cayenneContext.select(q)
        Assertions.assertEquals(0, transactions.size())

        cayenneContext.commitChanges()

        transactions = cayenneContext.select(q)
        Assertions.assertEquals(4, transactions.size())

        invoice.setModifiedOn(new Date())

        cayenneContext.commitChanges()

        List<AccountTransaction> newTransactions = cayenneContext.select(q)
        Assertions.assertEquals(4, newTransactions.size())

        // check if they are same transaction records
        Assertions.assertTrue(newTransactions.containsAll(transactions))

        for (AccountTransaction transaction : transactions) {
            Assertions.assertNotNull(AccountTransaction.getInvoiceLineForTransaction(cayenneContext, transaction))
        }
    }
    
    @Test
    void testPaymentTransactions() {
        Account accountIn = SelectById.query(Account.class, 60).selectOne(cayenneContext)
        Contact payer = SelectById.query(Contact.class, 1).selectOne(cayenneContext)
        Invoice invoice1 = SelectById.query(Invoice.class, 1).selectOne(cayenneContext)
        Invoice invoice2 = SelectById.query(Invoice.class, 2).selectOne(cayenneContext)

        PaymentIn payment = cayenneContext.newObject(PaymentIn.class)
        payment.setPaymentDate(LocalDate.now())
        payment.setAccountIn(accountIn)
        payment.setAmount(new Money(new BigDecimal(50)))
        payment.setPayer(payer)
        payment.setReconciled(false)
        payment.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.EFT), payment).set()
        payment.setStatus(PaymentStatus.IN_TRANSACTION)

        PaymentInLine pil1 = cayenneContext.newObject(PaymentInLine.class)
        pil1.setInvoice(invoice1)
        pil1.setAccountOut(accountIn)
        pil1.setAmount(invoice1.getAmountOwing())
        pil1.setPaymentIn(payment)

        PaymentInLine pil2 = cayenneContext.newObject(PaymentInLine.class)
        pil2.setInvoice(invoice2)
        pil2.setAccountOut(accountIn)
        pil2.setAmount(invoice2.getAmountOwing())
        pil2.setPaymentIn(payment)

        cayenneContext.commitChanges()

        SelectQuery<AccountTransaction> q = SelectQuery.query(AccountTransaction.class)
        q.setCacheStrategy(QueryCacheStrategy.NO_CACHE)

        List<AccountTransaction> transactions = cayenneContext.select(q)
        Assertions.assertEquals(0, transactions.size())

        payment.succeed()

        cayenneContext.commitChanges()

        transactions = cayenneContext.select(q)
        Assertions.assertEquals(4, transactions.size())

        // touch records to trigger callbacks on them
        payment.setModifiedOn(new Date())
        pil1.setModifiedOn(new Date())
        pil2.setModifiedOn(new Date())

        cayenneContext.commitChanges()

        List<AccountTransaction> newTransactions = cayenneContext.select(q)
        Assertions.assertEquals(4, newTransactions.size())

        // check if they are same transaction records
        Assertions.assertTrue(newTransactions.containsAll(transactions))

        for (AccountTransaction transaction : transactions) {
            Assertions.assertNotNull(AccountTransaction.getPaymentInLineForTransaction(cayenneContext, transaction))
        }
    }

    
    @Test
    void testPaymentOutTransactions() {
        Account accountOut = SelectById.query(Account.class, 60).selectOne(cayenneContext)
        Contact payee = SelectById.query(Contact.class, 1).selectOne(cayenneContext)
        Invoice invoice1 = SelectById.query(Invoice.class, 3).selectOne(cayenneContext)

        PaymentOut payment = cayenneContext.newObject(PaymentOut.class)
        payment.setPaymentDate(LocalDate.now())
        payment.setAccountOut(accountOut)
        payment.setAmount(new Money("-30.00"))
        payment.setPayee(payee)
        payment.setReconciled(false)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.EFT), payment).set()
        payment.setStatus(PaymentStatus.SUCCESS)

        PaymentOutLine pil1 = cayenneContext.newObject(PaymentOutLine.class)
        pil1.setInvoice(invoice1)
        pil1.setAccountIn(accountOut)
        pil1.setAmount(invoice1.getAmountOwing())
        pil1.setPaymentOut(payment)

        cayenneContext.commitChanges()

        SelectQuery<AccountTransaction> q = SelectQuery.query(AccountTransaction.class)
        q.setCacheStrategy(QueryCacheStrategy.NO_CACHE)

        List<AccountTransaction> transactions = cayenneContext.select(q)
        Assertions.assertEquals(2, transactions.size())

        // touch records to trigger callbacks on them
        payment.setModifiedOn(new Date())
        pil1.setModifiedOn(new Date())

        cayenneContext.commitChanges()

        List<AccountTransaction> newTransactions = cayenneContext.select(q)
        Assertions.assertEquals(2, newTransactions.size())

        // check if they are same transaction records
        Assertions.assertTrue(newTransactions.containsAll(transactions))

        for (AccountTransaction transaction : transactions) {
            Assertions.assertNotNull(AccountTransaction.getPaymentOutLineForTransaction(cayenneContext, transaction))
        }
    }

    
    @Test
    void testVoucherPurchaseTransactions() {
        Account account = SelectById.query(Account.class, 50).selectOne(cayenneContext)
        Contact payer = SelectById.query(Contact.class, 1).selectOne(cayenneContext)
        Tax tax = SelectById.query(Tax.class, 1).selectOne(cayenneContext)

        Invoice invoice = cayenneContext.newObject(Invoice.class)
        invoice.setInvoiceDate(LocalDate.now())
        invoice.setInvoiceNumber(1L)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setContact(payer)
        invoice.setDebtorsAccount(account)
        invoice.setAmountOwing(new Money("100.00"))

        InvoiceLine line = cayenneContext.newObject(InvoiceLine.class)
        line.setAccount(account)
        line.setTax(tax)
        line.setDiscountEachExTax(Money.ZERO)
        line.setInvoice(invoice)
        line.setPrepaidFeesAccount(account)
        line.setPrepaidFeesRemaining(Money.ZERO)
        line.setPriceEachExTax(new Money("100.00"))
        line.setTaxEach(new Money("10"))
        line.setQuantity(BigDecimal.ONE)
        line.setTitle("test line")

        SelectQuery<AccountTransaction> q = SelectQuery.query(AccountTransaction.class)
        q.setCacheStrategy(QueryCacheStrategy.NO_CACHE)

        List<AccountTransaction> transactions = cayenneContext.select(q)
        Assertions.assertEquals(0, transactions.size())

        cayenneContext.commitChanges()

        transactions = cayenneContext.select(q)
        Assertions.assertEquals(4, transactions.size())

        invoice.setModifiedOn(new Date())

        cayenneContext.commitChanges()

        List<AccountTransaction> newTransactions = cayenneContext.select(q)
        Assertions.assertEquals(4, newTransactions.size())

        // check if they are same transaction records
        Assertions.assertTrue(newTransactions.containsAll(transactions))

        for (AccountTransaction transaction : transactions) {
            Assertions.assertNotNull(AccountTransaction.getInvoiceLineForTransaction(cayenneContext, transaction))
        }
    }
}