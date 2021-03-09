/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util

import ish.CayenneIshTestCase
import ish.common.payable.PayableLineInterface
import ish.common.types.AccountTransactionType
import ish.common.types.AccountType
import ish.common.types.CourseClassAttendanceType
import ish.common.types.DeliveryMode
import ish.common.types.EnrolmentStatus
import ish.common.types.PaymentSource
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.cayenne.PaymentInterface
import ish.oncourse.cayenne.PaymentLineInterface
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.generator.DataGenerator
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.PaymentMethod
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.PaymentOutLine
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.Tax
import ish.oncourse.server.cayenne.glue._AccountTransaction
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.SelectQuery
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue
import static org.junit.Assert.fail
import org.junit.Test

import java.time.LocalDate

/**
 */
class InvoiceUtilTest extends CayenneIshTestCase {

	private static final Logger logger = LogManager.getLogger()

    private static int codeSequence = 0

    @Test
    void testNewPaymentLineForInvoiceAndPayment() {
		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Account account = newContext.newObject(Account.class)
        account.setType(AccountType.ASSET)
        account.setAccountCode("11100")

        Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(account)
        PaymentIn pIn = newContext.newObject(PaymentIn.class)
        PaymentOut pOut = newContext.newObject(PaymentOut.class)

        PaymentInLine pInLine = (PaymentInLine) InvoiceUtil.paymentLineForInvoiceAndPayment(pIn, invoice)
        PaymentOutLine pOutLine = (PaymentOutLine) InvoiceUtil.paymentLineForInvoiceAndPayment(pOut, invoice)

        assertEquals("Checking the payment line type", PaymentInLine.class, pInLine.getClass())
        assertEquals("Checking the payment in", pIn, pInLine.getPaymentIn())
        assertEquals("Checking the invoice", invoice, pInLine.getInvoice())
        assertEquals("Checking the account", account, pInLine.getAccountOut())

        assertEquals("Checking the payment line type", PaymentOutLine.class, pOutLine.getClass())
        assertEquals("Checking the payment out", pOut, pOutLine.getPaymentOut())
        assertEquals("Checking the invoice", invoice, pOutLine.getInvoice())
        assertEquals("Checking the account", account, pOutLine.getAccountIn())

    }

	@Test
    void testGetSuccessfulPaymentLines() {
		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
        Invoice invoice = newContext.newObject(Invoice.class)
        List<? extends PaymentLineInterface> result = InvoiceUtil.getSuccessfulPaymentLines(invoice)
        assertTrue("Empty list should be returned for invoice without linked payments", result.isEmpty())

        PaymentInLine pInLine = newContext.newObject(PaymentInLine.class)
        pInLine.setInvoice(invoice)
        result = InvoiceUtil.getSuccessfulPaymentLines(invoice)
        assertTrue("Empty list should be returned for invoice without linked payments to paymentinline", result.isEmpty())

        PaymentIn pIn = newContext.newObject(PaymentIn.class)
        pInLine.setPaymentIn(pIn)

        result = InvoiceUtil.getSuccessfulPaymentLines(invoice)
        assertTrue("Empty list should be returned for invoice without linked payments to paymentinline", result.isEmpty())

        pIn.setStatus(PaymentStatus.SUCCESS)
        result = InvoiceUtil.getSuccessfulPaymentLines(invoice)
        assertFalse(result.isEmpty())
        assertEquals("1 paymentInLine should returns", 1, result.size())
        assertNotNull("this paymentInLine should be linked with payment", result.get(0).getPayment())
        assertTrue("this paymentInLine should be linked with success payment", result.get(0).getPayment().isSuccess())
    }

	@Test
    void testExistingPaymentLineForInvoiceAndPayment() {
		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Account account = newContext.newObject(Account.class)
        account.setType(AccountType.ASSET)
        account.setAccountCode("11100")

        Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(account)
        Invoice invoice2 = newContext.newObject(Invoice.class)
        invoice2.setDebtorsAccount(account)

        PaymentIn pIn = newContext.newObject(PaymentIn.class)
        PaymentInLine pInLine = newContext.newObject(PaymentInLine.class)
        pInLine.setInvoice(invoice)
        pInLine.setPaymentIn(pIn)

        assertEquals("Checking the uniqueness of payment in line", pInLine, InvoiceUtil.paymentLineForInvoiceAndPayment(pIn, invoice))
        assertNotEquals(pInLine, InvoiceUtil.paymentLineForInvoiceAndPayment(pIn, invoice2))
        assertNotEquals(pInLine, InvoiceUtil.paymentLineForInvoiceAndPayment(newContext.newObject(PaymentIn.class), invoice))

        PaymentOut pOut = newContext.newObject(PaymentOut.class)
        PaymentOutLine pOutLine = newContext.newObject(PaymentOutLine.class)
        pOutLine.setInvoice(invoice)
        pOutLine.setPaymentOut(pOut)

        assertEquals("Checking the uniqueness of payment out line", pOutLine, InvoiceUtil.paymentLineForInvoiceAndPayment(pOut, invoice))
        assertNotEquals(pInLine, InvoiceUtil.paymentLineForInvoiceAndPayment(pOut, invoice2))
        assertNotEquals(pInLine, InvoiceUtil.paymentLineForInvoiceAndPayment(newContext.newObject(PaymentOut.class), invoice))

    }

	@Test
    void testSuccessfullPaymentLines() {

		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        // commented out parts are for future evaluation, in case the behaviour after commit will change
		// Account aAsset = newContext.newObject(Account.class);
		// aAsset.setType(AccountType.ASSET);

		Account aDebtors = newContext.newObject(Account.class)
        aDebtors.setType(AccountType.ASSET)
        aDebtors.setAccountCode("11100")

        Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(aDebtors)

        // int i = 0;
		for (PaymentStatus ps : PaymentStatus.values()) {
			PaymentInLine pInLine = (PaymentInLine) InvoiceUtil.paymentLineForInvoiceAndPayment(newContext.newObject(PaymentIn.class), invoice)
            // pInLine.getPaymentIn().setAccountIn(aAsset);
			// pInLine.getPaymentIn().setAmount(Money.valueOf(new BigDecimal("" + Math.pow(2, i))));
			pInLine.getPaymentIn().setStatus(ps)
            // i++;
		}

		// there is only one successful state
		assertEquals("Checking the number of successfull payment lines", 1, InvoiceUtil.getSuccessfulPaymentLines(invoice).size())

        // newContext.commitChanges();
		// assertEquals("Checking the number of successfull payment lines", 3, InvoiceUtil.getSuccessfulPaymentLines(invoice));
	}

