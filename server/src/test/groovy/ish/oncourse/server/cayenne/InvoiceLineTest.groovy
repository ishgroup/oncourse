/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.PaymentSource
import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.glue._Account
import ish.oncourse.server.cayenne.glue._AccountTransaction
import ish.persistence.Preferences
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.Ordering
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.cayenne.query.SortOrder
import org.apache.cayenne.validation.ValidationException
import org.apache.commons.lang3.time.DateUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.text.SimpleDateFormat

@CompileStatic
class InvoiceLineTest extends CayenneIshTestCase {
    private static final Logger logger = LogManager.getLogger()
    private ICayenneService cayenneService

    
    @BeforeEach
    void setup() throws Exception {
        wipeTables()

        this.cayenneService = injector.getInstance(ICayenneService.class)

        InputStream st = InvoiceLineTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/cayenne/invoiceLineTest.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
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
    void testInvoiceLineSetup() {

        DataContext newContext = cayenneService.getNewNonReplicatingContext()

        InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
        try {
            invoiceLine.setPriceEachExTax(new Money("100"))
        } catch (IllegalStateException e) {
            Assertions.assertEquals("Checking if the invoice price can be set before tax", "You must set the tax rate before setting the price.", e.getMessage())
            return
        }
        if (PreferenceController.getController().getTaxPK() != null) {
            // in this case the default tax will be applied and exception wont be thrown
            return
        }
        fail("No exception thrown")
        // this actually might not be an issue, just added a test because there are parts of code which rely on the order of setting those fields.

    }

    
    @Test
    void testInvoiceLineSetup2() {
        DataContext newContext = cayenneService.getNewNonReplicatingContext()

        Money amount = new Money("100")

        Tax tax = newContext.newObject(Tax.class)
        tax.setRate(BigDecimal.valueOf(0.1d))

        InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        try {
            invoiceLine.setPriceEachExTax(amount)
            invoiceLine.setTaxEach(amount.multiply(tax.getRate()))
        } catch (IllegalArgumentException e) {
            fail("Exception thrown when setting the invoice line price")
        }

        Assertions.assertEquals(amount.multiply(BigDecimal.ONE.add(tax.getRate())), invoiceLine.getPriceEachIncTax())

        // quantity not set, calculations will assume 0
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPriceTotalExTax())
        Assertions.assertEquals(Money.ZERO, invoiceLine.getPriceTotalIncTax())

        invoiceLine.setQuantity(BigDecimal.valueOf(3d))

        // now the results will be correct
        Assertions.assertEquals(amount.multiply(3), invoiceLine.getPriceTotalExTax())
        Assertions.assertEquals(amount.multiply(3).multiply(BigDecimal.ONE.add(tax.getRate())), invoiceLine.getPriceTotalIncTax())
    }

    /**
     * testing invoice line with no price, discount or prepaid fee
     */
    
    @Test
    void testCreateTransactions0() {
        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)
        DataContext newContext = cayenneService.getNewNonReplicatingContext()
        Tax tax = newContext.select(SelectQuery.query(Tax.class, ExpressionFactory.matchExp(Tax.ID_PROPERTY, 1L))).get(0)
        Contact contact = newContext.select(SelectQuery.query(Contact.class, ExpressionFactory.matchExp(Contact.ID_PROPERTY, 1L))).get(0)
        Account accountIncome = newContext.select(SelectQuery.query(Account.class, ExpressionFactory.matchExp(_Account.ID_PROPERTY, 200L))).get(0)
        Account accountDebtors = newContext.select(SelectQuery.query(Account.class, ExpressionFactory.matchExp(_Account.ID_PROPERTY, 100L))).get(0)
        Account accountPrepaidFees = newContext.select(SelectQuery.query(Account.class, ExpressionFactory.matchExp(_Account.ID_PROPERTY, 500L))).get(0)

        Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setContact(contact)
        invoice.setDebtorsAccount(accountDebtors)

        InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        invoiceLine.setPriceEachExTax(Money.ZERO)
        invoiceLine.setDiscountEachExTax(Money.ZERO)
        invoiceLine.setTaxEach(Money.ZERO)
        invoiceLine.setQuantity(BigDecimal.valueOf(1d))
        invoiceLine.setAccount(accountIncome)
        invoiceLine.setPrepaidFeesAccount(accountPrepaidFees)
        invoiceLine.setInvoice(invoice)
        invoiceLine.setTitle("test")

