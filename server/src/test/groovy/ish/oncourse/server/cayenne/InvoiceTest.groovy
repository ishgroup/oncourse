/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import ish.CayenneIshTestCase
import ish.common.types.PaymentSource
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.util.AccountUtil
import ish.util.PaymentMethodUtil
import org.apache.cayenne.access.DataContext
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

import java.time.LocalDate

/**
 */
class InvoiceTest extends CayenneIshTestCase {

	@Before
    void setup() throws Exception {
		super.setup()
        injector.getInstance(PreferenceController.class).setReplicationEnabled(true)
    }

	@Test
    void testAmountOwing() throws Exception {
		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Money invoiceMoney = new Money("90").negate()
        Money paymentMoney = new Money("40")

        Tax tax = Tax.getTaxWithCode("N", newContext)

        Invoice invoice = newContext.newObject(Invoice.class)

        Contact contact = newContext.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")
        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)

        InvoiceLine il = newContext.newObject(InvoiceLine.class)
        il.setAccount(AccountUtil.getDefaultStudentEnrolmentsAccount(newContext, Account.class))
        il.setDescription("test line")
        il.setInvoice(invoice)
        il.setTax(newContext.localObject(tax))
        il.setPriceEachExTax(invoiceMoney)
        il.setDiscountEachExTax(Money.ZERO)
        il.setTitle("test title")
        il.setQuantity(BigDecimal.ONE)
        il.setTaxEach(Money.ZERO)
        il.setPrepaidFeesAccount(AccountUtil.getDefaultPrepaidFeesAccount(newContext, Account.class))

        invoice.setDebtorsAccount(AccountUtil.getDefaultDebtorsAccount(newContext, Account.class))

        newContext.commitChanges()

        // check onPostAdd
		assertNotNull(invoice.getInvoiceDate())
        assertNotNull(invoice.getDateDue())
        assertNotNull(invoice.getInvoiceNumber())

        // check onPostUpdate
		assertEquals("Check AmountOwing: ", invoiceMoney, invoice.getAmountOwing())