	@Test
    void testSumPaymentLines() {

		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        ArrayList<PaymentLineInterface> list = new ArrayList<>()
        for (int i = 0; i < 8; i++) {
			PaymentIn pIn = newContext.newObject(PaymentIn.class)
            PaymentInLine pInLine = newContext.newObject(PaymentInLine.class)
            pInLine.setPaymentIn(pIn)
            // payments are powers of 2, every second is success
			pInLine.setAmount(Money.valueOf(new BigDecimal("" + Math.pow(2, i))))
            pInLine.getPaymentIn().setStatus(i % 2 == 0 ? PaymentStatus.FAILED : PaymentStatus.SUCCESS)
            list.add(pInLine)

            PaymentOut pOut = newContext.newObject(PaymentOut.class)
            PaymentOutLine pOutLine = newContext.newObject(PaymentOutLine.class)
            pOutLine.setPaymentOut(pOut)
            // payments are powers of 2, every second is success
			pOutLine.setAmount(Money.valueOf(new BigDecimal("" + Math.pow(2, i))))
            pOutLine.getPaymentOut().setStatus(i % 2 != 0 ? PaymentStatus.FAILED : PaymentStatus.SUCCESS)

            list.add(pOutLine)
        }

		Money sum1 = new Money("" + Integer.parseInt("10101010", 2))
        Money sum2 = new Money("" + Integer.parseInt("01010101", 2))

        assertEquals("Checking sum of payment in lines", sum1, InvoiceUtil.sumPaymentLines(list, PaymentInterface.TYPE_IN, true))
        assertEquals("Checking sum of payment out lines", sum2, InvoiceUtil.sumPaymentLines(list, PaymentInterface.TYPE_OUT, true))

        sum1 = sum2 = new Money("255")
        assertEquals("Checking sum of payment in lines", sum1, InvoiceUtil.sumPaymentLines(list, PaymentInterface.TYPE_IN, false))
        assertEquals("Checking sum of payment out lines", sum2, InvoiceUtil.sumPaymentLines(list, PaymentInterface.TYPE_OUT, false))

    }

	@Test
    void testSumInvoiceLinesNoTax() {

		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Tax tax = newContext.newObject(Tax.class)
        tax.setRate(BigDecimal.ZERO)

        ArrayList<PayableLineInterface> list = new ArrayList<>()

        InvoiceLine invoiceLine1 = newContext.newObject(InvoiceLine.class)
        invoiceLine1.setTax(tax)
        invoiceLine1.setQuantity(BigDecimal.ONE)
        invoiceLine1.setPriceEachExTax(new Money("100"))
        invoiceLine1.setTaxEach(Money.ZERO)

        InvoiceLine invoiceLine2 = newContext.newObject(InvoiceLine.class)
        invoiceLine2.setTax(tax)
        invoiceLine2.setQuantity(BigDecimal.ONE)
        invoiceLine2.setPriceEachExTax(new Money("200"))
        invoiceLine2.setTaxEach(Money.ZERO)

        list.add(invoiceLine1)
        list.add(invoiceLine2)

        Money expectedValueExTax = new Money("300")
        assertEquals("Checking sum of invoice lines", expectedValueExTax, InvoiceUtil.sumInvoiceLines(list, false))
        assertEquals("Checking sum of invoice lines", expectedValueExTax, InvoiceUtil.sumInvoiceLines(list))

        Money discount = new Money("10")
        invoiceLine1.setDiscountEachExTax(discount)
        expectedValueExTax = expectedValueExTax.subtract(discount)
        assertEquals("Checking sum of invoice lines", expectedValueExTax, InvoiceUtil.sumInvoiceLines(list, false))
        assertEquals("Checking sum of invoice lines", expectedValueExTax, InvoiceUtil.sumInvoiceLines(list))

    }

	@Test
    void testSumInvoiceLinesWithTax() {

		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Tax tax = newContext.newObject(Tax.class)
        tax.setRate(BigDecimal.valueOf(0.1d))

        ArrayList<PayableLineInterface> list = new ArrayList<>()

        InvoiceLine invoiceLine1 = newContext.newObject(InvoiceLine.class)
        invoiceLine1.setTax(tax)
        invoiceLine1.setQuantity(BigDecimal.ONE)
        invoiceLine1.setPriceEachExTax(new Money("100"))
        invoiceLine1.setTaxEach(new Money("10"))

        InvoiceLine invoiceLine2 = newContext.newObject(InvoiceLine.class)
        invoiceLine2.setTax(tax)
        invoiceLine2.setQuantity(BigDecimal.ONE)
        invoiceLine2.setPriceEachExTax(new Money("200"))
        invoiceLine2.setTaxEach(new Money("20"))

        list.add(invoiceLine1)
        list.add(invoiceLine2)

        Money expectedValueExTax = new Money("300")
        Money expectedValueIncTax = expectedValueExTax.multiply(BigDecimal.ONE.add(tax.getRate()))
        assertEquals("Checking sum of invoice lines", expectedValueExTax, InvoiceUtil.sumInvoiceLines(list, false))
        assertEquals("Checking sum of invoice lines", expectedValueIncTax, InvoiceUtil.sumInvoiceLines(list))

        Money discount = new Money("10")
        invoiceLine1.setDiscountEachExTax(discount)
        invoiceLine1.setTaxEach(invoiceLine1.getTaxEach().subtract(new Money("1")))
        expectedValueExTax = expectedValueExTax.subtract(discount)
        expectedValueIncTax = expectedValueExTax.multiply(BigDecimal.ONE.add(tax.getRate()))
        assertEquals("Checking sum of invoice lines", expectedValueExTax, InvoiceUtil.sumInvoiceLines(list, false))
        assertEquals("Checking sum of invoice lines", expectedValueIncTax, InvoiceUtil.sumInvoiceLines(list))

    }

