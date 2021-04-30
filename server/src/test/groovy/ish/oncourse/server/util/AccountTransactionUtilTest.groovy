/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.util

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.PaymentSource
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.*
import ish.util.PaymentMethodUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.QueryCacheStrategy
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.After
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate

import static org.junit.Assert.*

/**
 */
@CompileStatic
class AccountTransactionUtilTest extends CayenneIshTestCase {

	@BeforeEach
    void setupTest() throws Exception {
		wipeTables()

        InputStream st = AccountTransactionUtilTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/cayenne/ishDataContextTestDataSet.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)

        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))

        executeDatabaseOperation(rDataSet)
	}

	/**
	 * as tests include here rely on the number of transactions created during some processes, we must make sure that there are no transactions in the database.
	 */
	@After
    void deleteTransactions() {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        ObjectContext context = cayenneService.getNewNonReplicatingContext()
        List<AccountTransaction> list = context.select(SelectQuery.query(AccountTransaction.class))
        context.deleteObjects(list)
        context.commitChanges()
    }

	@Test
    void testInvoiceTransactions() {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        ObjectContext context = cayenneService.getNewNonReplicatingContext()

        Account account = SelectById.query(Account.class, 50).selectOne(context)
        Contact payer = SelectById.query(Contact.class, 1).selectOne(context)
        Tax tax = SelectById.query(Tax.class, 1).selectOne(context)

        Invoice invoice = context.newObject(Invoice.class)
        invoice.setInvoiceDate(LocalDate.now())
        invoice.setInvoiceNumber(1L)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setContact(payer)
        invoice.setDebtorsAccount(account)
        invoice.setAmountOwing(new Money("100.00"))

        InvoiceLine line = context.newObject(InvoiceLine.class)
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

        List<AccountTransaction> transactions = context.select(q)
        assertEquals(0, transactions.size())

        context.commitChanges()

        transactions = context.select(q)
        assertEquals(4, transactions.size())

        invoice.setModifiedOn(new Date())

        context.commitChanges()

        List<AccountTransaction> newTransactions = context.select(q)
        assertEquals(4, newTransactions.size())

        // check if they are same transaction records
		assertTrue(newTransactions.containsAll(transactions))

        for (AccountTransaction transaction : transactions) {
			assertNotNull(AccountTransaction.getInvoiceLineForTransaction(context, transaction))
        }
	}

	@Test
    void testPaymentTransactions() {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        ObjectContext context = cayenneService.getNewNonReplicatingContext()

        Account accountIn = SelectById.query(Account.class, 60).selectOne(context)
        Contact payer = SelectById.query(Contact.class, 1).selectOne(context)
        Invoice invoice1 = SelectById.query(Invoice.class, 1).selectOne(context)
        Invoice invoice2 = SelectById.query(Invoice.class, 2).selectOne(context)

        PaymentIn payment = context.newObject(PaymentIn.class)
        payment.setPaymentDate(LocalDate.now())
        payment.setAccountIn(accountIn)
        payment.setAmount(new Money(new BigDecimal(50)))
        payment.setPayer(payer)
        payment.setReconciled(false)
        payment.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(context, PaymentMethod.class, PaymentType.EFT), payment).set()
        payment.setStatus(PaymentStatus.IN_TRANSACTION)

        PaymentInLine pil1 = context.newObject(PaymentInLine.class)
        pil1.setInvoice(invoice1)
        pil1.setAccountOut(accountIn)
        pil1.setAmount(invoice1.getAmountOwing())
        pil1.setPaymentIn(payment)

        PaymentInLine pil2 = context.newObject(PaymentInLine.class)
        pil2.setInvoice(invoice2)
        pil2.setAccountOut(accountIn)
        pil2.setAmount(invoice2.getAmountOwing())
        pil2.setPaymentIn(payment)

        context.commitChanges()

        SelectQuery<AccountTransaction> q = SelectQuery.query(AccountTransaction.class)
        q.setCacheStrategy(QueryCacheStrategy.NO_CACHE)

        List<AccountTransaction> transactions = context.select(q)
        assertEquals(0, transactions.size())

        payment.succeed()

        context.commitChanges()

        transactions = context.select(q)
        assertEquals(4, transactions.size())

        // touch records to trigger callbacks on them
		payment.setModifiedOn(new Date())
        pil1.setModifiedOn(new Date())
        pil2.setModifiedOn(new Date())

        context.commitChanges()

        List<AccountTransaction> newTransactions = context.select(q)
        assertEquals(4, newTransactions.size())

        // check if they are same transaction records
		assertTrue(newTransactions.containsAll(transactions))

        for (AccountTransaction transaction : transactions) {
			assertNotNull(AccountTransaction.getPaymentInLineForTransaction(context, transaction))
        }
	}

	@Test
    void testPaymentOutTransactions() {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        ObjectContext context = cayenneService.getNewNonReplicatingContext()

        Account accountOut = SelectById.query(Account.class, 60).selectOne(context)
        Contact payee = SelectById.query(Contact.class, 1).selectOne(context)
        Invoice invoice1 = SelectById.query(Invoice.class, 3).selectOne(context)

        PaymentOut payment = context.newObject(PaymentOut.class)
        payment.setPaymentDate(LocalDate.now())
        payment.setAccountOut(accountOut)
        payment.setAmount(new Money("-30.00"))
        payment.setPayee(payee)
        payment.setReconciled(false)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(context, PaymentMethod.class, PaymentType.EFT), payment).set()
        payment.setStatus(PaymentStatus.SUCCESS)

        PaymentOutLine pil1 = context.newObject(PaymentOutLine.class)
        pil1.setInvoice(invoice1)
        pil1.setAccountIn(accountOut)
        pil1.setAmount(invoice1.getAmountOwing())
        pil1.setPaymentOut(payment)

        context.commitChanges()

        SelectQuery<AccountTransaction> q = SelectQuery.query(AccountTransaction.class)
        q.setCacheStrategy(QueryCacheStrategy.NO_CACHE)

        List<AccountTransaction> transactions = context.select(q)
        assertEquals(2, transactions.size())

        // touch records to trigger callbacks on them
		payment.setModifiedOn(new Date())
        pil1.setModifiedOn(new Date())

        context.commitChanges()

        List<AccountTransaction> newTransactions = context.select(q)
        assertEquals(2, newTransactions.size())

        // check if they are same transaction records
		assertTrue(newTransactions.containsAll(transactions))

        for (AccountTransaction transaction : transactions) {
			assertNotNull(AccountTransaction.getPaymentOutLineForTransaction(context, transaction))
        }
	}
	
	@Test
    void testVoucherPurchaseTransactions() {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        ObjectContext context = cayenneService.getNewNonReplicatingContext()

        Account account = SelectById.query(Account.class, 50).selectOne(context)
        Contact payer = SelectById.query(Contact.class, 1).selectOne(context)
        Tax tax = SelectById.query(Tax.class, 1).selectOne(context)

        Invoice invoice = context.newObject(Invoice.class)
        invoice.setInvoiceDate(LocalDate.now())
        invoice.setInvoiceNumber(1L)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setContact(payer)
        invoice.setDebtorsAccount(account)
        invoice.setAmountOwing(new Money("100.00"))

        InvoiceLine line = context.newObject(InvoiceLine.class)
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

        List<AccountTransaction> transactions = context.select(q)
        assertEquals(0, transactions.size())

        context.commitChanges()

        transactions = context.select(q)
        assertEquals(4, transactions.size())

        invoice.setModifiedOn(new Date())

        context.commitChanges()

        List<AccountTransaction> newTransactions = context.select(q)
        assertEquals(4, newTransactions.size())

        // check if they are same transaction records
		assertTrue(newTransactions.containsAll(transactions))

        for (AccountTransaction transaction : transactions) {
			assertNotNull(AccountTransaction.getInvoiceLineForTransaction(context, transaction))
        }
	}
}