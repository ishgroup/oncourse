/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.common.payable.PayableLineInterface
import ish.common.types.*
import ish.math.Money
import ish.oncourse.cayenne.PaymentInterface
import ish.oncourse.cayenne.PaymentLineInterface
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.generator.DataGenerator
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.access.DataContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.time.LocalDate

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class InvoiceUtilTest extends TestWithDatabase {

    private static final Logger logger = LogManager.getLogger()
    private static int codeSequence = 0

    @Test
    void testNewPaymentLineForInvoiceAndPayment() {
        Account account = cayenneContext.newObject(Account.class)
        account.setType(AccountType.ASSET)
        account.setAccountCode("11100")

        Invoice invoice = cayenneContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(account)
        PaymentIn pIn = cayenneContext.newObject(PaymentIn.class)
        PaymentOut pOut = cayenneContext.newObject(PaymentOut.class)

        PaymentInLine pInLine = (PaymentInLine) InvoiceUtil.paymentLineForInvoiceAndPayment(pIn, invoice)
        PaymentOutLine pOutLine = (PaymentOutLine) InvoiceUtil.paymentLineForInvoiceAndPayment(pOut, invoice)

        assertEquals(PaymentInLine.class, pInLine.getClass(), "Checking the payment line type")
        assertEquals(pIn, pInLine.getPaymentIn(), "Checking the payment in")
        assertEquals(invoice, pInLine.getInvoice(), "Checking the invoice")
        assertEquals(account, pInLine.getAccountOut(), "Checking the account")

        assertEquals(PaymentOutLine.class, pOutLine.getClass(), "Checking the payment line type")
        assertEquals(pOut, pOutLine.getPaymentOut(), "Checking the payment out")
        assertEquals(invoice, pOutLine.getInvoice(), "Checking the invoice")
        assertEquals(account, pOutLine.getAccountIn(), "Checking the account")

    }

    @Test
    void testGetSuccessfulPaymentLines() {
        DataContext cayenneContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
        Invoice invoice = cayenneContext.newObject(Invoice.class)
        List<? extends PaymentLineInterface> result = InvoiceUtil.getSuccessfulPaymentLines(invoice)
        Assertions.assertTrue(result.isEmpty(), "Empty list should be returned for invoice without linked payments")

        PaymentInLine pInLine = cayenneContext.newObject(PaymentInLine.class)
        pInLine.setInvoice(invoice)
        result = InvoiceUtil.getSuccessfulPaymentLines(invoice)
        Assertions.assertTrue(result.isEmpty(), "Empty list should be returned for invoice without linked payments to paymentinline")

        PaymentIn pIn = cayenneContext.newObject(PaymentIn.class)
        pInLine.setPaymentIn(pIn)

        result = InvoiceUtil.getSuccessfulPaymentLines(invoice)
        Assertions.assertTrue(result.isEmpty(), "Empty list should be returned for invoice without linked payments to paymentinline")

        pIn.setStatus(PaymentStatus.SUCCESS)
        result = InvoiceUtil.getSuccessfulPaymentLines(invoice)
        Assertions.assertFalse(result.isEmpty())
        assertEquals(1, result.size(), "1 paymentInLine should returns")
        Assertions.assertNotNull(result.get(0).getPayment(), "this paymentInLine should be linked with payment")
        Assertions.assertTrue(result.get(0).getPayment().isSuccess(), "this paymentInLine should be linked with success payment")
    }

    @Test
    void testExistingPaymentLineForInvoiceAndPayment() {
        Account account = cayenneContext.newObject(Account.class)
        account.setType(AccountType.ASSET)
        account.setAccountCode("11100")

        Invoice invoice = cayenneContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(account)
        Invoice invoice2 = cayenneContext.newObject(Invoice.class)
        invoice2.setDebtorsAccount(account)

        PaymentIn pIn = cayenneContext.newObject(PaymentIn.class)
        PaymentInLine pInLine = cayenneContext.newObject(PaymentInLine.class)
        pInLine.setInvoice(invoice)
        pInLine.setPaymentIn(pIn)

        assertEquals(pInLine, InvoiceUtil.paymentLineForInvoiceAndPayment(pIn, invoice), "Checking the uniqueness of payment in line")
        Assertions.assertNotEquals(pInLine, InvoiceUtil.paymentLineForInvoiceAndPayment(pIn, invoice2))
        Assertions.assertNotEquals(pInLine, InvoiceUtil.paymentLineForInvoiceAndPayment(cayenneContext.newObject(PaymentIn.class), invoice))

        PaymentOut pOut = cayenneContext.newObject(PaymentOut.class)
        PaymentOutLine pOutLine = cayenneContext.newObject(PaymentOutLine.class)
        pOutLine.setInvoice(invoice)
        pOutLine.setPaymentOut(pOut)

        assertEquals(pOutLine, InvoiceUtil.paymentLineForInvoiceAndPayment(pOut, invoice), "Checking the uniqueness of payment out line")
        Assertions.assertNotEquals(pInLine, InvoiceUtil.paymentLineForInvoiceAndPayment(pOut, invoice2))
        Assertions.assertNotEquals(pInLine, InvoiceUtil.paymentLineForInvoiceAndPayment(cayenneContext.newObject(PaymentOut.class), invoice))

    }

    @Test
    void testSuccessfullPaymentLines() {
        // commented out parts are for future evaluation, in case the behaviour after commit will change
        // Account aAsset = cayenneContext.newObject(Account.class);
        // aAsset.setType(AccountType.ASSET);

        Account aDebtors = cayenneContext.newObject(Account.class)
        aDebtors.setType(AccountType.ASSET)
        aDebtors.setAccountCode("11100")

        Invoice invoice = cayenneContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(aDebtors)

        // int i = 0;
        for (PaymentStatus ps : PaymentStatus.values()) {
            PaymentInLine pInLine = (PaymentInLine) InvoiceUtil.paymentLineForInvoiceAndPayment(cayenneContext.newObject(PaymentIn.class), invoice)
            // pInLine.getPaymentIn().setAccountIn(aAsset);
            // pInLine.getPaymentIn().setAmount(Money.valueOf(new BigDecimal("" + Math.pow(2, i))));
            pInLine.getPaymentIn().setStatus(ps)
            // i++;
        }

        // there is only one successful state
        assertEquals(1, InvoiceUtil.getSuccessfulPaymentLines(invoice).size(), "Checking the number of successfull payment lines")

        // cayenneContext.commitChanges();
        // assertEquals("Checking the number of successfull payment lines", 3, InvoiceUtil.getSuccessfulPaymentLines(invoice));
    }

    @Test
    void testSumPaymentLines() {

        ArrayList<PaymentLineInterface> list = new ArrayList<>()
        for (int i = 0; i < 8; i++) {
            PaymentIn pIn = cayenneContext.newObject(PaymentIn.class)
            PaymentInLine pInLine = cayenneContext.newObject(PaymentInLine.class)
            pInLine.setPaymentIn(pIn)
            // payments are powers of 2, every second is success
            pInLine.setAmount(Money.valueOf(new BigDecimal("" + Math.pow(2, i))))
            pInLine.getPaymentIn().setStatus(i % 2 == 0 ? PaymentStatus.FAILED : PaymentStatus.SUCCESS)
            list.add(pInLine)

            PaymentOut pOut = cayenneContext.newObject(PaymentOut.class)
            PaymentOutLine pOutLine = cayenneContext.newObject(PaymentOutLine.class)
            pOutLine.setPaymentOut(pOut)
            // payments are powers of 2, every second is success
            pOutLine.setAmount(Money.valueOf(new BigDecimal("" + Math.pow(2, i))))
            pOutLine.getPaymentOut().setStatus(i % 2 != 0 ? PaymentStatus.FAILED : PaymentStatus.SUCCESS)

            list.add(pOutLine)
        }

        Money sum1 = new Money("" + Integer.parseInt("10101010", 2))
        Money sum2 = new Money("" + Integer.parseInt("01010101", 2))

        assertEquals(sum1, InvoiceUtil.sumPaymentLines(list, PaymentInterface.TYPE_IN, true), "Checking sum of payment in lines")
        assertEquals(sum2, InvoiceUtil.sumPaymentLines(list, PaymentInterface.TYPE_OUT, true), "Checking sum of payment out lines")

        sum1 = sum2 = new Money("255")
        assertEquals(sum1, InvoiceUtil.sumPaymentLines(list, PaymentInterface.TYPE_IN, false), "Checking sum of payment in lines")
        assertEquals(sum2, InvoiceUtil.sumPaymentLines(list, PaymentInterface.TYPE_OUT, false), "Checking sum of payment out lines")

    }

    @Test
    void testSumInvoiceLinesNoTax() {
        Tax tax = cayenneContext.newObject(Tax.class)
        tax.setRate(BigDecimal.ZERO)

        ArrayList<PayableLineInterface> list = new ArrayList<>()

        InvoiceLine invoiceLine1 = cayenneContext.newObject(InvoiceLine.class)
        invoiceLine1.setTax(tax)
        invoiceLine1.setQuantity(BigDecimal.ONE)
        invoiceLine1.setPriceEachExTax(new Money("100"))
        invoiceLine1.setTaxEach(Money.ZERO)

        InvoiceLine invoiceLine2 = cayenneContext.newObject(InvoiceLine.class)
        invoiceLine2.setTax(tax)
        invoiceLine2.setQuantity(BigDecimal.ONE)
        invoiceLine2.setPriceEachExTax(new Money("200"))
        invoiceLine2.setTaxEach(Money.ZERO)

        list.add(invoiceLine1)
        list.add(invoiceLine2)

        Money expectedValueExTax = new Money("300")
        assertEquals(expectedValueExTax, InvoiceUtil.sumInvoiceLines(list, false), "Checking sum of invoice lines")
        assertEquals(expectedValueExTax, InvoiceUtil.sumInvoiceLines(list), "Checking sum of invoice lines")

        Money discount = new Money("10")
        invoiceLine1.setDiscountEachExTax(discount)
        expectedValueExTax = expectedValueExTax.subtract(discount)
        assertEquals(expectedValueExTax, InvoiceUtil.sumInvoiceLines(list, false), "Checking sum of invoice lines")
        assertEquals(expectedValueExTax, InvoiceUtil.sumInvoiceLines(list), "Checking sum of invoice lines")

    }

    @Test
    void testSumInvoiceLinesWithTax() {
        Tax tax = cayenneContext.newObject(Tax.class)
        tax.setRate(BigDecimal.valueOf(0.1d))

        ArrayList<PayableLineInterface> list = new ArrayList<>()

        InvoiceLine invoiceLine1 = cayenneContext.newObject(InvoiceLine.class)
        invoiceLine1.setTax(tax)
        invoiceLine1.setQuantity(BigDecimal.ONE)
        invoiceLine1.setPriceEachExTax(new Money("100"))
        invoiceLine1.setTaxEach(new Money("10"))

        InvoiceLine invoiceLine2 = cayenneContext.newObject(InvoiceLine.class)
        invoiceLine2.setTax(tax)
        invoiceLine2.setQuantity(BigDecimal.ONE)
        invoiceLine2.setPriceEachExTax(new Money("200"))
        invoiceLine2.setTaxEach(new Money("20"))

        list.add(invoiceLine1)
        list.add(invoiceLine2)

        Money expectedValueExTax = new Money("300")
        Money expectedValueIncTax = expectedValueExTax.multiply(BigDecimal.ONE.add(tax.getRate()))
        assertEquals(expectedValueExTax, InvoiceUtil.sumInvoiceLines(list, false), "Checking sum of invoice lines")
        assertEquals(expectedValueIncTax, InvoiceUtil.sumInvoiceLines(list), "Checking sum of invoice lines")

        Money discount = new Money("10")
        invoiceLine1.setDiscountEachExTax(discount)
        invoiceLine1.setTaxEach(invoiceLine1.getTaxEach().subtract(new Money("1")))
        expectedValueExTax = expectedValueExTax.subtract(discount)
        expectedValueIncTax = expectedValueExTax.multiply(BigDecimal.ONE.add(tax.getRate()))
        assertEquals(expectedValueExTax, InvoiceUtil.sumInvoiceLines(list, false), "Checking sum of invoice lines")
        assertEquals(expectedValueIncTax, InvoiceUtil.sumInvoiceLines(list), "Checking sum of invoice lines")

    }

    @Test
    void testUpdateAmountOwingWithoutCommits() {
        Account aDebtors = cayenneContext.newObject(Account.class)
        aDebtors.setType(AccountType.ASSET)
        aDebtors.setAccountCode("11100")

        Contact contact = cayenneContext.newObject(Contact.class)

        Invoice invoice = cayenneContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(aDebtors)
        invoice.setContact(contact)

        Tax tax = cayenneContext.newObject(Tax.class)
        tax.setRate(BigDecimal.valueOf(0.1d))

        InvoiceLine invoiceLine = cayenneContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setPriceEachExTax(new Money("100"))
        invoiceLine.setTaxEach(new Money("10"))
        invoiceLine.setInvoice(invoice)

        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals(new Money("110"), invoice.getAmountOwing(), "Checking amount owing")

        Tax tax2 = cayenneContext.newObject(Tax.class)
        tax2.setRate(BigDecimal.ZERO)

        InvoiceLine invoiceLine2 = cayenneContext.newObject(InvoiceLine.class)
        invoiceLine2.setTax(tax2)
        invoiceLine2.setQuantity(BigDecimal.valueOf(5d))
        invoiceLine2.setPriceEachExTax(new Money("50"))
        invoiceLine2.setInvoice(invoice)

        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals(new Money("360"), invoice.getAmountOwing(), "Checking amount owing")

        PaymentIn payment = cayenneContext.newObject(PaymentIn.class)
        payment.setAccountIn(aDebtors)
        payment.setAmount(new Money("100"))
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.CASH), payment).set()
        payment.setStatus(PaymentStatus.SUCCESS)
        payment.setPayer(contact)

        PaymentInLine pinLine = cayenneContext.newObject(PaymentInLine.class)
        pinLine.setPayment(payment)
        invoice.addToPaymentInLines(pinLine)
        pinLine.setAmount(new Money("100"))

        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals(new Money("260"), invoice.getAmountOwing(), "Checking amount owing")

        payment.setAmount(new Money("360"))
        pinLine.setAmount(new Money("360"))

        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals(Money.ZERO, invoice.getAmountOwing(), "Checking amount owing")

    }

    @Test
    void testUpdateAmountOwingWithCommits() {
        Account account = cayenneContext.newObject(Account.class)
        account.setType(AccountType.ASSET)
        account.setAccountCode("11100")
        account.setIsEnabled(true)
        account.setDescription("testUpdateAmountOwingWithCommits")

        Account account3 = cayenneContext.newObject(Account.class)
        account3.setType(AccountType.LIABILITY)
        account3.setAccountCode("21010")
        account3.setIsEnabled(true)
        account3.setDescription("testUpdateAmountOwingWithCommits2")
        //commit accounts first than link to taxes (avoid exception with circular dependency on tables)
        cayenneContext.commitChanges()

        Contact contact = cayenneContext.newObject(Contact.class)
        contact.setFirstName("testUpdateAmountOwingWithCommits")
        contact.setLastName("testUpdateAmountOwingWithCommits")

        Invoice invoice = cayenneContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(account)
        invoice.setContact(contact)
        invoice.setAmountOwing(Money.ZERO)
        invoice.setAllowAutoPay(Boolean.FALSE)

        Tax tax = cayenneContext.newObject(Tax.class)
        tax.setRate(BigDecimal.valueOf(0.1d))
        tax.setPayableToAccount(account)
        tax.setTaxCode("GST")
        tax.setReceivableFromAccount(account)

        InvoiceLine invoiceLine = cayenneContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setPriceEachExTax(new Money("100"))
        invoiceLine.setDiscountEachExTax(Money.ZERO)
        invoiceLine.setTaxEach(new Money("10"))
        invoiceLine.setInvoice(invoice)
        invoiceLine.setTitle("test")
        invoiceLine.setAccount(account)
        invoiceLine.setPrepaidFeesAccount(account3)

        cayenneContext.commitChanges()
        assertEquals(new Money("110"), invoice.getAmountOwing(), "Checking amount owing")

        Tax tax2 = cayenneContext.newObject(Tax.class)
        tax2.setRate(BigDecimal.ZERO)
        tax2.setPayableToAccount(account)
        tax2.setTaxCode("N")
        tax2.setReceivableFromAccount(account)

        InvoiceLine invoiceLine2 = cayenneContext.newObject(InvoiceLine.class)
        invoiceLine2.setTax(tax2)
        invoiceLine2.setQuantity(BigDecimal.valueOf(5d))
        invoiceLine2.setPriceEachExTax(new Money("50"))
        invoiceLine2.setDiscountEachExTax(Money.ZERO)
        invoiceLine2.setTaxEach(new Money("0"))
        invoiceLine2.setInvoice(invoice)
        invoiceLine2.setTitle("test2")
        invoiceLine2.setAccount(account)
        invoiceLine2.setPrepaidFeesAccount(account3)

        cayenneContext.commitChanges()
        assertEquals(new Money("360"), invoice.getAmountOwing(), "Checking amount owing")

        Account account2 = cayenneContext.newObject(Account.class)
        account2.setType(AccountType.ASSET)
        account2.setAccountCode("21100")
        account2.setIsEnabled(true)
        account2.setDescription("testUpdateAmountOwingWithCommits - asset")

        PaymentIn payment = cayenneContext.newObject(PaymentIn.class)
        payment.setPaymentDate(LocalDate.now())
        payment.setAccountIn(account2)
        payment.setAmount(new Money("100"))
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.CASH), payment).set()
        payment.setStatus(PaymentStatus.SUCCESS)
        payment.setPayer(contact)

        PaymentInLine pinLine = cayenneContext.newObject(PaymentInLine.class)
        pinLine.setPayment(payment)
        invoice.addToPaymentInLines(pinLine)
        // pinLine.setInvoice(invoice);
        pinLine.setAmount(new Money("100"))
        pinLine.setAccountOut(account2)

        cayenneContext.commitChanges()
        assertEquals(new Money("260"), invoice.getAmountOwing(), "Checking amount owing")

        PaymentIn payment2 = cayenneContext.newObject(PaymentIn.class)
        payment2.setPaymentDate(LocalDate.now())
        payment2.setAccountIn(account2)
        payment2.setAmount(new Money("260"))
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.CASH), payment2).set()
        payment2.setStatus(PaymentStatus.SUCCESS)
        payment2.setPayer(contact)

        PaymentInLine pinLine2 = cayenneContext.newObject(PaymentInLine.class)
        pinLine2.setPayment(payment2)
        // pinLine2.setInvoice(invoice);
        invoice.addToPaymentInLines(pinLine2)
        pinLine2.setAmount(new Money("260"))
        pinLine2.setAccountOut(account2)

        cayenneContext.commitChanges()
        assertEquals(Money.ZERO, invoice.getAmountOwing(), "Checking amount owing")

    }

    @Test
    void testUpdateAmountOwingWithoutCommitsPaymentInAndOut() {
        Account account = cayenneContext.newObject(Account.class)
        account.setType(AccountType.ASSET)
        account.setAccountCode("11100")
        account.setIsEnabled(true)
        account.setDescription("testUpdateAmountOwingWithCommits")

        Account account3 = cayenneContext.newObject(Account.class)
        account3.setType(AccountType.LIABILITY)
        account3.setAccountCode("21010")
        account3.setIsEnabled(true)
        account3.setDescription("testUpdateAmountOwingWithCommits2")

        Contact contact = cayenneContext.newObject(Contact.class)
        contact.setFirstName("testUpdateAmountOwingWithCommits")
        contact.setLastName("testUpdateAmountOwingWithCommits")

        Invoice invoice = cayenneContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(account)
        invoice.setContact(contact)
        invoice.setAmountOwing(Money.ZERO)

        Tax tax = cayenneContext.newObject(Tax.class)
        tax.setRate(BigDecimal.valueOf(0.1d))
        tax.setPayableToAccount(account)
        tax.setTaxCode("GST")
        tax.setReceivableFromAccount(account)

        InvoiceLine invoiceLine = cayenneContext.newObject(InvoiceLine.class)
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
        assertEquals(new Money("110"), invoice.getAmountOwing(), "Checking amount owing")

        Tax tax2 = cayenneContext.newObject(Tax.class)
        tax2.setRate(BigDecimal.ZERO)
        tax2.setPayableToAccount(account)
        tax2.setTaxCode("N")
        tax2.setReceivableFromAccount(account)

        InvoiceLine invoiceLine2 = cayenneContext.newObject(InvoiceLine.class)
        invoiceLine2.setTax(tax2)
        invoiceLine2.setQuantity(BigDecimal.valueOf(5d))
        invoiceLine2.setPriceEachExTax(new Money("50"))
        invoiceLine2.setDiscountEachExTax(Money.ZERO)
        invoiceLine2.setInvoice(invoice)
        invoiceLine2.setTitle("test2")
        invoiceLine2.setAccount(account)
        invoiceLine2.setPrepaidFeesAccount(account3)

        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals(new Money("360"), invoice.getAmountOwing(), "Checking amount owing")

        Account account2 = cayenneContext.newObject(Account.class)
        account2.setType(AccountType.ASSET)
        account2.setAccountCode("21100")
        account2.setIsEnabled(true)
        account2.setDescription("testUpdateAmountOwingWithCommits - asset")

        PaymentIn payment = cayenneContext.newObject(PaymentIn.class)
        payment.setAccountIn(account2)
        payment.setAmount(new Money("460"))
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.CASH), payment).set()
        payment.setStatus(PaymentStatus.SUCCESS)
        payment.setPayer(contact)

        PaymentInLine pinLine = cayenneContext.newObject(PaymentInLine.class)
        pinLine.setPayment(payment)
        invoice.addToPaymentInLines(pinLine)
        // pinLine.setInvoice(invoice);
        pinLine.setAmount(new Money("460"))
        pinLine.setAccountOut(account2)

        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals(new Money("100").negate(), invoice.getAmountOwing(), "Checking amount owing")

        PaymentOut payment2 = cayenneContext.newObject(PaymentOut.class)
        payment2.setAccountOut(account2)
        payment2.setAmount(new Money("100"))
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.CASH), payment2).set()
        payment2.setStatus(PaymentStatus.SUCCESS)
        payment2.setPayee(contact)

        PaymentOutLine pinLine2 = cayenneContext.newObject(PaymentOutLine.class)
        pinLine2.setPayment(payment2)
        // pinLine2.setInvoice(invoice);
        invoice.addToPaymentOutLines(pinLine2)
        pinLine2.setAmount(new Money("100"))
        pinLine2.setAccountIn(account2)

        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals(Money.ZERO, invoice.getAmountOwing(), "Checking amount owing")

    }

    @Test
    void testUpdateAmountOwingWithCommitsPaymentInAndOut() {
        Account account = cayenneContext.newObject(Account.class)
        account.setType(AccountType.ASSET)
        account.setAccountCode("11100")
        account.setIsEnabled(true)
        account.setDescription("testUpdateAmountOwingWithCommits")

        Account account3 = cayenneContext.newObject(Account.class)
        account3.setType(AccountType.LIABILITY)
        account3.setAccountCode("21010")
        account3.setIsEnabled(true)
        account3.setDescription("testUpdateAmountOwingWithCommits2")
        //commit accounts first than link to taxes (avoid exception with circular dependency on tables)
        cayenneContext.commitChanges()

        Contact contact = cayenneContext.newObject(Contact.class)
        contact.setFirstName("testUpdateAmountOwingWithCommits")
        contact.setLastName("testUpdateAmountOwingWithCommits")

        Invoice invoice = cayenneContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(account)
        invoice.setContact(contact)
        invoice.setAmountOwing(Money.ZERO)
        invoice.setAllowAutoPay(Boolean.FALSE)

        Tax tax = cayenneContext.newObject(Tax.class)
        tax.setRate(BigDecimal.valueOf(0.1d))
        tax.setPayableToAccount(account)
        tax.setTaxCode("GST")
        tax.setReceivableFromAccount(account)

        InvoiceLine invoiceLine = cayenneContext.newObject(InvoiceLine.class)
        invoiceLine.setTax(tax)
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setPriceEachExTax(new Money("100"))
        invoiceLine.setDiscountEachExTax(Money.ZERO)
        invoiceLine.setTaxEach(new Money("10"))
        invoiceLine.setInvoice(invoice)
        invoiceLine.setTitle("test")
        invoiceLine.setAccount(account)
        invoiceLine.setPrepaidFeesAccount(account3)

        cayenneContext.commitChanges()
        assertEquals(new Money("110"), invoice.getAmountOwing(), "Checking amount owing")

        Tax tax2 = cayenneContext.newObject(Tax.class)
        tax2.setRate(BigDecimal.ZERO)
        tax2.setPayableToAccount(account)
        tax2.setTaxCode("N")
        tax2.setReceivableFromAccount(account)

        InvoiceLine invoiceLine2 = cayenneContext.newObject(InvoiceLine.class)
        invoiceLine2.setTax(tax2)
        invoiceLine2.setQuantity(BigDecimal.valueOf(5d))
        invoiceLine2.setPriceEachExTax(new Money("50"))
        invoiceLine2.setDiscountEachExTax(Money.ZERO)
        invoiceLine2.setTaxEach(new Money("0"))
        invoiceLine2.setInvoice(invoice)
        invoiceLine2.setTitle("test2")
        invoiceLine2.setAccount(account)
        invoiceLine2.setPrepaidFeesAccount(account3)

        cayenneContext.commitChanges()
        assertEquals(new Money("360"), invoice.getAmountOwing(), "Checking amount owing")

        Account account2 = cayenneContext.newObject(Account.class)
        account2.setType(AccountType.ASSET)
        account2.setAccountCode("21100")
        account2.setIsEnabled(true)
        account2.setDescription("testUpdateAmountOwingWithCommits - asset")

        PaymentIn payment = cayenneContext.newObject(PaymentIn.class)
        payment.setPaymentDate(LocalDate.now())
        payment.setAccountIn(account2)
        payment.setAmount(new Money("460"))
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.CASH), payment).set()
        payment.setStatus(PaymentStatus.SUCCESS)
        payment.setPayer(contact)

        PaymentInLine pinLine = cayenneContext.newObject(PaymentInLine.class)
        pinLine.setPayment(payment)
        invoice.addToPaymentInLines(pinLine)
        // pinLine.setInvoice(invoice);
        pinLine.setAmount(new Money("460"))
        pinLine.setAccountOut(account2)

        cayenneContext.commitChanges()
        assertEquals(new Money("100").negate(), invoice.getAmountOwing(), "Checking amount owing")

        PaymentOut payment2 = cayenneContext.newObject(PaymentOut.class)
        payment2.setPaymentDate(LocalDate.now())
        payment2.setAccountOut(account2)
        payment2.setAmount(new Money("100"))
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.CASH), payment2).set()
        payment2.setStatus(PaymentStatus.SUCCESS)
        payment2.setPayee(contact)

        PaymentOutLine pinLine2 = cayenneContext.newObject(PaymentOutLine.class)
        pinLine2.setPayment(payment2)
        // pinLine2.setInvoice(invoice);
        invoice.addToPaymentOutLines(pinLine2)
        pinLine2.setAmount(new Money("100"))
        pinLine2.setAccountIn(account2)

        cayenneContext.commitChanges()
        assertEquals(Money.ZERO, invoice.getAmountOwing(), "Checking amount owing")

    }

    @Test
    void testAllocateSingleInvoice() {
        // setup required accounts, taxes
        Account aDebtors = cayenneContext.newObject(Account.class)
        aDebtors.setType(AccountType.ASSET)
        aDebtors.setAccountCode("123")
        aDebtors.setDescription("123")
        aDebtors.setIsEnabled(true)

        Account taxAccount = cayenneContext.newObject(Account.class)
        taxAccount.setType(AccountType.LIABILITY)
        taxAccount.setAccountCode("1234")
        taxAccount.setDescription("1234")
        taxAccount.setIsEnabled(true)

        Account aIncome = cayenneContext.newObject(Account.class)
        aIncome.setType(AccountType.ASSET)
        aIncome.setAccountCode("12345")
        aIncome.setDescription("12345")
        aIncome.setIsEnabled(true)

        Account accountIn = cayenneContext.newObject(Account.class)
        accountIn.setType(AccountType.ASSET)
        accountIn.setAccountCode("12345")
        accountIn.setDescription("12345")
        accountIn.setIsEnabled(true)

        Account account3 = cayenneContext.newObject(Account.class)
        account3.setType(AccountType.LIABILITY)
        account3.setAccountCode("21010")
        account3.setIsEnabled(true)
        account3.setDescription("testUpdateAmountOwingWithCommits2")
        //commit accounts first than link to taxes (avoid exception with circular dependency on tables)
        cayenneContext.commitChanges()

        Tax tax = cayenneContext.newObject(Tax.class)
        tax.setRate(BigDecimal.valueOf(0.1d))
        tax.setReceivableFromAccount(taxAccount)
        tax.setPayableToAccount(taxAccount)
        tax.setTaxCode("G")

        Contact contact = cayenneContext.newObject(Contact.class)
        contact.setFirstName("Albert")
        contact.setLastName("Einstein")
        cayenneContext.commitChanges()

        // this is the invoice we are going to test
        Invoice invoice = cayenneContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(aDebtors)
        invoice.setContact(contact)
        invoice.setAllowAutoPay(Boolean.FALSE)

        // ... and its invoice line
        InvoiceLine invoiceLine = cayenneContext.newObject(InvoiceLine.class)
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
        PaymentIn payment = cayenneContext.newObject(PaymentIn.class)
        payment.setPaymentDate(LocalDate.now())
        payment.setStatus(PaymentStatus.SUCCESS)
        payment.setPayer(contact)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(cayenneContext, PaymentMethod.class), payment).set()
        payment.setAccountIn(accountIn)

        ArrayList<PaymentLineInterface> list = new ArrayList<>()

        // first user chooses to pay invoice in full
        Money result = InvoiceUtil.invoiceAllocate(invoice, new Money("110"), payment, list, false)
        assertEquals(new Money("110"), result, "Checking money allocation for invoice")
        assertEquals(1, invoice.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(new Money("110"), payment.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")

        // then just half
        result = InvoiceUtil.invoiceAllocate(invoice, new Money("55"), payment, list, false)
        assertEquals(new Money("55"), result, "Checking money allocation for invoice")
        assertEquals(1, invoice.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(new Money("55"), payment.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")

        // then actually types some wrong amount
        result = InvoiceUtil.invoiceAllocate(invoice, new Money("150"), payment, list, false)
        assertEquals(new Money("110"), result, "Checking money allocation for invoice")
        assertEquals(1, invoice.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(new Money("110"), payment.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")

        // then removes the payment alltogether
        InvoiceUtil.invoiceDeallocate(invoice, payment, list)

        assertEquals(0, invoice.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(0, payment.getPaymentLines().size(), "Checking number of payment lines")

        // then creates a $0 payment
        result = InvoiceUtil.invoiceAllocate(invoice, Money.ZERO, payment, list, false)
        assertEquals(Money.ZERO, result, "Checking money allocation for invoice")
        assertEquals(1, invoice.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(Money.ZERO, payment.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")

        // removes it again
        InvoiceUtil.invoiceDeallocate(invoice, payment, list)

        // finally create a payment for half the invoice
        result = InvoiceUtil.invoiceAllocate(invoice, new Money("55"), payment, list, false)
        assertEquals(new Money("55"), result, "Checking money allocation for invoice")
        assertEquals(1, invoice.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(new Money("55"), payment.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")

        payment.setAmount(payment.getPaymentInLines().get(0).getAmount())

        // save
        cayenneContext.commitChanges()

        // confirm the values are ok
        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals(new Money("55"), result, "Checking money allocation for invoice")
        assertEquals(new Money("55"), invoice.getAmountOwing(), "Checking money owing for invoice")
        assertEquals(1, invoice.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(new Money("55"), payment.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")

        // editing existing payment line should not be allowed
        try {
            result = InvoiceUtil.invoiceAllocate(invoice, new Money("10"), payment, list, false)
            Assertions.fail("The editing of existing payment line should be forbidden")
        } catch (IllegalStateException ex) {
            assertEquals(ex.getMessage(), "The amount can be only altered for a new payment line.")
        }

        // verify the values again
        assertEquals(new Money("55"), invoice.getAmountOwing(), "Checking money owing for invoice")
        assertEquals(1, invoice.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(new Money("55"), payment.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")

        // create second payment
        PaymentIn payment2 = cayenneContext.newObject(PaymentIn.class)
        payment2.setPaymentDate(LocalDate.now())
        payment2.setStatus(PaymentStatus.SUCCESS)
        payment2.setPayer(contact)
        payment2.setAccountIn(accountIn)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.CASH), payment2).set()
        ArrayList<PaymentLineInterface> list2 = new ArrayList<>()

        // first just small
        result = InvoiceUtil.invoiceAllocate(invoice, new Money("11"), payment2, list2, false)
        assertEquals(new Money("11"), result, "Checking money allocation for invoice")
        assertEquals(2, invoice.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment2.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(new Money("55"), payment.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")
        assertEquals(new Money("11"), payment2.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")

        // then $0
        result = InvoiceUtil.invoiceAllocate(invoice, Money.ZERO, payment2, list2, false)
        assertEquals(Money.ZERO, result, "Checking money allocation for invoice")
        assertEquals(2, invoice.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment2.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(new Money("55"), payment.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")
        assertEquals(Money.ZERO, payment2.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")

        // then removes the payment alltogether
        InvoiceUtil.invoiceDeallocate(invoice, payment2, list2)

        assertEquals(1, invoice.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(0, payment2.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(new Money("55"), payment.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")

        // then pay second half
        result = InvoiceUtil.invoiceAllocate(invoice, new Money("55"), payment2, list2, false)
        assertEquals(new Money("55"), result, "Checking money allocation for invoice")
        assertEquals(2, invoice.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment2.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(new Money("55"), payment.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")
        assertEquals(new Money("55"), payment2.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")

        payment2.setAmount(payment2.getPaymentInLines().get(0).getAmount())

        // save
        cayenneContext.commitChanges()

        // verify all
        InvoiceUtil.updateAmountOwing(invoice)
        assertEquals(Money.ZERO, invoice.getAmountOwing(), "Checking money owing for invoice")
        assertEquals(2, invoice.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(1, payment2.getPaymentLines().size(), "Checking number of payment lines")
        assertEquals(new Money("55"), payment.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")
        assertEquals(new Money("55"), payment2.getPaymentLines().get(0).getAmount(), "Checking money allocation for paymentline")
    }

    @Test
    void testAllocateMultipleInvoices() {
        // setup required accounts, taxes
        Account aDebtors = cayenneContext.newObject(Account.class)
        aDebtors.setType(AccountType.ASSET)
        aDebtors.setAccountCode("123")
        aDebtors.setDescription("123")
        aDebtors.setIsEnabled(true)

        Account taxAccount = cayenneContext.newObject(Account.class)
        taxAccount.setType(AccountType.LIABILITY)
        taxAccount.setAccountCode("1234")
        taxAccount.setDescription("1234")
        taxAccount.setIsEnabled(true)

        Account aIncome = cayenneContext.newObject(Account.class)
        aIncome.setType(AccountType.ASSET)
        aIncome.setAccountCode("12345")
        aIncome.setDescription("12345")
        aIncome.setIsEnabled(true)

        Account accountIn = cayenneContext.newObject(Account.class)
        accountIn.setType(AccountType.ASSET)
        accountIn.setAccountCode("12345")
        accountIn.setDescription("12345")
        accountIn.setIsEnabled(true)

        Account account3 = cayenneContext.newObject(Account.class)
        account3.setType(AccountType.LIABILITY)
        account3.setAccountCode("21010")
        account3.setIsEnabled(true)
        account3.setDescription("testUpdateAmountOwingWithCommits2")
        //commit accounts first than link to taxes (avoid exception with circular dependency on tables)
        cayenneContext.commitChanges()

        Tax tax = cayenneContext.newObject(Tax.class)
        tax.setRate(BigDecimal.valueOf(0.1d))
        tax.setReceivableFromAccount(taxAccount)
        tax.setPayableToAccount(taxAccount)
        tax.setTaxCode("G")

        Contact contact = cayenneContext.newObject(Contact.class)
        contact.setFirstName("Marie")
        contact.setLastName("Sklodowska-Curie")

        // first invoice
        Invoice invoice = cayenneContext.newObject(Invoice.class)
        invoice.setDebtorsAccount(aDebtors)
        invoice.setContact(contact)
        invoice.setAllowAutoPay(Boolean.FALSE)

        Enrolment enrl = createEnrolment(cayenneContext, aIncome, tax)
        enrl.setStatus(EnrolmentStatus.IN_TRANSACTION)
        // ... and its invoice line
        InvoiceLine invoiceLine = cayenneContext.newObject(InvoiceLine.class)
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
        Invoice invoice2 = cayenneContext.newObject(Invoice.class)
        invoice2.setDebtorsAccount(aDebtors)
        invoice2.setContact(contact)
        invoice2.setAllowAutoPay(Boolean.FALSE)

        Enrolment enrolment = createEnrolment(cayenneContext, aIncome, tax)
        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        // ... and its invoice line
        InvoiceLine invoiceLine2 = cayenneContext.newObject(InvoiceLine.class)
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
        cayenneContext.commitChanges()

        assertEquals(new Money("220"), InvoiceUtil.amountOwingForPayer(contact), "Checking the amount owing")

        // lets create first payment
        PaymentIn payment = cayenneContext.newObject(PaymentIn.class)
        payment.setPaymentDate(LocalDate.now())
        payment.setStatus(PaymentStatus.SUCCESS)
        payment.setPayer(contact)
        payment.setAccountIn(accountIn)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.CASH), payment).set()
        payment.setAmount(new Money("150"))

        ArrayList<PaymentLineInterface> list = new ArrayList<>()

        Money result = InvoiceUtil.allocateMoneyToInvoices(payment.getAmount(), contact.getInvoices(), payment, list)
        assertEquals(Money.ZERO, result, "Checking money left over")
        assertEquals(1, invoice.getPaymentLines().size(), "Checking number of invoice payment lines")
        assertEquals(1, invoice2.getPaymentLines().size(), "Checking number of invoice payment lines")
        assertEquals(2, payment.getPaymentLines().size(), "Checking number of payment lines")
        PaymentInLine pLine1 = payment.getPaymentInLines().get(0)
        PaymentInLine pLine2 = payment.getPaymentInLines().get(1)
        if (pLine1.getAmount().compareTo(pLine2.getAmount()) > 0) {
            assertEquals(new Money("110"), pLine1.getAmount(), "Checking money allocation for paymentline")
            assertEquals(new Money("40"), pLine2.getAmount(), "Checking money allocation for paymentline")
        } else {
            assertEquals(new Money("110"), pLine2.getAmount(), "Checking money allocation for paymentline")
            assertEquals(new Money("40"), pLine1.getAmount(), "Checking money allocation for paymentline")
        }
        assertEquals(new Money("150"), payment.getAmount(), "Checking payment total")
        assertEquals(new Money("70"), InvoiceUtil.amountOwingForPayer(contact), "Checking the amount owing")

        // save...
        cayenneContext.commitChanges()
        // and verify again
        assertEquals(Money.ZERO, result, "Checking money left over")
        assertEquals(1, invoice.getPaymentLines().size(), "Checking number of invoice payment lines")
        assertEquals(1, invoice2.getPaymentLines().size(), "Checking number of invoice payment lines")
        assertEquals(2, payment.getPaymentLines().size(), "Checking number of payment lines")
        pLine1 = payment.getPaymentInLines().get(0)
        pLine2 = payment.getPaymentInLines().get(1)
        if (pLine1.getAmount().compareTo(pLine2.getAmount()) > 0) {
            assertEquals(new Money("110"), pLine1.getAmount(), "Checking money allocation for paymentline")
            assertEquals(new Money("40"), pLine2.getAmount(), "Checking money allocation for paymentline")
        } else {
            assertEquals(new Money("110"), pLine2.getAmount(), "Checking money allocation for paymentline")
            assertEquals(new Money("40"), pLine1.getAmount(), "Checking money allocation for paymentline")
        }
        assertEquals(new Money("150"), payment.getAmount(), "Checking payment total")
        assertEquals(new Money("70"), InvoiceUtil.amountOwingForPayer(contact), "Checking the amount owing")

        // now third invoice - credit note
        Invoice invoice3 = cayenneContext.newObject(Invoice.class)
        invoice3.setDebtorsAccount(aDebtors)
        invoice3.setContact(contact)
        invoice3.setAllowAutoPay(Boolean.FALSE)

        Enrolment enrl3 = createEnrolment(cayenneContext, aIncome, tax)
        enrl3.setStatus(EnrolmentStatus.NEW)
        // ... and its invoice line
        InvoiceLine invoiceLine3 = cayenneContext.newObject(InvoiceLine.class)
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
        PaymentIn payment2 = cayenneContext.newObject(PaymentIn.class)
        payment2.setPaymentDate(LocalDate.now())
        payment2.setStatus(PaymentStatus.SUCCESS)
        payment2.setPayer(contact)
        payment2.setAccountIn(accountIn)
        payment2.setAmount(Money.ZERO)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getINTERNALPaymentMethods(cayenneContext, PaymentMethod.class), payment2).set()

        ArrayList<PaymentLineInterface> list2 = new ArrayList<>()

        result = InvoiceUtil.allocateMoneyToInvoices(payment2.getAmount(), contact.getInvoices(), payment2, list2)
        assertEquals(Money.ZERO, result, "Checking money left over")
        assertEquals(1, invoice.getPaymentLines().size(), "Checking number of invoice payment lines")
        assertEquals(2, invoice2.getPaymentLines().size(), "Checking number of invoice payment lines")
        assertEquals(1, invoice3.getPaymentLines().size(), "Checking number of invoice payment lines")
        assertEquals(2, payment2.getPaymentLines().size(), "Checking number of payment lines")
        pLine1 = payment2.getPaymentInLines().get(0)
        pLine2 = payment2.getPaymentInLines().get(1)
        if (pLine1.getAmount().compareTo(pLine2.getAmount()) > 0) {
            assertEquals(new Money("70"), pLine1.getAmount(), "Checking money allocation for paymentline")
            assertEquals(new Money("-70"), pLine2.getAmount(), "Checking money allocation for paymentline")
        } else {
            assertEquals(new Money("70"), pLine2.getAmount(), "Checking money allocation for paymentline")
            assertEquals(new Money("-70"), pLine1.getAmount(), "Checking money allocation for paymentline")
        }
        assertEquals(Money.ZERO, payment2.getAmount(), "Checking payment total")
        assertEquals(Money.ZERO, InvoiceUtil.amountOwingForPayer(contact), "Checking the amount owing")

        // save...
        cayenneContext.commitChanges()
        // and verify again
        assertEquals(Money.ZERO, result, "Checking money left over")
        assertEquals(1, invoice.getPaymentLines().size(), "Checking number of invoice payment lines")
        assertEquals(2, invoice2.getPaymentLines().size(), "Checking number of invoice payment lines")
        assertEquals(1, invoice3.getPaymentLines().size(), "Checking number of invoice payment lines")
        assertEquals(2, payment2.getPaymentLines().size(), "Checking number of payment lines")
        pLine1 = payment2.getPaymentInLines().get(0)
        pLine2 = payment2.getPaymentInLines().get(1)
        if (pLine1.getAmount().compareTo(pLine2.getAmount()) > 0) {
            assertEquals(new Money("70"), pLine1.getAmount(), "Checking money allocation for paymentline")
            assertEquals(new Money("-70"), pLine2.getAmount(), "Checking money allocation for paymentline")
        } else {
            assertEquals(new Money("70"), pLine2.getAmount(), "Checking money allocation for paymentline")
            assertEquals(new Money("-70"), pLine1.getAmount(), "Checking money allocation for paymentline")
        }
        assertEquals(Money.ZERO, payment2.getAmount(), "Checking payment total")
        assertEquals(Money.ZERO, InvoiceUtil.amountOwingForPayer(contact), "Checking the amount owing")
    }

    private static Enrolment createEnrolment(ObjectContext cayenneContext, Account account, Tax tax) {

        Course course = cayenneContext.newObject(Course.class)
        course.setCode("AABBDD" + codeSequence++)
        course.setName("courseName")
        course.setFieldConfigurationSchema(DataGenerator.valueOf(cayenneContext).getFieldConfigurationScheme())
        course.setFeeHelpClass(Boolean.FALSE)

        CourseClass cc = cayenneContext.newObject(CourseClass.class)
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

        Contact contact = cayenneContext.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Student student = cayenneContext.newObject(Student.class)
        contact.setStudent(student)

        Enrolment enrl = cayenneContext.newObject(Enrolment.class)
        enrl.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrl.setStudent(student)
        enrl.setCourseClass(cc)

        return enrl
    }

}