	@Test
    void testUpdateAmountOwingWithoutCommits() {

		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Account aDebtors = newContext.newObject(Account.class)
        aDebtors.setType(AccountType.ASSET)
        aDebtors.setAccountCode("11100")

        Contact contact = newContext.newObject(Contact.class)

        Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(aDebtors)
        invoice.setContact(contact)

        Tax tax = newContext.newObject(Tax.class)
        tax.setRate(BigDecimal.valueOf(0.1d))

        InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setPriceEachExTax(new Money("100"))
        invoiceLine.setTaxEach(new Money("10"))
        invoiceLine.setInvoice(invoice)

        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals("Checking amount owing", new Money("110"), invoice.getAmountOwing())

        Tax tax2 = newContext.newObject(Tax.class)
        tax2.setRate(BigDecimal.ZERO)

        InvoiceLine invoiceLine2 = newContext.newObject(InvoiceLine.class)
        invoiceLine2.setTax(tax2)
        invoiceLine2.setQuantity(BigDecimal.valueOf(5d))
        invoiceLine2.setPriceEachExTax(new Money("50"))
        invoiceLine2.setInvoice(invoice)

        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals("Checking amount owing", new Money("360"), invoice.getAmountOwing())

        PaymentIn payment = newContext.newObject(PaymentIn.class)
        payment.setAccountIn(aDebtors)
        payment.setAmount(new Money("100"))
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(newContext, PaymentMethod.class, PaymentType.CASH), payment).set()
        payment.setStatus(PaymentStatus.SUCCESS)
        payment.setPayer(contact)

        PaymentInLine pinLine = newContext.newObject(PaymentInLine.class)
        pinLine.setPayment(payment)
        invoice.addToPaymentInLines(pinLine)
        pinLine.setAmount(new Money("100"))

        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals("Checking amount owing", new Money("260"), invoice.getAmountOwing())

