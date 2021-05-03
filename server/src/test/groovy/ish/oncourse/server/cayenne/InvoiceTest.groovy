/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate

@CompileStatic
class InvoiceTest extends CayenneIshTestCase {

    
    @BeforeEach
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
        Assertions.assertNotNull(invoice.getInvoiceDate())
        Assertions.assertNotNull(invoice.getDateDue())
        Assertions.assertNotNull(invoice.getInvoiceNumber())

        // check onPostUpdate
        Assertions.assertEquals(invoiceMoney, invoice.getAmountOwing())

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

        Assertions.assertEquals(invoiceMoney, invoice.getAmountOwing())

        newContext.commitChanges()

        // check onPostUpdate
        Assertions.assertEquals(invoiceMoney.add(paymentMoney), invoice.getAmountOwing())

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
        Assertions.assertNotNull(invoice.getInvoiceDate())
        Assertions.assertNotNull(invoice.getDateDue())
        Assertions.assertNotNull(invoice.getInvoiceNumber())

        // check onPostUpdate
        Assertions.assertEquals(invoiceMoney, invoice.getAmountOwing())

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

        Assertions.assertEquals(invoiceMoney, invoice.getAmountOwing())

        newContext.commitChanges()

        // check onPostUpdate
        Assertions.assertEquals(invoiceMoney.subtract(paymentMoney), invoice.getAmountOwing())

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

        Assertions.assertEquals(Money.ZERO, invoice.getAmountOwing())

        newContext.commitChanges()

        // check onPostUpdate
        Assertions.assertEquals(invoiceMoney, invoice.getAmountOwing())

        paymentin.setStatus(PaymentStatus.SUCCESS)

        Assertions.assertEquals(invoiceMoney, invoice.getAmountOwing())

        newContext.commitChanges()

        // check onPostUpdate
        Assertions.assertEquals(invoiceMoney.subtract(paymentMoney), invoice.getAmountOwing())

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

        Assertions.assertEquals(Money.ZERO, invoice.getAmountOwing())

        newContext.commitChanges()

        // check onPostUpdate
        Assertions.assertEquals(invoiceMoney, invoice.getAmountOwing())

        DataContext newContext2 = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Invoice invoiceInOtherContext = newContext2.localObject(invoice)
        PaymentIn paymentInOtherContext = newContext2.localObject(paymentin)

        paymentInOtherContext.setStatus(PaymentStatus.SUCCESS)

        Assertions.assertEquals(invoiceMoney, invoiceInOtherContext.getAmountOwing())

        newContext2.commitChanges()

        // check onPostUpdate
        Assertions.assertEquals(invoiceMoney.subtract(paymentMoney), invoiceInOtherContext.getAmountOwing())

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

        Assertions.assertNotNull(invoice.getInvoiceNumber())

        Invoice invoice2 = newContext.newObject(Invoice.class)

        Contact contact2 = newContext.newObject(Contact.class)
        contact2.setFirstName("firstName5")
        contact2.setLastName("lastName5")
        invoice2.setContact(contact2)
        invoice2.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice2.setAmountOwing(new Money("90"))

        invoice2.setDebtorsAccount(AccountUtil.getDefaultDebtorsAccount(newContext, Account.class))

        newContext.commitChanges()

        Assertions.assertEquals(1, invoice2.getInvoiceNumber() - invoice.getInvoiceNumber())

    }
}