        PaymentOut paymentOut = newContext.newObject(PaymentOut.class)
        paymentOut.setPaymentDate(LocalDate.now())
        paymentOut.setAmount(paymentMoney)
        paymentOut.setPayee(contact)
        paymentOut.setReconciled(true)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(newContext, PaymentMethod.class, PaymentType.CASH), paymentOut).set()
        paymentOut.setAccountOut(AccountUtil.getDefaultDebtorsAccount(newContext, Account.class))
        paymentOut.setStatus(PaymentStatus.SUCCESS)

        PaymentOutLine paymentOutLine = newContext.newObject(PaymentOutLine.class)
        paymentOutLine.setAmount(paymentMoney)
        paymentOutLine.setAccountIn(AccountUtil.getDefaultBankAccount(newContext, Account.class))
        paymentOutLine.setPaymentOut(paymentOut)

        invoice.addToPaymentOutLines(paymentOutLine)

        assertEquals("Check AmountOwing: ", invoiceMoney, invoice.getAmountOwing())

        newContext.commitChanges()

        // check onPostUpdate
		assertEquals("Check AmountOwing: ", invoiceMoney.add(paymentMoney), invoice.getAmountOwing())

    }

	@Test
    void testAmountOwing2() throws Exception {
		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Money invoiceMoney = new Money("90")
        Money paymentMoney = new Money("40")

        Tax tax = Tax.getTaxWithCode("N", newContext)

        Invoice invoice = newContext.newObject(Invoice.class)

        Contact contact = newContext.newObject(Contact.class)
        contact.setFirstName("firstName2")
        contact.setLastName("lastName2")
        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)

        InvoiceLine il = newContext.newObject(InvoiceLine.class)
        il.setAccount(AccountUtil.getDefaultStudentEnrolmentsAccount(newContext, Account.class))
        il.setDescription("test line 2")
        il.setInvoice(invoice)
        il.setTax(newContext.localObject(tax))
        il.setPriceEachExTax(invoiceMoney)
        il.setDiscountEachExTax(Money.ZERO)
        il.setTitle("test title")
        il.setQuantity(BigDecimal.ONE)
        il.setTaxEach(Money.ZERO)
        il.setPrepaidFeesAccount(AccountUtil.getDefaultPrepaidFeesAccount(newContext, Account.class))

        invoice.setDebtorsAccount(AccountUtil.getDefaultDebtorsAccount(newContext, Account.class))

        newContext.commitChanges()

        // check onPostAdd
		assertNotNull(invoice.getInvoiceDate())
        assertNotNull(invoice.getDateDue())
        assertNotNull(invoice.getInvoiceNumber())

        // check onPostUpdate
		assertEquals("Check AmountOwing: ", invoiceMoney, invoice.getAmountOwing())

        PaymentIn paymentin = newContext.newObject(PaymentIn.class)
        paymentin.setPaymentDate(LocalDate.now())
        paymentin.setAmount(paymentMoney)
        paymentin.setPayer(contact)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(newContext, PaymentMethod.class, PaymentType.CASH), paymentin).set()
        paymentin.setAccountIn(AccountUtil.getDefaultBankAccount(newContext, Account.class))
        paymentin.setStatus(PaymentStatus.SUCCESS)

        PaymentInLine paymentInLine = newContext.newObject(PaymentInLine.class)
        paymentInLine.setAmount(paymentMoney)
        paymentInLine.setAccountOut(AccountUtil.getDefaultDebtorsAccount(newContext, Account.class))
        paymentInLine.setPaymentIn(paymentin)

        invoice.addToPaymentInLines(paymentInLine)

        assertEquals("Check AmountOwing: ", invoiceMoney, invoice.getAmountOwing())

        newContext.commitChanges()

        // check onPostUpdate
		assertEquals("Check AmountOwing: ", invoiceMoney.subtract(paymentMoney), invoice.getAmountOwing())

    }

	@Test
    void testAmountOwing3() throws Exception {
		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Money invoiceMoney = new Money("90")
        Money paymentMoney = new Money("40")

        Tax tax = Tax.getTaxWithCode("N", newContext)

        Invoice invoice = newContext.newObject(Invoice.class)

        Contact contact = newContext.newObject(Contact.class)
        contact.setFirstName("firstName3")
        contact.setLastName("lastName3")
        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)

        InvoiceLine il = newContext.newObject(InvoiceLine.class)
        il.setAccount(AccountUtil.getDefaultStudentEnrolmentsAccount(newContext, Account.class))
        il.setDescription("test line")
        il.setInvoice(invoice)
        il.setTax(newContext.localObject(tax))
        il.setPriceEachExTax(invoiceMoney)
        il.setDiscountEachExTax(Money.ZERO)
        il.setTitle("test title")
        il.setQuantity(BigDecimal.ONE)
        il.setTaxEach(Money.ZERO)
        il.setPrepaidFeesAccount(AccountUtil.getDefaultPrepaidFeesAccount(newContext, Account.class))

        invoice.setDebtorsAccount(AccountUtil.getDefaultDebtorsAccount(newContext, Account.class))

        PaymentIn paymentin = newContext.newObject(PaymentIn.class)
        paymentin.setPaymentDate(LocalDate.now())
        paymentin.setAmount(paymentMoney)
        paymentin.setPayer(contact)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(newContext, PaymentMethod.class, PaymentType.CASH), paymentin).set()
        paymentin.setAccountIn(AccountUtil.getDefaultBankAccount(newContext, Account.class))
        paymentin.setStatus(PaymentStatus.NEW)

        PaymentInLine paymentInLine = newContext.newObject(PaymentInLine.class)
        paymentInLine.setAmount(paymentMoney)
        paymentInLine.setAccountOut(AccountUtil.getDefaultDebtorsAccount(newContext, Account.class))
        paymentInLine.setPaymentIn(paymentin)

        invoice.addToPaymentInLines(paymentInLine)

        assertEquals("Check AmountOwing: ", Money.ZERO, invoice.getAmountOwing())

        newContext.commitChanges()

        // check onPostUpdate
		assertEquals("Check AmountOwing: ", invoiceMoney, invoice.getAmountOwing())

        paymentin.setStatus(PaymentStatus.SUCCESS)

        assertEquals("Check AmountOwing: ", invoiceMoney, invoice.getAmountOwing())

        newContext.commitChanges()

        // check onPostUpdate
		assertEquals("Check AmountOwing: ", invoiceMoney.subtract(paymentMoney), invoice.getAmountOwing())

    }

	@Test
    void testAmountOwing4() throws Exception {
		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Money invoiceMoney = new Money("90")
        Money paymentMoney = new Money("40")

        Tax tax = Tax.getTaxWithCode("N", newContext)

        Invoice invoice = newContext.newObject(Invoice.class)

        Contact contact = newContext.newObject(Contact.class)
        contact.setFirstName("firstName4")
        contact.setLastName("lastName4")
        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)

        InvoiceLine il = newContext.newObject(InvoiceLine.class)
        il.setAccount(AccountUtil.getDefaultStudentEnrolmentsAccount(newContext, Account.class))
        il.setDescription("test line")
        il.setInvoice(invoice)
        il.setTax(newContext.localObject(tax))
        il.setPriceEachExTax(invoiceMoney)
        il.setDiscountEachExTax(Money.ZERO)
        il.setTitle("test title")
        il.setQuantity(BigDecimal.ONE)
        il.setTaxEach(Money.ZERO)
        il.setPrepaidFeesAccount(AccountUtil.getDefaultPrepaidFeesAccount(newContext, Account.class))

        invoice.setDebtorsAccount(AccountUtil.getDefaultDebtorsAccount(newContext, Account.class))

        PaymentIn paymentin = newContext.newObject(PaymentIn.class)
        paymentin.setPaymentDate(LocalDate.now())
        paymentin.setAmount(paymentMoney)
        paymentin.setPayer(contact)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(newContext, PaymentMethod.class, PaymentType.CASH), paymentin).set()
        paymentin.setAccountIn(AccountUtil.getDefaultBankAccount(newContext, Account.class))
        paymentin.setStatus(PaymentStatus.NEW)

        PaymentInLine paymentInLine = newContext.newObject(PaymentInLine.class)
        paymentInLine.setAmount(paymentMoney)
        paymentInLine.setAccountOut(AccountUtil.getDefaultDebtorsAccount(newContext, Account.class))
        paymentInLine.setPaymentIn(paymentin)

        invoice.addToPaymentInLines(paymentInLine)

        assertEquals("Check AmountOwing: ", Money.ZERO, invoice.getAmountOwing())

        newContext.commitChanges()

        // check onPostUpdate
		assertEquals("Check AmountOwing: ", invoiceMoney, invoice.getAmountOwing())

        DataContext newContext2 = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Invoice invoiceInOtherContext = newContext2.localObject(invoice)
        PaymentIn paymentInOtherContext = newContext2.localObject(paymentin)

        paymentInOtherContext.setStatus(PaymentStatus.SUCCESS)

        assertEquals("Check AmountOwing: ", invoiceMoney, invoiceInOtherContext.getAmountOwing())

        newContext2.commitChanges()

        // check onPostUpdate
		assertEquals("Check AmountOwing: ", invoiceMoney.subtract(paymentMoney), invoiceInOtherContext.getAmountOwing())

    }

	@Test
    void testNextInvoiceNumber() {

		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Invoice invoice = newContext.newObject(Invoice.class)

        Contact contact = newContext.newObject(Contact.class)
        contact.setFirstName("firstName4")
        contact.setLastName("lastName4")
        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setAmountOwing(new Money("90"))

        invoice.setDebtorsAccount(AccountUtil.getDefaultDebtorsAccount(newContext, Account.class))

        newContext.commitChanges()

        assertNotNull("Check invoiceNumber ", invoice.getInvoiceNumber())

        Invoice invoice2 = newContext.newObject(Invoice.class)

        Contact contact2 = newContext.newObject(Contact.class)
        contact2.setFirstName("firstName5")
        contact2.setLastName("lastName5")
        invoice2.setContact(contact2)
        invoice2.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice2.setAmountOwing(new Money("90"))

        invoice2.setDebtorsAccount(AccountUtil.getDefaultDebtorsAccount(newContext, Account.class))

        newContext.commitChanges()

        assertEquals("Check invoiceNumbers ", 1, invoice2.getInvoiceNumber() - invoice.getInvoiceNumber())

    }
}