        invoice.updateAmountOwing()

        newContext.commitChanges()

        Expression e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, tax.getReceivableFromAccount())
        e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
        SelectQuery<AccountTransaction> sq = SelectQuery.query(AccountTransaction.class, e)
        List<AccountTransaction> list = newContext.select(sq)

        Assertions.assertEquals("Checking tax transactions", 0, list.size())

        e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, accountIncome)
        e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
        sq = SelectQuery.query(AccountTransaction.class, e)
        list = newContext.select(sq)

        Assertions.assertEquals("Checking " + accountIncome.getDescription() + " transactions", 0, list.size())

        e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, accountPrepaidFees)
        e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
        sq = SelectQuery.query(AccountTransaction.class, e)
        list = newContext.select(sq)

        Assertions.assertEquals("Checking " + accountPrepaidFees.getDescription() + " transactions", 0, list.size())

        e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, accountDebtors)
        e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
        sq = SelectQuery.query(AccountTransaction.class, e)
        list = newContext.select(sq)

        Assertions.assertEquals("Checking " + accountDebtors.getDescription() + " transactions", 0, list.size())

    }

    /**
     * testing invoice line with no prepaid amount, no discount
     */
    
    @Test
    void testCreateTransactions1() {
        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)
        DataContext initContext = cayenneService.getNewNonReplicatingContext()
        Tax tax = initContext.select(SelectQuery.query(Tax.class, ExpressionFactory.matchExp(Tax.ID_PROPERTY, 1L))).get(0)
        Contact contact = initContext.select(SelectQuery.query(Contact.class, ExpressionFactory.matchExp(Contact.ID_PROPERTY, 1L))).get(0)
        Account accountIncome = initContext.select(SelectQuery.query(Account.class, ExpressionFactory.matchExp(_Account.ID_PROPERTY, 200L))).get(0)
        Account accountDebtors = initContext.select(SelectQuery.query(Account.class, ExpressionFactory.matchExp(_Account.ID_PROPERTY, 100L))).get(0)
        Account accountPrepaidFees = initContext.select(SelectQuery.query(Account.class, ExpressionFactory.matchExp(_Account.ID_PROPERTY, 500L)))
                .get(0)

        List<Money> amounts = new ArrayList<>()
        amounts.add(new Money("-10.00"))
        // amounts.add(Money.ZERO) covered in test above
        amounts.add(new Money("100.00"))

        for (Money amount : amounts) {
            for (int quantity = 1; quantity < 4; quantity++) {
                Money expectedTax = amount.multiply(tax.getRate()).multiply(quantity)
                Money expectedTotal = amount.multiply(quantity)

                DataContext newContext = cayenneService.getNewNonReplicatingContext()
                Invoice invoice = newContext.newObject(Invoice.class)
                invoice.setContact(newContext.localObject(contact))
                invoice.setDebtorsAccount(newContext.localObject(accountDebtors))

                InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
                invoiceLine.setTax(newContext.localObject(tax))
                invoiceLine.setPriceEachExTax(amount)
                invoiceLine.setDiscountEachExTax(Money.ZERO)
                invoiceLine.setTaxEach(amount.multiply(tax.getRate()))
                invoiceLine.setQuantity(BigDecimal.valueOf(quantity))
                invoiceLine.setAccount(newContext.localObject(accountIncome))
                invoiceLine.setPrepaidFeesAccount(newContext.localObject(accountPrepaidFees))
                invoiceLine.setInvoice(invoice)
                invoiceLine.setTitle("test")

                invoice.updateAmountOwing()

                newContext.commitChanges()

                Expression e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, tax.getReceivableFromAccount())
                e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
                SelectQuery<AccountTransaction> sq = SelectQuery.query(AccountTransaction.class, e)
                List<AccountTransaction> list = newContext.select(sq)

                Assertions.assertEquals("Checking tax transactions", 1, list.size())
                Assertions.assertEquals("Checking tax transactions amount", expectedTax, list.get(0).getAmount())

                e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, accountIncome)
                e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
                sq = SelectQuery.query(AccountTransaction.class, e)
                list = newContext.select(sq)

                Assertions.assertEquals("Checking " + accountIncome.getDescription() + " transactions", 1, list.size())
                Assertions.assertEquals("Checking " + accountIncome.getDescription() + " transactions amount", expectedTotal, list.get(0).getAmount())

                e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, accountPrepaidFees)
                e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
                sq = SelectQuery.query(AccountTransaction.class, e)
                list = newContext.select(sq)

                Assertions.assertEquals("Checking " + accountPrepaidFees.getDescription() + " transactions", 0, list.size())

                e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, accountDebtors)
                e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
                sq = SelectQuery.query(AccountTransaction.class, e)
                list = newContext.select(sq)

                Assertions.assertEquals("Checking " + accountDebtors.getDescription() + " transactions", 2, list.size())
                Money amount1 = list.get(0).getAmount()
                Money amount2 = list.get(1).getAmount()
                if (amount1.abs().isGreaterThan(amount2.abs())) {
                    amount2 = list.get(0).getAmount()
                    amount1 = list.get(1).getAmount()
                }
                Assertions.assertEquals("Checking " + accountDebtors.getDescription() + " transactions amount", expectedTax, amount1)
                Assertions.assertEquals("Checking " + accountDebtors.getDescription() + " transactions amount", expectedTotal, amount2)
            }
        }
    }

    /*
     * testing invoice line with prepaid amount
     */

    
    @Test
    void testCreateTransactions3() {
        DataContext newContext = cayenneService.getNewNonReplicatingContext()
        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)
        Money amount = new Money("100")

        Tax tax = newContext.select(SelectQuery.query(Tax.class, ExpressionFactory.matchExp(Tax.ID_PROPERTY, 1L))).get(0)
        Contact contact = newContext.select(SelectQuery.query(Contact.class, ExpressionFactory.matchExp(Contact.ID_PROPERTY, 1L))).get(0)
        Account accountIncome = newContext.select(SelectQuery.query(Account.class, ExpressionFactory.matchExp(_Account.ID_PROPERTY, 200L))).get(0)
        Account accountDebtors = newContext.select(SelectQuery.query(Account.class, ExpressionFactory.matchExp(_Account.ID_PROPERTY, 100L))).get(0)
        Account accountPrepaidFees = newContext.select(SelectQuery.query(Account.class, ExpressionFactory.matchExp(_Account.ID_PROPERTY, 500L)))
                .get(0)

        Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setContact(contact)
        invoice.setDebtorsAccount(accountDebtors)

        InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        invoiceLine.setPriceEachExTax(amount)
        invoiceLine.setDiscountEachExTax(Money.ZERO)
        invoiceLine.setTaxEach(amount.multiply(tax.getRate()))
        invoiceLine.setQuantity(BigDecimal.valueOf(3d))
        invoiceLine.setAccount(accountIncome)
        invoiceLine.setPrepaidFeesAccount(accountPrepaidFees)
        invoiceLine.setInvoice(invoice)
        invoiceLine.setTitle("test")
        invoiceLine.setPrepaidFeesRemaining(new Money("60"))

        invoice.updateAmountOwing()

        try {
            newContext.commitChanges()
        } catch (ValidationException e) {
            Assertions.assertTrue("expecting exception", e.getMessage().contains("The prepaid fees remaining must be zero for invoice line without enrolment."))
            return
        }
        fail("was expecting exception")
    }

    /*
     * testing credit note line with prepaid amount
     */

    
    @Test
    void testCreateTransactions4() {
        DataContext newContext = cayenneService.getNewNonReplicatingContext()
        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)
        Money amount = new Money("100").negate()

        Tax tax = newContext.select(SelectQuery.query(Tax.class, ExpressionFactory.matchExp(Tax.ID_PROPERTY, 1L))).get(0)
        Contact contact = newContext.select(SelectQuery.query(Contact.class, ExpressionFactory.matchExp(Contact.ID_PROPERTY, 1L))).get(0)
        Account accountIncome = newContext.select(SelectQuery.query(Account.class, ExpressionFactory.matchExp(_Account.ID_PROPERTY, 200L))).get(0)
        Account accountDebtors = newContext.select(SelectQuery.query(Account.class, ExpressionFactory.matchExp(_Account.ID_PROPERTY, 100L))).get(0)
        Account accountPrepaidFees = newContext.select(SelectQuery.query(Account.class, ExpressionFactory.matchExp(_Account.ID_PROPERTY, 500L)))
                .get(0)

        Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setContact(contact)
        invoice.setDebtorsAccount(accountDebtors)

        InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        invoiceLine.setPriceEachExTax(amount)
        invoiceLine.setDiscountEachExTax(Money.ZERO)
        invoiceLine.setTaxEach(amount.multiply(tax.getRate()))
        invoiceLine.setQuantity(BigDecimal.valueOf(3d))
        invoiceLine.setAccount(accountIncome)
        invoiceLine.setPrepaidFeesAccount(accountPrepaidFees)
        invoiceLine.setInvoice(invoice)
        invoiceLine.setTitle("test")
        invoiceLine.setPrepaidFeesRemaining(new Money("60").negate())

        invoice.updateAmountOwing()

        try {
            newContext.commitChanges()
        } catch (ValidationException e) {
            Assertions.assertTrue("expecting exception", e.getMessage().contains("The prepaid fees remaining must be zero for invoice line without enrolment."))
            return
        }
        fail("was expecting exception")

    }

    /**
     * testing enrolment + invoice line, no prepaid amout
     */
    
    @Test
    void testCreateTransactions5() {
        DataContext newContext = cayenneService.getNewNonReplicatingContext()

        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)

        Tax tax = newContext.select(SelectQuery.query(Tax.class, Tax.ID.eq(1L))).get(0)
        Student student = newContext.select(SelectQuery.query(Student.class, Student.ID.eq(2L))).get(0)
        CourseClass courseClass = newContext.select(SelectQuery.query(CourseClass.class, CourseClass.ID.eq(1L))).get(0)
        Account accountIncome = newContext.select(SelectQuery.query(Account.class, Account.ID.eq(200L))).get(0)
        Account accountDebtors = newContext.select(SelectQuery.query(Account.class, Account.ID.eq(100L))).get(0)
        Account accountPrepaidFees = newContext.select(SelectQuery.query(Account.class, Account.ID.eq(500L)))
                .get(0)

        Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setContact(student.getContact())
        invoice.setDebtorsAccount(accountDebtors)

        InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        invoiceLine.setPriceEachExTax(courseClass.getFeeExGst())
        invoiceLine.setDiscountEachExTax(Money.ZERO)
        invoiceLine.setTaxEach(invoiceLine.getPriceEachExTax().multiply(tax.getRate()))
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setAccount(accountIncome)
        invoiceLine.setPrepaidFeesAccount(accountPrepaidFees)
        invoiceLine.setInvoice(invoice)
        invoiceLine.setTitle("test")

        Enrolment enrolment = newContext.newObject(Enrolment.class)
        enrolment.setStudent(student)
        enrolment.setCourseClass(courseClass)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrolment.addToInvoiceLines(invoiceLine)

        invoice.updateAmountOwing()

        newContext.commitChanges()

        Expression e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, tax.getReceivableFromAccount())
        e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
        List<AccountTransaction> list = newContext.select(SelectQuery.query(AccountTransaction.class, e))

        Assertions.assertEquals("Checking tax transactions", 1, list.size())
        Assertions.assertEquals("Checking tax transactions amount", new Money("10.00"), list.get(0).getAmount())

        e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, accountIncome)
        e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
        list = newContext.select(SelectQuery.query(AccountTransaction.class, e))

        Assertions.assertEquals("Checking " + accountIncome.getDescription() + " transactions", 1, list.size())
        Assertions.assertEquals("Checking " + accountIncome.getDescription() + " transactions amount", new Money("100.00"), list.get(0).getAmount())

        e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, accountPrepaidFees)
        e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
        list = newContext.select(SelectQuery.query(AccountTransaction.class, e))

        Assertions.assertEquals("Checking " + accountPrepaidFees.getDescription() + " transactions", 0, list.size())

        e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, accountDebtors)
        e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
        list = newContext.select(SelectQuery.query(AccountTransaction.class, e))

        Assertions.assertEquals("Checking " + accountDebtors.getDescription() + " transactions", 2, list.size())
        Money amount1 = list.get(0).getAmount()
        Money amount2 = list.get(1).getAmount()
        if (amount1.abs().isGreaterThan(amount2.abs())) {
            amount2 = list.get(0).getAmount()
            amount1 = list.get(1).getAmount()
        }
        Assertions.assertEquals("Checking " + accountDebtors.getDescription() + " transactions amount", new Money("10.00"), amount1)
        Assertions.assertEquals("Checking " + accountDebtors.getDescription() + " transactions amount", new Money("100.00"), amount2)

    }

    /**
     * testing invoice line + enrolment with prepaid amout
     */
    
    @Test
    void testCreateTransactions6() {
        DataContext newContext = cayenneService.getNewNonReplicatingContext()

        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)

        Tax tax = newContext.select(SelectQuery.query(Tax.class, Tax.ID.eq(1L))).get(0)
        Student student = newContext.select(SelectQuery.query(Student.class, Student.ID.eq(2L))).get(0)
        CourseClass courseClass = newContext.select(SelectQuery.query(CourseClass.class, CourseClass.ID.eq(1L))).get(0)
        Account accountIncome = newContext.select(SelectQuery.query(Account.class, Account.ID.eq(200L))).get(0)
        Account accountDebtors = newContext.select(SelectQuery.query(Account.class, Account.ID.eq(100L))).get(0)
        Account accountPrepaidFees = newContext.select(SelectQuery.query(Account.class, Account.ID.eq(500L))).get(0)

        Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setContact(student.getContact())
        invoice.setDebtorsAccount(accountDebtors)

        InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        invoiceLine.setPriceEachExTax(courseClass.getFeeExGst())
        invoiceLine.setDiscountEachExTax(Money.ZERO)
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setTaxEach(invoiceLine.getPriceEachExTax().multiply(tax.getRate()))
        invoiceLine.setAccount(accountIncome)
        invoiceLine.setPrepaidFeesAccount(accountPrepaidFees)
        invoiceLine.setInvoice(invoice)
        invoiceLine.setTitle("test")
        invoiceLine.setPrepaidFeesRemaining(new Money("60"))

        Enrolment enrolment = newContext.newObject(Enrolment.class)
        enrolment.setStudent(student)
        enrolment.setCourseClass(courseClass)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrolment.addToInvoiceLines(invoiceLine)

        invoice.updateAmountOwing()

        newContext.commitChanges()

        Expression e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, tax.getReceivableFromAccount())
        e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
        List<AccountTransaction> list = newContext.select(SelectQuery.query(AccountTransaction.class, e))

        Assertions.assertEquals("Checking tax transactions", 1, list.size())
        Assertions.assertEquals("Checking tax transactions amount", new Money("10.00"), list.get(0).getAmount())

        e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, accountIncome)
        e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
        list = newContext.select(SelectQuery.query(AccountTransaction.class, e))

        Assertions.assertEquals("Checking " + accountIncome.getDescription() + " transactions", 1, list.size())
        Assertions.assertEquals("Checking " + accountIncome.getDescription(), new Money("40"), list.get(0).getAmount())

        e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, accountPrepaidFees)
        e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
        list = newContext.select(SelectQuery.query(AccountTransaction.class, e))

        Assertions.assertEquals("Checking " + accountPrepaidFees.getDescription() + " transactions", 1, list.size())
        Assertions.assertEquals("Checking " + accountPrepaidFees.getDescription(), new Money("60"), list.get(0).getAmount())

        e = ExpressionFactory.matchExp(AccountTransaction.ACCOUNT_PROPERTY, accountDebtors)
        e = e.andExp(ExpressionFactory.matchExp(_AccountTransaction.FOREIGN_RECORD_ID_PROPERTY, invoiceLine.getId()))
        SelectQuery<AccountTransaction> sq = SelectQuery.query(AccountTransaction.class, e)
        sq.addOrdering(new Ordering(_AccountTransaction.AMOUNT_PROPERTY, SortOrder.ASCENDING))
        list = newContext.select(sq)

        Assertions.assertEquals("Checking " + accountDebtors.getDescription() + " transactions", 3, list.size())
        Assertions.assertEquals("Checking " + accountDebtors.getDescription(), new Money("10"), list.get(0).getAmount())
        Assertions.assertEquals("Checking " + accountDebtors.getDescription(), new Money("40"), list.get(1).getAmount())
        Assertions.assertEquals("Checking " + accountDebtors.getDescription(), new Money("60"), list.get(2).getAmount())

    }

    /**
     * testing invoice line + enrolment with full prepaid amout
     */
    
    @Test
    void testCreateTransactions7() {
        DataContext newContext = cayenneService.getNewNonReplicatingContext()

        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)

        Tax tax = newContext.select(SelectQuery.query(Tax.class, Tax.ID.eq(1L))).get(0)
        Student student = newContext.select(SelectQuery.query(Student.class, Student.ID.eq(2L))).get(0)
        CourseClass courseClass = newContext.select(SelectQuery.query(CourseClass.class, CourseClass.ID.eq(1L))).get(0)
        Account accountIncome = newContext.select(SelectQuery.query(Account.class, Account.ID.eq(200L))).get(0)
        Account accountDebtors = newContext.select(SelectQuery.query(Account.class, Account.ID.eq(100L))).get(0)
        Account accountPrepaidFees = newContext.select(SelectQuery.query(Account.class, Account.ID.eq(500L))).get(0)

        Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setContact(student.getContact())
        invoice.setDebtorsAccount(accountDebtors)

        InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        invoiceLine.setPriceEachExTax(courseClass.getFeeExGst())
        invoiceLine.setDiscountEachExTax(Money.ZERO)
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setTaxEach(invoiceLine.getPriceEachExTax().multiply(tax.getRate()))
        invoiceLine.setAccount(accountIncome)
        invoiceLine.setPrepaidFeesAccount(accountPrepaidFees)
        invoiceLine.setInvoice(invoice)
        invoiceLine.setTitle("test")
        invoiceLine.setPrepaidFeesRemaining(courseClass.getFeeExGst())

        Enrolment enrolment = newContext.newObject(Enrolment.class)
        enrolment.setStudent(student)
        enrolment.setCourseClass(courseClass)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrolment.addToInvoiceLines(invoiceLine)

        invoice.updateAmountOwing()

        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

        newContext.commitChanges()

        Long invoiceLineId = invoiceLine.getId()
        Long invoiceId = invoice.getId()

        DataContext newContext2 = cayenneService.getNewNonReplicatingContext()
        invoiceLine = getRecordWithId(newContext2, InvoiceLine.class, invoiceLineId)
        invoice = getRecordWithId(newContext2, Invoice.class, invoiceId)

        Expression e = AccountTransaction.ACCOUNT.eq(tax.getReceivableFromAccount())
        e = e.andExp(AccountTransaction.FOREIGN_RECORD_ID.eq(invoiceLine.getId()))
        List<AccountTransaction> list = newContext.select(SelectQuery.query(AccountTransaction.class, e))

        Assertions.assertEquals("Checking tax transactions", 1, list.size())
        Assertions.assertEquals("Checking tax transactions amount", new Money("10.00"), list.get(0).getAmount())

        Assertions.assertEquals("Checking transactions dates ", invoice.getInvoiceDate(), list.get(0).getTransactionDate())

        e = AccountTransaction.ACCOUNT.eq(accountIncome)
        e = e.andExp(AccountTransaction.FOREIGN_RECORD_ID.eq(invoiceLine.getId()))
        list = newContext.select(SelectQuery.query(AccountTransaction.class, e))

        Assertions.assertEquals("Checking " + accountIncome.getDescription() + " transactions", 0, list.size())

        e = AccountTransaction.ACCOUNT.eq(accountPrepaidFees)
        e = e.andExp(AccountTransaction.FOREIGN_RECORD_ID.eq(invoiceLine.getId()))
        list = newContext.select(SelectQuery.query(AccountTransaction.class, e))

        Assertions.assertEquals("Checking " + accountPrepaidFees.getDescription() + " transactions", 1, list.size())
        Assertions.assertEquals("Checking " + accountPrepaidFees.getDescription(), new Money("100"), list.get(0).getAmount())

        e = AccountTransaction.ACCOUNT.eq(accountDebtors)
        e = e.andExp(AccountTransaction.FOREIGN_RECORD_ID.eq(invoiceLine.getId()))
        SelectQuery<AccountTransaction> sq = SelectQuery.query(AccountTransaction.class, e)
        sq.addOrdering(AccountTransaction.AMOUNT.asc())
        list = newContext.select(sq)

        Assertions.assertEquals("Checking " + accountDebtors.getDescription() + " transactions", 2, list.size())
        Assertions.assertEquals("Checking " + accountDebtors.getDescription(), new Money("10"), list.get(0).getAmount())
        Assertions.assertEquals("Checking " + accountDebtors.getDescription(), new Money("100"), list.get(1).getAmount())
    }

    
    @Test
    void testCreateTransactionsVoucherPurchase() throws Exception {
        ObjectContext newContext = cayenneService.getNewNonReplicatingContext()

        PreferenceController.getController().setAccountPrepaidFeesPostAt(Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION)

        Contact contact = SelectById.query(Contact.class, 2).selectOne(newContext)
        Tax tax = SelectById.query(Tax.class, 2).selectOne(newContext)
        Account incomeAccount = SelectById.query(Account.class, 200).selectOne(newContext)
        Account debtorsAccount = SelectById.query(Account.class, 100).selectOne(newContext)
        Account liabilityAccount = SelectById.query(Account.class, 700).selectOne(newContext)
        VoucherProduct voucherType = SelectById.query(VoucherProduct.class, 1).selectOne(newContext)

        Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setContact(contact)
        invoice.setDebtorsAccount(debtorsAccount)

        InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        invoiceLine.setPriceEachExTax(voucherType.getPriceExTax())
        invoiceLine.setDiscountEachExTax(Money.ZERO)
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setTaxEach(voucherType.getFeeGST())
        invoiceLine.setAccount(voucherType.getLiabilityAccount())
        invoiceLine.setInvoice(invoice)
        invoiceLine.setTitle("test")
        invoiceLine.setPrepaidFeesRemaining(Money.ZERO)

        Voucher voucher = newContext.newObject(Voucher.class)
        voucher.setProduct(voucherType)
        voucher.setRedeemableBy(contact)
        voucher.setStatus(ProductStatus.NEW)
        voucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        voucher.setCode("TEST")
        voucher.setExpiryDate(new SimpleDateFormat("yyyy/MM/dd").parse("2113/01/01"))

        voucher.setInvoiceLine(invoiceLine)

        invoice.updateAmountOwing()

        newContext.commitChanges()

        Long invoiceLineId = invoiceLine.getId()

        DataContext newContext2 = cayenneService.getNewNonReplicatingContext()
        invoiceLine = getRecordWithId(newContext2, InvoiceLine.class, invoiceLineId)

        Expression e = AccountTransaction.ACCOUNT.eq(incomeAccount)
        e = e.andExp(AccountTransaction.FOREIGN_RECORD_ID.eq(invoiceLine.getId()))
        List<AccountTransaction> list = newContext.select(SelectQuery.query(AccountTransaction.class, e))

        Assertions.assertEquals("Checking " + incomeAccount.getDescription() + " transactions", 0, list.size())

        e = AccountTransaction.ACCOUNT.eq(liabilityAccount)
        e = e.andExp(AccountTransaction.FOREIGN_RECORD_ID.eq(invoiceLine.getId()))
        list = newContext.select(SelectQuery.query(AccountTransaction.class, e))

        Assertions.assertEquals("Checking " + liabilityAccount.getDescription() + " transactions", 1, list.size())
        Assertions.assertEquals("Checking " + liabilityAccount.getDescription(), new Money("100"), list.get(0).getAmount())

        e = AccountTransaction.ACCOUNT.eq(debtorsAccount)
        e = e.andExp(AccountTransaction.FOREIGN_RECORD_ID.eq(invoiceLine.getId()))
        SelectQuery<AccountTransaction> sq = SelectQuery.query(AccountTransaction.class, e)
        sq.addOrdering(AccountTransaction.AMOUNT.asc())
        list = newContext.select(sq)

        Assertions.assertEquals("Checking " + debtorsAccount.getDescription() + " transactions", 1, list.size())
        Assertions.assertEquals("Checking " + debtorsAccount.getDescription(), new Money("100"), list.get(0).getAmount())
    }
}
