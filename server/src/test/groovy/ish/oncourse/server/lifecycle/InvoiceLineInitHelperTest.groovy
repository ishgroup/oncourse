/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.lifecycle

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.*
import ish.math.Money
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.*
import ish.util.AccountUtil
import ish.util.PaymentMethodUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.Ordering
import org.apache.cayenne.query.SelectById
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.text.SimpleDateFormat
import java.time.LocalDate

@CompileStatic
class InvoiceLineInitHelperTest extends CayenneIshTestCase {

    private ICayenneService cayenneService

    
    @BeforeEach
    void setup() throws Exception {

        wipeTables()

        this.cayenneService = injector.getInstance(ICayenneService.class)

        InputStream st = InvoiceLineInitHelper.class.getClassLoader()
                .getResourceAsStream("ish/oncourse/server/lifecycle/invoiceLineInitHelperTest.xml")

        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
        builder.setColumnSensing(true)
        FlatXmlDataSet dataSet = builder.build(st)

        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        rDataSet.addReplacementObject("[null]", null)

        executeDatabaseOperation(rDataSet)
    }

    
    @Test
    void testInitByReversePayment() {

        ObjectContext context = cayenneService.getNewContext()

        Invoice originalInvoice = SelectById.query(Invoice.class, 100).selectOne(context)

        PaymentIn reversePayment = context.newObject(PaymentIn.class)
        reversePayment.setPaymentDate(LocalDate.now())
        reversePayment.setSource(PaymentSource.SOURCE_WEB)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getREVERSPaymentMethods(context, PaymentMethod.class), reversePayment).set()
        reversePayment.setAccountIn(AccountUtil.getDefaultBankAccount(context, Account.class))
        reversePayment.setAmount(Money.ZERO)
        reversePayment.setStatus(PaymentStatus.SUCCESS)

        reversePayment.setPayer(originalInvoice.getContact())

        PaymentInLine reversePaymentLine1 = context.newObject(PaymentInLine.class)
        reversePaymentLine1.setPayment(reversePayment)
        reversePaymentLine1.setInvoice(originalInvoice)
        reversePaymentLine1.setAmount(originalInvoice.getTotalIncTax())

        PaymentInLine reversePaymentLine2 = context.newObject(PaymentInLine.class)
        reversePaymentLine2.setPayment(reversePayment)

        Invoice reverseInvoice = createRefundInvoice(context, originalInvoice)

        reversePaymentLine2.setInvoice(reverseInvoice)
        reversePaymentLine2.setAmount(reverseInvoice.getTotalIncTax())

        for (InvoiceLine reverseLine : reverseInvoice.getInvoiceLines()) {
            Assertions.assertNull(reverseLine.getAccount())
        }

        context.commitChanges()

        List<InvoiceLine> originalLines = originalInvoice.getInvoiceLines()
        List<InvoiceLine> reverseLines = reverseInvoice.getInvoiceLines()

        Assertions.assertEquals(originalLines.size(), reverseLines.size())

        Ordering.orderList(originalLines, Arrays.asList(InvoiceLine.SORT_ORDER.asc()))
        Ordering.orderList(reverseLines, Arrays.asList(InvoiceLine.SORT_ORDER.asc()))