        payment.setAmount(new Money("360"))
        pinLine.setAmount(new Money("360"))

        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals("Checking amount owing", Money.ZERO, invoice.getAmountOwing())

    }

	@Test
    void testUpdateAmountOwingWithCommits() {

		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Account account = newContext.newObject(Account.class)
        account.setType(AccountType.ASSET)
        account.setAccountCode("11100")
        account.setIsEnabled(true)
        account.setDescription("testUpdateAmountOwingWithCommits")

        Account account3 = newContext.newObject(Account.class)
        account3.setType(AccountType.LIABILITY)
        account3.setAccountCode("21010")
        account3.setIsEnabled(true)
        account3.setDescription("testUpdateAmountOwingWithCommits2")
        //commit accounts first than link to taxes (avoid exception with circular dependency on tables)
		newContext.commitChanges()

        Contact contact = newContext.newObject(Contact.class)
        contact.setFirstName("testUpdateAmountOwingWithCommits")
        contact.setLastName("testUpdateAmountOwingWithCommits")

        Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(account)
        invoice.setContact(contact)
        invoice.setAmountOwing(Money.ZERO)
        invoice.setAllowAutoPay(Boolean.FALSE)

        Tax tax = newContext.newObject(Tax.class)
        tax.setRate(BigDecimal.valueOf(0.1d))
        tax.setPayableToAccount(account)
        tax.setTaxCode("GST")
        tax.setReceivableFromAccount(account)

        InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setPriceEachExTax(new Money("100"))
        invoiceLine.setDiscountEachExTax(Money.ZERO)
        invoiceLine.setTaxEach(new Money("10"))
        invoiceLine.setInvoice(invoice)
        invoiceLine.setTitle("test")
        invoiceLine.setAccount(account)
        invoiceLine.setPrepaidFeesAccount(account3)

        newContext.commitChanges()
        assertEquals("Checking amount owing", new Money("110"), invoice.getAmountOwing())

        Tax tax2 = newContext.newObject(Tax.class)
        tax2.setRate(BigDecimal.ZERO)
        tax2.setPayableToAccount(account)
        tax2.setTaxCode("N")
        tax2.setReceivableFromAccount(account)

        InvoiceLine invoiceLine2 = newContext.newObject(InvoiceLine.class)
        invoiceLine2.setTax(tax2)
        invoiceLine2.setQuantity(BigDecimal.valueOf(5d))
        invoiceLine2.setPriceEachExTax(new Money("50"))
        invoiceLine2.setDiscountEachExTax(Money.ZERO)
        invoiceLine2.setTaxEach(new Money("0"))
        invoiceLine2.setInvoice(invoice)
        invoiceLine2.setTitle("test2")
        invoiceLine2.setAccount(account)
        invoiceLine2.setPrepaidFeesAccount(account3)

        newContext.commitChanges()
        assertEquals("Checking amount owing", new Money("360"), invoice.getAmountOwing())

        Account account2 = newContext.newObject(Account.class)
        account2.setType(AccountType.ASSET)
        account2.setAccountCode("21100")
        account2.setIsEnabled(true)
        account2.setDescription("testUpdateAmountOwingWithCommits - asset")

        PaymentIn payment = newContext.newObject(PaymentIn.class)
        payment.setPaymentDate(LocalDate.now())
        payment.setAccountIn(account2)
        payment.setAmount(new Money("100"))
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(newContext, PaymentMethod.class, PaymentType.CASH), payment).set()
        payment.setStatus(PaymentStatus.SUCCESS)
        payment.setPayer(contact)

        PaymentInLine pinLine = newContext.newObject(PaymentInLine.class)
        pinLine.setPayment(payment)
        invoice.addToPaymentInLines(pinLine)
        // pinLine.setInvoice(invoice);
		pinLine.setAmount(new Money("100"))
        pinLine.setAccountOut(account2)

        newContext.commitChanges()
        assertEquals("Checking amount owing", new Money("260"), invoice.getAmountOwing())

        PaymentIn payment2 = newContext.newObject(PaymentIn.class)
        payment2.setPaymentDate(LocalDate.now())
        payment2.setAccountIn(account2)
        payment2.setAmount(new Money("260"))
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(newContext, PaymentMethod.class, PaymentType.CASH), payment2).set()
        payment2.setStatus(PaymentStatus.SUCCESS)
        payment2.setPayer(contact)

        PaymentInLine pinLine2 = newContext.newObject(PaymentInLine.class)
        pinLine2.setPayment(payment2)
        // pinLine2.setInvoice(invoice);
		invoice.addToPaymentInLines(pinLine2)
        pinLine2.setAmount(new Money("260"))
        pinLine2.setAccountOut(account2)

        newContext.commitChanges()
        assertEquals("Checking amount owing", Money.ZERO, invoice.getAmountOwing())

    }

	@Test
    void testUpdateAmountOwingWithoutCommitsPaymentInAndOut() {

		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Account account = newContext.newObject(Account.class)
        account.setType(AccountType.ASSET)
        account.setAccountCode("11100")
        account.setIsEnabled(true)
        account.setDescription("testUpdateAmountOwingWithCommits")

        Account account3 = newContext.newObject(Account.class)
        account3.setType(AccountType.LIABILITY)
        account3.setAccountCode("21010")
        account3.setIsEnabled(true)
        account3.setDescription("testUpdateAmountOwingWithCommits2")

        Contact contact = newContext.newObject(Contact.class)
        contact.setFirstName("testUpdateAmountOwingWithCommits")
        contact.setLastName("testUpdateAmountOwingWithCommits")

        Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(account)
        invoice.setContact(contact)
        invoice.setAmountOwing(Money.ZERO)

        Tax tax = newContext.newObject(Tax.class)
        tax.setRate(BigDecimal.valueOf(0.1d))
        tax.setPayableToAccount(account)
        tax.setTaxCode("GST")
        tax.setReceivableFromAccount(account)

        InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setPriceEachExTax(new Money("100"))
        invoiceLine.setDiscountEachExTax(Money.ZERO)
        invoiceLine.setTaxEach(new Money("10"))
        invoiceLine.setInvoice(invoice)
        invoiceLine.setTitle("test")
        invoiceLine.setAccount(account)
        invoiceLine.setPrepaidFeesAccount(account3)

        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals("Checking amount owing", new Money("110"), invoice.getAmountOwing())

        Tax tax2 = newContext.newObject(Tax.class)
        tax2.setRate(BigDecimal.ZERO)
        tax2.setPayableToAccount(account)
        tax2.setTaxCode("N")
        tax2.setReceivableFromAccount(account)

        InvoiceLine invoiceLine2 = newContext.newObject(InvoiceLine.class)
        invoiceLine2.setTax(tax2)
        invoiceLine2.setQuantity(BigDecimal.valueOf(5d))
        invoiceLine2.setPriceEachExTax(new Money("50"))
        invoiceLine2.setDiscountEachExTax(Money.ZERO)
        invoiceLine2.setInvoice(invoice)
        invoiceLine2.setTitle("test2")
        invoiceLine2.setAccount(account)
        invoiceLine2.setPrepaidFeesAccount(account3)

        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals("Checking amount owing", new Money("360"), invoice.getAmountOwing())

        Account account2 = newContext.newObject(Account.class)
        account2.setType(AccountType.ASSET)
        account2.setAccountCode("21100")
        account2.setIsEnabled(true)
        account2.setDescription("testUpdateAmountOwingWithCommits - asset")

        PaymentIn payment = newContext.newObject(PaymentIn.class)
        payment.setAccountIn(account2)
        payment.setAmount(new Money("460"))
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(newContext, PaymentMethod.class, PaymentType.CASH), payment).set()
        payment.setStatus(PaymentStatus.SUCCESS)
        payment.setPayer(contact)

        PaymentInLine pinLine = newContext.newObject(PaymentInLine.class)
        pinLine.setPayment(payment)
        invoice.addToPaymentInLines(pinLine)
        // pinLine.setInvoice(invoice);
		pinLine.setAmount(new Money("460"))
        pinLine.setAccountOut(account2)

        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals("Checking amount owing", new Money("100").negate(), invoice.getAmountOwing())

        PaymentOut payment2 = newContext.newObject(PaymentOut.class)
        payment2.setAccountOut(account2)
        payment2.setAmount(new Money("100"))
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(newContext, PaymentMethod.class, PaymentType.CASH), payment2).set()
        payment2.setStatus(PaymentStatus.SUCCESS)
        payment2.setPayee(contact)

        PaymentOutLine pinLine2 = newContext.newObject(PaymentOutLine.class)
        pinLine2.setPayment(payment2)
        // pinLine2.setInvoice(invoice);
		invoice.addToPaymentOutLines(pinLine2)
        pinLine2.setAmount(new Money("100"))
        pinLine2.setAccountIn(account2)

        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals("Checking amount owing", Money.ZERO, invoice.getAmountOwing())

    }

	@Test
    void testUpdateAmountOwingWithCommitsPaymentInAndOut() {

		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Account account = newContext.newObject(Account.class)
        account.setType(AccountType.ASSET)
        account.setAccountCode("11100")
        account.setIsEnabled(true)
        account.setDescription("testUpdateAmountOwingWithCommits")

        Account account3 = newContext.newObject(Account.class)
        account3.setType(AccountType.LIABILITY)
        account3.setAccountCode("21010")
        account3.setIsEnabled(true)
        account3.setDescription("testUpdateAmountOwingWithCommits2")
        //commit accounts first than link to taxes (avoid exception with circular dependency on tables)
		newContext.commitChanges()

        Contact contact = newContext.newObject(Contact.class)
        contact.setFirstName("testUpdateAmountOwingWithCommits")
        contact.setLastName("testUpdateAmountOwingWithCommits")

        Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(account)
        invoice.setContact(contact)
        invoice.setAmountOwing(Money.ZERO)
        invoice.setAllowAutoPay(Boolean.FALSE)

        Tax tax = newContext.newObject(Tax.class)
        tax.setRate(BigDecimal.valueOf(0.1d))
        tax.setPayableToAccount(account)
        tax.setTaxCode("GST")
        tax.setReceivableFromAccount(account)

        InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setPriceEachExTax(new Money("100"))
        invoiceLine.setDiscountEachExTax(Money.ZERO)
        invoiceLine.setTaxEach(new Money("10"))
        invoiceLine.setInvoice(invoice)
        invoiceLine.setTitle("test")
        invoiceLine.setAccount(account)
        invoiceLine.setPrepaidFeesAccount(account3)

        newContext.commitChanges()
        assertEquals("Checking amount owing", new Money("110"), invoice.getAmountOwing())

        Tax tax2 = newContext.newObject(Tax.class)
        tax2.setRate(BigDecimal.ZERO)
        tax2.setPayableToAccount(account)
        tax2.setTaxCode("N")
        tax2.setReceivableFromAccount(account)

        InvoiceLine invoiceLine2 = newContext.newObject(InvoiceLine.class)
        invoiceLine2.setTax(tax2)
        invoiceLine2.setQuantity(BigDecimal.valueOf(5d))
        invoiceLine2.setPriceEachExTax(new Money("50"))
        invoiceLine2.setDiscountEachExTax(Money.ZERO)
        invoiceLine2.setTaxEach(new Money("0"))
        invoiceLine2.setInvoice(invoice)
        invoiceLine2.setTitle("test2")
        invoiceLine2.setAccount(account)
        invoiceLine2.setPrepaidFeesAccount(account3)

        newContext.commitChanges()
        assertEquals("Checking amount owing", new Money("360"), invoice.getAmountOwing())

        Account account2 = newContext.newObject(Account.class)
        account2.setType(AccountType.ASSET)
        account2.setAccountCode("21100")
        account2.setIsEnabled(true)
        account2.setDescription("testUpdateAmountOwingWithCommits - asset")

        PaymentIn payment = newContext.newObject(PaymentIn.class)
        payment.setPaymentDate(LocalDate.now())
        payment.setAccountIn(account2)
        payment.setAmount(new Money("460"))
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(newContext, PaymentMethod.class, PaymentType.CASH), payment).set()
        payment.setStatus(PaymentStatus.SUCCESS)
        payment.setPayer(contact)

        PaymentInLine pinLine = newContext.newObject(PaymentInLine.class)
        pinLine.setPayment(payment)
        invoice.addToPaymentInLines(pinLine)
        // pinLine.setInvoice(invoice);
		pinLine.setAmount(new Money("460"))
        pinLine.setAccountOut(account2)

        newContext.commitChanges()
        assertEquals("Checking amount owing", new Money("100").negate(), invoice.getAmountOwing())

        PaymentOut payment2 = newContext.newObject(PaymentOut.class)
        payment2.setPaymentDate(LocalDate.now())
        payment2.setAccountOut(account2)
        payment2.setAmount(new Money("100"))
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(newContext, PaymentMethod.class, PaymentType.CASH), payment2).set()
        payment2.setStatus(PaymentStatus.SUCCESS)
        payment2.setPayee(contact)

        PaymentOutLine pinLine2 = newContext.newObject(PaymentOutLine.class)
        pinLine2.setPayment(payment2)
        // pinLine2.setInvoice(invoice);
		invoice.addToPaymentOutLines(pinLine2)
        pinLine2.setAmount(new Money("100"))
        pinLine2.setAccountIn(account2)

        newContext.commitChanges()
        assertEquals("Checking amount owing", Money.ZERO, invoice.getAmountOwing())

    }

	@Test
    void testAllocateSingleInvoice() {

		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        // setup required accounts, taxes
		Account aDebtors = newContext.newObject(Account.class)
        aDebtors.setType(AccountType.ASSET)
        aDebtors.setAccountCode("123")
        aDebtors.setDescription("123")
        aDebtors.setIsEnabled(true)

        Account taxAccount = newContext.newObject(Account.class)
        taxAccount.setType(AccountType.LIABILITY)
        taxAccount.setAccountCode("1234")
        taxAccount.setDescription("1234")
        taxAccount.setIsEnabled(true)

        Account aIncome = newContext.newObject(Account.class)
        aIncome.setType(AccountType.ASSET)
        aIncome.setAccountCode("12345")
        aIncome.setDescription("12345")
        aIncome.setIsEnabled(true)

        Account accountIn = newContext.newObject(Account.class)
        accountIn.setType(AccountType.ASSET)
        accountIn.setAccountCode("12345")
        accountIn.setDescription("12345")
        accountIn.setIsEnabled(true)

        Account account3 = newContext.newObject(Account.class)
        account3.setType(AccountType.LIABILITY)
        account3.setAccountCode("21010")
        account3.setIsEnabled(true)
        account3.setDescription("testUpdateAmountOwingWithCommits2")
        //commit accounts first than link to taxes (avoid exception with circular dependency on tables)
		newContext.commitChanges()

        Tax tax = newContext.newObject(Tax.class)
        tax.setRate(BigDecimal.valueOf(0.1d))
        tax.setReceivableFromAccount(taxAccount)
        tax.setPayableToAccount(taxAccount)
        tax.setTaxCode("G")

        Contact contact = newContext.newObject(Contact.class)
        contact.setFirstName("Albert")
        contact.setLastName("Einstein")
        newContext.commitChanges()

        // this is the invoice we are going to test
		Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(aDebtors)
        invoice.setContact(contact)
        invoice.setAllowAutoPay(Boolean.FALSE)

        // ... and its invoice line
		InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setPriceEachExTax(new Money("100"))
        invoiceLine.setTaxEach(new Money("10"))
        invoiceLine.setInvoice(invoice)
        invoiceLine.setAccount(aDebtors)
        invoiceLine.setTitle("test")
        invoiceLine.setDiscountEachExTax(Money.ZERO)
        invoiceLine.setPrepaidFeesAccount(account3)

        // lets create first payment
		PaymentIn payment = newContext.newObject(PaymentIn.class)
        payment.setPaymentDate(LocalDate.now())
        payment.setStatus(PaymentStatus.SUCCESS)
        payment.setPayer(contact)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(newContext, PaymentMethod.class), payment).set()
        payment.setAccountIn(accountIn)

        ArrayList<PaymentLineInterface> list = new ArrayList<>()

        // first user chooses to pay invoice in full
		Money result = InvoiceUtil.invoiceAllocate(invoice, new Money("110"), payment, list, false)
        assertEquals("Checking money allocation for invoice", new Money("110"), result)
        assertEquals("Checking number of payment lines", 1, invoice.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment.getPaymentLines().size())
        assertEquals("Checking money allocation for paymentline", new Money("110"), payment.getPaymentLines().get(0).getAmount())

        // then just half
		result = InvoiceUtil.invoiceAllocate(invoice, new Money("55"), payment, list, false)
        assertEquals("Checking money allocation for invoice", new Money("55"), result)
        assertEquals("Checking number of payment lines", 1, invoice.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment.getPaymentLines().size())
        assertEquals("Checking money allocation for paymentline", new Money("55"), payment.getPaymentLines().get(0).getAmount())

        // then actually types some wrong amount
		result = InvoiceUtil.invoiceAllocate(invoice, new Money("150"), payment, list, false)
        assertEquals("Checking money allocation for invoice", new Money("110"), result)
        assertEquals("Checking number of payment lines", 1, invoice.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment.getPaymentLines().size())
        assertEquals("Checking money allocation for paymentline", new Money("110"), payment.getPaymentLines().get(0).getAmount())

        // then removes the payment alltogether
		InvoiceUtil.invoiceDeallocate(invoice, payment, list)

        assertEquals("Checking number of payment lines", 0, invoice.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 0, payment.getPaymentLines().size())

        // then creates a $0 payment
		result = InvoiceUtil.invoiceAllocate(invoice, Money.ZERO, payment, list, false)
        assertEquals("Checking money allocation for invoice", Money.ZERO, result)
        assertEquals("Checking number of payment lines", 1, invoice.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment.getPaymentLines().size())
        assertEquals("Checking money allocation for paymentline", Money.ZERO, payment.getPaymentLines().get(0).getAmount())

        // removes it again
		InvoiceUtil.invoiceDeallocate(invoice, payment, list)

        // finally create a payment for half the invoice
		result = InvoiceUtil.invoiceAllocate(invoice, new Money("55"), payment, list, false)
        assertEquals("Checking money allocation for invoice", new Money("55"), result)
        assertEquals("Checking number of payment lines", 1, invoice.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment.getPaymentLines().size())
        assertEquals("Checking money allocation for paymentline", new Money("55"), payment.getPaymentLines().get(0).getAmount())

        payment.setAmount(payment.getPaymentInLines().get(0).getAmount())

        // save
		newContext.commitChanges()

        // confirm the values are ok
		InvoiceUtil.updateAmountOwing(invoice)
        assertEquals("Checking money allocation for invoice", new Money("55"), result)
        assertEquals("Checking money owing for invoice", new Money("55"), invoice.getAmountOwing())
        assertEquals("Checking number of payment lines", 1, invoice.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment.getPaymentLines().size())
        assertEquals("Checking money allocation for paymentline", new Money("55"), payment.getPaymentLines().get(0).getAmount())

        // editing existing payment line should not be allowed
		try {
			result = InvoiceUtil.invoiceAllocate(invoice, new Money("10"), payment, list, false)
            fail("The editing of existing payment line should be forbidden")
        } catch (IllegalStateException ex) {
			assertEquals("The amount can be only altered for a new payment line.", ex.getMessage())
        }

		// verify the values again
		assertEquals("Checking money owing for invoice", new Money("55"), invoice.getAmountOwing())
        assertEquals("Checking number of payment lines", 1, invoice.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment.getPaymentLines().size())
        assertEquals("Checking money allocation for paymentline", new Money("55"), payment.getPaymentLines().get(0).getAmount())

        // create second payment
		PaymentIn payment2 = newContext.newObject(PaymentIn.class)
        payment2.setPaymentDate(LocalDate.now())
        payment2.setStatus(PaymentStatus.SUCCESS)
        payment2.setPayer(contact)
        payment2.setAccountIn(accountIn)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(newContext, PaymentMethod.class, PaymentType.CASH), payment2).set()
        ArrayList<PaymentLineInterface> list2 = new ArrayList<>()

        // first just small
		result = InvoiceUtil.invoiceAllocate(invoice, new Money("11"), payment2, list2, false)
        assertEquals("Checking money allocation for invoice", new Money("11"), result)
        assertEquals("Checking number of payment lines", 2, invoice.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment2.getPaymentLines().size())
        assertEquals("Checking money allocation for paymentline", new Money("55"), payment.getPaymentLines().get(0).getAmount())
        assertEquals("Checking money allocation for paymentline", new Money("11"), payment2.getPaymentLines().get(0).getAmount())

        // then $0
		result = InvoiceUtil.invoiceAllocate(invoice, Money.ZERO, payment2, list2, false)
        assertEquals("Checking money allocation for invoice", Money.ZERO, result)
        assertEquals("Checking number of payment lines", 2, invoice.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment2.getPaymentLines().size())
        assertEquals("Checking money allocation for paymentline", new Money("55"), payment.getPaymentLines().get(0).getAmount())
        assertEquals("Checking money allocation for paymentline", Money.ZERO, payment2.getPaymentLines().get(0).getAmount())

        // then removes the payment alltogether
		InvoiceUtil.invoiceDeallocate(invoice, payment2, list2)

        assertEquals("Checking number of payment lines", 1, invoice.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 0, payment2.getPaymentLines().size())
        assertEquals("Checking money allocation for paymentline", new Money("55"), payment.getPaymentLines().get(0).getAmount())

        // then pay second half
		result = InvoiceUtil.invoiceAllocate(invoice, new Money("55"), payment2, list2, false)
        assertEquals("Checking money allocation for invoice", new Money("55"), result)
        assertEquals("Checking number of payment lines", 2, invoice.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment2.getPaymentLines().size())
        assertEquals("Checking money allocation for paymentline", new Money("55"), payment.getPaymentLines().get(0).getAmount())
        assertEquals("Checking money allocation for paymentline", new Money("55"), payment2.getPaymentLines().get(0).getAmount())

        payment2.setAmount(payment2.getPaymentInLines().get(0).getAmount())

        // save
		newContext.commitChanges()

        // verify all
		InvoiceUtil.updateAmountOwing(invoice)
        assertEquals("Checking money owing for invoice", Money.ZERO, invoice.getAmountOwing())
        assertEquals("Checking number of payment lines", 2, invoice.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 1, payment2.getPaymentLines().size())
        assertEquals("Checking money allocation for paymentline", new Money("55"), payment.getPaymentLines().get(0).getAmount())
        assertEquals("Checking money allocation for paymentline", new Money("55"), payment2.getPaymentLines().get(0).getAmount())
    }

	@Test
    void testAllocateMultipleInvoices() {

		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        // setup required accounts, taxes
		Account aDebtors = newContext.newObject(Account.class)
        aDebtors.setType(AccountType.ASSET)
        aDebtors.setAccountCode("123")
        aDebtors.setDescription("123")
        aDebtors.setIsEnabled(true)

        Account taxAccount = newContext.newObject(Account.class)
        taxAccount.setType(AccountType.LIABILITY)
        taxAccount.setAccountCode("1234")
        taxAccount.setDescription("1234")
        taxAccount.setIsEnabled(true)

        Account aIncome = newContext.newObject(Account.class)
        aIncome.setType(AccountType.ASSET)
        aIncome.setAccountCode("12345")
        aIncome.setDescription("12345")
        aIncome.setIsEnabled(true)

        Account accountIn = newContext.newObject(Account.class)
        accountIn.setType(AccountType.ASSET)
        accountIn.setAccountCode("12345")
        accountIn.setDescription("12345")
        accountIn.setIsEnabled(true)

        Account account3 = newContext.newObject(Account.class)
        account3.setType(AccountType.LIABILITY)
        account3.setAccountCode("21010")
        account3.setIsEnabled(true)
        account3.setDescription("testUpdateAmountOwingWithCommits2")
        //commit accounts first than link to taxes (avoid exception with circular dependency on tables)
		newContext.commitChanges()

        Tax tax = newContext.newObject(Tax.class)
        tax.setRate(BigDecimal.valueOf(0.1d))
        tax.setReceivableFromAccount(taxAccount)
        tax.setPayableToAccount(taxAccount)
        tax.setTaxCode("G")

        Contact contact = newContext.newObject(Contact.class)
        contact.setFirstName("Marie")
        contact.setLastName("Sklodowska-Curie")

        // first invoice
		Invoice invoice = newContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(aDebtors)
        invoice.setContact(contact)
        invoice.setAllowAutoPay(Boolean.FALSE)

        Enrolment enrl = createEnrolment(newContext, aIncome, tax)
        enrl.setStatus(EnrolmentStatus.IN_TRANSACTION)
        // ... and its invoice line
		InvoiceLine invoiceLine = newContext.newObject(InvoiceLine.class)
        invoiceLine.setEnrolment(enrl)
        invoiceLine.setTax(tax)
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setPriceEachExTax(new Money("100"))
        invoiceLine.setTaxEach(new Money("10"))
        invoiceLine.setInvoice(invoice)
        invoiceLine.setAccount(aDebtors)
        invoiceLine.setTitle("test")
        invoiceLine.setDiscountEachExTax(Money.ZERO)
        invoiceLine.setPrepaidFeesAccount(account3)

        // second invoice
		Invoice invoice2 = newContext.newObject(Invoice.class)
        invoice2.setDebtorsAccount(aDebtors)
        invoice2.setContact(contact)
        invoice2.setAllowAutoPay(Boolean.FALSE)

        Enrolment enrolment = createEnrolment(newContext, aIncome, tax)
        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        // ... and its invoice line
		InvoiceLine invoiceLine2 = newContext.newObject(InvoiceLine.class)
        invoiceLine2.setTax(tax)
        invoiceLine2.setEnrolment(enrolment)
        invoiceLine2.setQuantity(BigDecimal.ONE)
        invoiceLine2.setPriceEachExTax(new Money("100"))
        invoiceLine2.setTaxEach(new Money("10"))
        invoiceLine2.setInvoice(invoice2)
        invoiceLine2.setAccount(aDebtors)
        invoiceLine2.setTitle("test")
        invoiceLine2.setDiscountEachExTax(Money.ZERO)
        invoiceLine2.setPrepaidFeesAccount(account3)

        // save
		newContext.commitChanges()

        assertEquals("Checking the amount owing", new Money("220"), InvoiceUtil.amountOwingForPayer(contact))

        // lets create first payment
		PaymentIn payment = newContext.newObject(PaymentIn.class)
        payment.setPaymentDate(LocalDate.now())
        payment.setStatus(PaymentStatus.SUCCESS)
        payment.setPayer(contact)
        payment.setAccountIn(accountIn)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(newContext, PaymentMethod.class, PaymentType.CASH), payment).set()
        payment.setAmount(new Money("150"))

        ArrayList<PaymentLineInterface> list = new ArrayList<>()

        Money result = InvoiceUtil.allocateMoneyToInvoices(payment.getAmount(), contact.getInvoices(), payment, list)
        assertEquals("Checking money left over", Money.ZERO, result)
        assertEquals("Checking number of invoice payment lines", 1, invoice.getPaymentLines().size())
        assertEquals("Checking number of invoice payment lines", 1, invoice2.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 2, payment.getPaymentLines().size())
        PaymentInLine pLine1 = payment.getPaymentInLines().get(0)
        PaymentInLine pLine2 = payment.getPaymentInLines().get(1)
        if (pLine1.getAmount().compareTo(pLine2.getAmount()) > 0) {
			assertEquals("Checking money allocation for paymentline", new Money("110"), pLine1.getAmount())
            assertEquals("Checking money allocation for paymentline", new Money("40"), pLine2.getAmount())
        } else {
			assertEquals("Checking money allocation for paymentline", new Money("110"), pLine2.getAmount())
            assertEquals("Checking money allocation for paymentline", new Money("40"), pLine1.getAmount())
        }
		assertEquals("Checking payment total", new Money("150"), payment.getAmount())
        assertEquals("Checking the amount owing", new Money("70"), InvoiceUtil.amountOwingForPayer(contact))

        // save...
		newContext.commitChanges()
        // and verify again
		assertEquals("Checking money left over", Money.ZERO, result)
        assertEquals("Checking number of invoice payment lines", 1, invoice.getPaymentLines().size())
        assertEquals("Checking number of invoice payment lines", 1, invoice2.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 2, payment.getPaymentLines().size())
        pLine1 = payment.getPaymentInLines().get(0)
        pLine2 = payment.getPaymentInLines().get(1)
        if (pLine1.getAmount().compareTo(pLine2.getAmount()) > 0) {
			assertEquals("Checking money allocation for paymentline", new Money("110"), pLine1.getAmount())
            assertEquals("Checking money allocation for paymentline", new Money("40"), pLine2.getAmount())
        } else {
			assertEquals("Checking money allocation for paymentline", new Money("110"), pLine2.getAmount())
            assertEquals("Checking money allocation for paymentline", new Money("40"), pLine1.getAmount())
        }
		assertEquals("Checking payment total", new Money("150"), payment.getAmount())
        assertEquals("Checking the amount owing", new Money("70"), InvoiceUtil.amountOwingForPayer(contact))

        // now third invoice - credit note
		Invoice invoice3 = newContext.newObject(Invoice.class)
        invoice3.setDebtorsAccount(aDebtors)
        invoice3.setContact(contact)
        invoice3.setAllowAutoPay(Boolean.FALSE)

        Enrolment enrl3 = createEnrolment(newContext, aIncome, tax)
        enrl3.setStatus(EnrolmentStatus.NEW)
        // ... and its invoice line
		InvoiceLine invoiceLine3 = newContext.newObject(InvoiceLine.class)
        invoiceLine3.setEnrolment(enrl3)
        invoiceLine3.setTax(tax)
        invoiceLine3.setQuantity(BigDecimal.ONE)
        invoiceLine3.setPriceEachExTax(new Money("-63.64"))
        invoiceLine3.setDiscountEachExTax(Money.ZERO)
        invoiceLine3.setTaxEach(invoiceLine3.getPriceEachExTax().multiply(invoiceLine3.getTax().getRate()))
        invoiceLine3.setInvoice(invoice3)
        invoiceLine3.setAccount(aDebtors)
        invoiceLine3.setTitle("test")

        invoiceLine3.setPrepaidFeesAccount(account3)

        // creating a $0 payment should make everything square
		PaymentIn payment2 = newContext.newObject(PaymentIn.class)
        payment2.setPaymentDate(LocalDate.now())
        payment2.setStatus(PaymentStatus.SUCCESS)
        payment2.setPayer(contact)
        payment2.setAccountIn(accountIn)
        payment2.setAmount(Money.ZERO)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(newContext, PaymentMethod.class), payment2).set()

        ArrayList<PaymentLineInterface> list2 = new ArrayList<>()

        result = InvoiceUtil.allocateMoneyToInvoices(payment2.getAmount(), contact.getInvoices(), payment2, list2)
        assertEquals("Checking money left over", Money.ZERO, result)
        assertEquals("Checking number of invoice payment lines", 1, invoice.getPaymentLines().size())
        assertEquals("Checking number of invoice payment lines", 2, invoice2.getPaymentLines().size())
        assertEquals("Checking number of invoice payment lines", 1, invoice3.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 2, payment2.getPaymentLines().size())
        pLine1 = payment2.getPaymentInLines().get(0)
        pLine2 = payment2.getPaymentInLines().get(1)
        if (pLine1.getAmount().compareTo(pLine2.getAmount()) > 0) {
			assertEquals("Checking money allocation for paymentline", new Money("70"), pLine1.getAmount())
            assertEquals("Checking money allocation for paymentline", new Money("-70"), pLine2.getAmount())
        } else {
			assertEquals("Checking money allocation for paymentline", new Money("70"), pLine2.getAmount())
            assertEquals("Checking money allocation for paymentline", new Money("-70"), pLine1.getAmount())
        }
		assertEquals("Checking payment total", Money.ZERO, payment2.getAmount())
        assertEquals("Checking the amount owing", Money.ZERO, InvoiceUtil.amountOwingForPayer(contact))

        // save...
		newContext.commitChanges()
        // and verify again
		assertEquals("Checking money left over", Money.ZERO, result)
        assertEquals("Checking number of invoice payment lines", 1, invoice.getPaymentLines().size())
        assertEquals("Checking number of invoice payment lines", 2, invoice2.getPaymentLines().size())
        assertEquals("Checking number of invoice payment lines", 1, invoice3.getPaymentLines().size())
        assertEquals("Checking number of payment lines", 2, payment2.getPaymentLines().size())
        pLine1 = payment2.getPaymentInLines().get(0)
        pLine2 = payment2.getPaymentInLines().get(1)
        if (pLine1.getAmount().compareTo(pLine2.getAmount()) > 0) {
			assertEquals("Checking money allocation for paymentline", new Money("70"), pLine1.getAmount())
            assertEquals("Checking money allocation for paymentline", new Money("-70"), pLine2.getAmount())
        } else {
			assertEquals("Checking money allocation for paymentline", new Money("70"), pLine2.getAmount())
            assertEquals("Checking money allocation for paymentline", new Money("-70"), pLine1.getAmount())
        }
		assertEquals("Checking payment total", Money.ZERO, payment2.getAmount())
        assertEquals("Checking the amount owing", Money.ZERO, InvoiceUtil.amountOwingForPayer(contact))
    }

	private Enrolment createEnrolment(ObjectContext newContext, Account account, Tax tax) {

		Course course = newContext.newObject(Course.class)
        course.setCode("AABBDD" + codeSequence++)
        course.setName("courseName")
        course.setFieldConfigurationSchema(DataGenerator.valueOf(newContext).getFieldConfigurationScheme())

        CourseClass cc = newContext.newObject(CourseClass.class)
        cc.setSessionsCount(0)
        cc.setMinimumPlaces(4)
        cc.setMaximumPlaces(5)
        cc.setCode("testCourse")
        cc.setCourse(course)
        cc.setTax(tax)
        cc.setIncomeAccount(account)
        cc.setIsActive(true)
        cc.setFeeExGst(Money.ZERO)
        cc.setTaxAdjustment(Money.ZERO)
        cc.setDeliveryMode(DeliveryMode.CLASSROOM)
        cc.setIsClassFeeApplicationOnly(true)
        cc.setSuppressAvetmissExport(false)
        cc.setAttendanceType(CourseClassAttendanceType.NO_INFORMATION)

        Contact contact = newContext.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Student student = newContext.newObject(Student.class)
        contact.setStudent(student)

        Enrolment enrl = newContext.newObject(Enrolment.class)
        enrl.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrl.setStudent(student)
        enrl.setCourseClass(cc)

        return enrl
    }

}
