/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne


import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.CreditCardType
import ish.common.types.PaymentSource
import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.ICayenneService
import ish.util.PaymentMethodUtil
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.validation.ValidationException
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.text.ParseException
import java.time.LocalDate
import java.time.Month

@CompileStatic
class EffectiveDatesServerTest extends CayenneIshTestCase {

    private static final Logger logger = LogManager.getLogger()
    private DataContext context

    
    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        injector.getInstance(ICayenneService.class)

        InputStream st = EffectiveDatesServerTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/cayenne/EffectiveDatesServerTestDataSet.xml")

        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
        builder.setColumnSensing(true)
        FlatXmlDataSet dataSet = builder.build(st)

        executeDatabaseOperation(dataSet)
        super.setup()

        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        context = cayenneService.getNewContext()
    }

    
    @Test
    void testInvoice() throws ParseException {

        fillInvoiceWitDate(LocalDate.of(2015, Month.DECEMBER, 30))

        try {
            context.commitChanges()
            Assertions.fail()
        } catch (Exception ex) {
            checkError(ex)
        }

        context.rollbackChanges()
        fillInvoiceWitDate(LocalDate.now())
        context.commitChanges()
        checkTransactions()

    }


    @Test
    void testPaymentIn() {

        fillPaymentIn()

        context.commitChanges()
        checkTransactions()

    }

    @Test
    void testPaymentOut() {

        fillPaymentOut()

        context.commitChanges()
        checkTransactions()
    }

    
    private void fillInvoiceWitDate(LocalDate effectiveDate) {
        Contact contact = ObjectSelect.query(Contact.class).where(Contact.ID.eq(1L)).selectOne(context)

        Invoice invoice = context.newObject(Invoice.class)
        invoice.setContact(contact)
        invoice.setDescription("Description")
        invoice.setInvoiceDate(effectiveDate)
        invoice.setDateDue(effectiveDate)

        InvoiceLine line = context.newObject(InvoiceLine.class)
        line.setInvoice(invoice)
        line.setDiscountEachExTax(Money.ZERO)
        line.setPriceEachExTax(Money.ONE)
        Tax tax = ObjectSelect.query(Tax.class).where(Tax.ID.eq(1L)).selectOne(context)
        Account account = tax.getPayableToAccount()
        line.setTax(tax)
        line.setAccount(account)
        line.setTitle("junit test enrolment")
        line.setTaxEach(Money.ZERO)
        line.setQuantity(BigDecimal.ONE)
    }

    
    private void fillPaymentIn() {
        Contact contact = ObjectSelect.query(Contact.class).where(Contact.ID.eq(1L)).selectOne(context)

        Invoice invoice = ObjectSelect.query(Invoice.class).where(Invoice.ID.eq(1L)).selectOne(context)

        PaymentIn paymentIn = context.newObject(PaymentIn.class)

        paymentIn.setStatus(PaymentStatus.SUCCESS)
        paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getRealTimeCreditCardPaymentMethod(context, PaymentMethod.class), paymentIn).set()
        paymentIn.setCreditCardName("test name")
        paymentIn.setCreditCardNumber("test number")
        paymentIn.setCreditCardExpiry("01/01")
        paymentIn.setCreditCardType(CreditCardType.VISA)
        paymentIn.setAmount(invoice.getAmountOwing())
        paymentIn.setPayer(contact)
        paymentIn.setPaymentDate(LocalDate.now())

        PaymentInLine paymentInLine = context.newObject(PaymentInLine.class)
        paymentInLine.setAmount(invoice.getTotalIncTax())
        paymentInLine.setAccount(invoice.getDebtorsAccount())
        paymentInLine.setPayment(paymentIn)
        paymentInLine.setInvoice(invoice)
    }

    
    private void fillPaymentOut() {
        Contact contact = ObjectSelect.query(Contact.class).where(Contact.ID.eq(1L)).selectOne(context)

        Invoice invoice = ObjectSelect.query(Invoice.class).where(Invoice.ID.eq(2L)).selectOne(context)

        PaymentOut paymentOut = context.newObject(PaymentOut.class)
        PaymentOutLine outLine = context.newObject(PaymentOutLine.class)
        outLine.setPaymentOut(paymentOut)
        outLine.setAccount(invoice.getDebtorsAccount())
        outLine.setInvoice(invoice)
        outLine.setAmount(invoice.getAmountOwing().negate())

        paymentOut.setStatus(PaymentStatus.SUCCESS)
        paymentOut.setPayee(contact)
        paymentOut.setAmount(invoice.getAmountOwing().negate())
        paymentOut.setPaymentDate(LocalDate.now())
        SetPaymentMethod.valueOf(PaymentMethodUtil.getRealTimeCreditCardPaymentMethod(context, PaymentMethod.class), paymentOut).set()

    }


    
    private void checkTransactions() {

        List<AccountTransaction> transactions = ObjectSelect.query(AccountTransaction.class).select(context)

        Assertions.assertTrue(transactions.size() > 0)
        transactions.each { t ->
            Assertions.assertEquals(t.getTransactionDate(), LocalDate.now())
        }
    }

    
    private void checkError(Exception ex) {
        logger.catching(ex)
        Throwable th = ExceptionUtils.getThrowables(ex)[0]
        Assertions.assertTrue(th instanceof ValidationException)
        Assertions.assertTrue(((ValidationException) th).getValidationResult().hasFailures())
        Assertions.assertEquals(1, ((ValidationException) th).getValidationResult().getFailures().size())
        Assertions.assertEquals("You must choose a date after 31-Dec-2015", ((ValidationException) th).getValidationResult().getFailures().get(0).getDescription())
    }
}