        for (int i = 0; i < originalLines.size(); i++) {
            Assertions.assertEquals(originalLines.get(i).getAccount(), reverseLines.get(i).getAccount())
        }
    }

    
    @Test
    void testInitByEnrolment() {

        ObjectContext context = cayenneService.getNewContext()

        Student student1 = SelectById.query(Student.class, 1).selectOne(context)

        CourseClass courseClass1 = SelectById.query(CourseClass.class, 1).selectOne(context)

        Enrolment enrol1 = context.newObject(Enrolment.class)
        enrol1.setStudent(student1)
        enrol1.setCourseClass(courseClass1)
        enrol1.setEligibilityExemptionIndicator(false)
        enrol1.setSource(PaymentSource.SOURCE_WEB)
        enrol1.setStudyReason(StudyReason.STUDY_REASON_NOT_STATED)
        enrol1.setVetFeeIndicator(false)
        enrol1.setVetIsFullTime(false)
        enrol1.setStatus(EnrolmentStatus.SUCCESS)

        Invoice invoice = context.newObject(Invoice.class)

        invoice.setContact(student1.getContact())
        invoice.setInvoiceDate(LocalDate.now())
        invoice.setDateDue(LocalDate.now())

        InvoiceLine invoiceLine = context.newObject(InvoiceLine.class)
        invoiceLine.setInvoice(invoice)
        invoiceLine.setEnrolment(enrol1)
        invoiceLine.setSortOrder(0)
        invoiceLine.setTitle("enrolment line")
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setPriceEachExTax(courseClass1.getFeeExGst())
        invoiceLine.setTaxEach(courseClass1.getFeeGST())
        invoiceLine.setDiscountEachExTax(Money.ZERO)

        Assertions.assertNull(invoiceLine.getAccount())

        context.commitChanges()

        Assertions.assertEquals(courseClass1.getIncomeAccount(), invoiceLine.getAccount())
        Assertions.assertEquals(courseClass1.getTax(), invoiceLine.getTax())
    }

    
    @Test
    void testInitByProduct() {
        ObjectContext context = cayenneService.getNewContext()

        Student student1 = SelectById.query(Student.class, 1).selectOne(context)

        MembershipProduct product = SelectById.query(MembershipProduct.class, 200).selectOne(context)

        Membership membership = context.newObject(Membership.class)

        membership.setProduct(product)
        membership.setContact(student1.getContact())
        membership.setExpiryDate(new SimpleDateFormat("yyyy/MM/dd").parse("2113/01/01"))
        membership.setStatus(ProductStatus.ACTIVE)

        Invoice invoice = context.newObject(Invoice.class)

        invoice.setContact(student1.getContact())
        invoice.setInvoiceDate(LocalDate.now())
        invoice.setDateDue(LocalDate.now())

        InvoiceLine invoiceLine = context.newObject(InvoiceLine.class)
        invoiceLine.setInvoice(invoice)
        invoiceLine.setSortOrder(0)
        invoiceLine.setTitle("enrolment line")
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setPriceEachExTax(membership.getProduct().getPriceExTax())
        invoiceLine.setTaxEach(membership.getProduct().getFeeGST())
        invoiceLine.setDiscountEachExTax(Money.ZERO)

        membership.setInvoiceLine(invoiceLine)

        Assertions.assertNull(invoiceLine.getAccount())

        context.commitChanges()

        Assertions.assertEquals(product.getIncomeAccount(), invoiceLine.getAccount())
        Assertions.assertEquals(product.getTax(), invoiceLine.getTax())
    }

    
    @Test
    void testInitByVoucherProduct() {
        ObjectContext context = cayenneService.getNewContext()

        Student student1 = SelectById.query(Student.class, 1).selectOne(context)

        VoucherProduct product = SelectById.query(VoucherProduct.class, 201).selectOne(context)

        Voucher voucher = context.newObject(Voucher.class)
        voucher.setCode("akjsfhashf")
        voucher.setStatus(ProductStatus.ACTIVE)
        voucher.setSource(PaymentSource.SOURCE_WEB)
        voucher.setProduct(product)
        voucher.setContact(student1.getContact())
        voucher.setExpiryDate(new Date())

        Invoice invoice = context.newObject(Invoice.class)

        invoice.setContact(student1.getContact())
        invoice.setInvoiceDate(LocalDate.now())
        invoice.setDateDue(LocalDate.now())

        InvoiceLine invoiceLine = context.newObject(InvoiceLine.class)
        invoiceLine.setInvoice(invoice)
        invoiceLine.setSortOrder(0)
        invoiceLine.setTitle("voucher line")
        invoiceLine.setQuantity(BigDecimal.ONE)
        invoiceLine.setPriceEachExTax(voucher.getProduct().getPriceExTax())
        invoiceLine.setTaxEach(voucher.getProduct().getFeeGST())
        invoiceLine.setDiscountEachExTax(Money.ZERO)

        voucher.setInvoiceLine(invoiceLine)

        Assertions.assertNull(invoiceLine.getAccount())

        context.commitChanges()

        Assertions.assertEquals(product.getLiabilityAccount(), invoiceLine.getAccount())
        Assertions.assertEquals(product.getTax(), invoiceLine.getTax())
    }

    
    Invoice createRefundInvoice(ObjectContext cc, Invoice invoiceToRefund) {
        Invoice refundInvoice = cc.newObject(Invoice.class)

        refundInvoice.setBillToAddress(invoiceToRefund.getBillToAddress())
        refundInvoice.setInvoiceDate(LocalDate.now())
        refundInvoice.setDateDue(LocalDate.now())
        refundInvoice.setContact(invoiceToRefund.getContact())

        for (InvoiceLine originalLine : invoiceToRefund.getInvoiceLines()) {
            final InvoiceLine refundInvoiceLine = cc.newObject(InvoiceLine.class)

            refundInvoiceLine.setInvoice(refundInvoice)
            refundInvoiceLine.setSortOrder(originalLine.getSortOrder())
            refundInvoiceLine.setTitle(originalLine.getTitle())
            refundInvoiceLine.setUnit(originalLine.getUnit())
            refundInvoiceLine.setQuantity(new BigDecimal("1.00"))
            refundInvoiceLine.setPriceEachExTax(Money.ZERO.subtract(originalLine.getPriceEachExTax()))
            refundInvoiceLine.setDiscountEachExTax(Money.ZERO.subtract(originalLine.getDiscountEachExTax()))
            refundInvoiceLine.setTaxEach(Money.ZERO.subtract(originalLine.getTaxEach()))
        }

        return refundInvoice
    }
}
