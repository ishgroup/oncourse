/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.common.types.*
import ish.math.Money
import ish.oncourse.commercial.replication.handler.OutboundReplicationHandlerTest
import ish.oncourse.commercial.replication.services.TransactionGroupProcessorImpl
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.*
import ish.oncourse.webservices.v23.stubs.replication.*
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

import org.junit.Test

import java.time.LocalDate

import static org.junit.Assert.*

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/commercial/replication/updaters/PaymentInUpdaterTest.xml")
class PaymentInUpdaterTest extends TestWithDatabase {

    private ICayenneService cayenneService

    @Test
    void testWillowVoucherPaymentIn() {
        replicatePaymentIn(PaymentSource.SOURCE_WEB, PaymentType.VOUCHER)

        ObjectContext context = cayenneService.getNewContext()

        //changed getting of object, because looking for object by Id, which sets by db is not correct
	    PaymentIn paymentIn = ObjectSelect.query(PaymentIn.class).
			    where(PaymentIn.PAYER.dot(Contact.ID).eq(2L)).
			    and(PaymentIn.AMOUNT.eq(new Money(BigDecimal.ZERO))).
			    and(PaymentIn.SOURCE.eq(PaymentSource.SOURCE_WEB)).
			    and(PaymentIn.PAYMENT_METHOD.dot(PaymentMethod.NAME).eq(PaymentType.VOUCHER.getDisplayName())).
			    and(PaymentIn.WILLOW_ID.isNotNull()).
			    selectOne(context)

        VoucherProduct voucherProduct = SelectById.query(VoucherProduct.class, 100).selectOne(context)

        assertEquals(voucherProduct.getLiabilityAccount(), paymentIn.getAccountIn())
    }

    @Test
    void testAngelVoucherPaymentIn() {
        replicatePaymentIn(PaymentSource.SOURCE_ONCOURSE, PaymentType.VOUCHER)

        ObjectContext context = cayenneService.getNewContext()

        //changed getting of object, because looking for object by Id, which sets by db is not correct
	    PaymentIn paymentIn = ObjectSelect.query(PaymentIn.class).
			    where(PaymentIn.PAYER.dot(Contact.ID).eq(2L)).
	            and(PaymentIn.AMOUNT.eq(new Money(BigDecimal.ZERO))).
			    and(PaymentIn.SOURCE.eq(PaymentSource.SOURCE_ONCOURSE)).
			    and(PaymentIn.PAYMENT_METHOD.dot(PaymentMethod.NAME).eq(PaymentType.VOUCHER.getDisplayName())).
			    and(PaymentIn.WILLOW_ID.isNotNull()).
				selectOne(context)

        assertNotNull(paymentIn.getBanking())
        assertEquals(LocalDate.of(2100, 12 ,31), paymentIn.getBanking().getSettlementDate())

        VoucherProduct voucherProduct = SelectById.query(VoucherProduct.class, 100).selectOne(context)

        assertNotEquals(voucherProduct.getLiabilityAccount(), paymentIn.getAccountIn())

    }

    @Test
    void testWillowPaymentIn() {
        replicatePaymentIn(PaymentSource.SOURCE_WEB, PaymentType.CREDIT_CARD)

        ObjectContext context = cayenneService.getNewContext()

        //changed getting of object, because looking for object by Id, which sets by db is not correct
	    PaymentIn paymentIn = ObjectSelect.query(PaymentIn.class).
			    where(PaymentIn.PAYER.dot(Contact.ID).eq(2L)).
				and(PaymentIn.AMOUNT.eq(new Money(BigDecimal.ZERO))).
			    and(PaymentIn.SOURCE.eq(PaymentSource.SOURCE_WEB)).
			    and(PaymentIn.PAYMENT_METHOD.dot(PaymentMethod.TYPE).eq(PaymentType.CREDIT_CARD)).
			    and(PaymentIn.WILLOW_ID.isNotNull()).
			    selectOne(context)

        assertNotNull(paymentIn.getBanking())
        assertEquals(LocalDate.of(2100, 12 ,31), paymentIn.getBanking().getSettlementDate())

        VoucherProduct voucherProduct = SelectById.query(VoucherProduct.class, 100).selectOne(context)

        assertNotEquals(voucherProduct.getLiabilityAccount(), paymentIn.getAccountIn())

    }

	@Test
    void testWillowRefundPaymentIn() {
		replicatePaymentIn(PaymentSource.SOURCE_WEB, PaymentType.REVERSE)

        ObjectContext context = cayenneService.getNewContext()

        //changed getting of object, because looking for object by Id, which sets by db is not correct
		PaymentIn paymentIn = ObjectSelect.query(PaymentIn.class).
				where(PaymentIn.PAYER.dot(Contact.ID).eq(2L)).
				and(PaymentIn.AMOUNT.eq(new Money(BigDecimal.ZERO))).
				and(PaymentIn.SOURCE.eq(PaymentSource.SOURCE_WEB)).
				and(PaymentIn.PAYMENT_METHOD.dot(PaymentMethod.TYPE).eq(PaymentType.REVERSE)).
				and(PaymentIn.WILLOW_ID.isNotNull()).
				selectOne(context)

        assertNotNull(paymentIn)
    }


	@Test
    void testWillowInternalPaymentIn() {
		replicatePaymentIn(PaymentSource.SOURCE_WEB, PaymentType.INTERNAL)

        ObjectContext context = cayenneService.getNewContext()

        //changed getting of object, because looking for object by Id, which sets by db is not correct
		PaymentIn paymentIn = ObjectSelect.query(PaymentIn.class).
				where(PaymentIn.PAYER.dot(Contact.ID).eq(2L)).
				and(PaymentIn.AMOUNT.eq(new Money(BigDecimal.ZERO))).
				and(PaymentIn.SOURCE.eq(PaymentSource.SOURCE_WEB)).
				and(PaymentIn.PAYMENT_METHOD.dot(PaymentMethod.TYPE).eq(PaymentType.INTERNAL)).
				and(PaymentIn.WILLOW_ID.isNotNull()).
				selectOne(context)

        assertNotNull(paymentIn)
    }

    private void replicatePaymentIn(PaymentSource paymentSource, PaymentType paymentType) {
        long contactId = 2L
        PaymentInStub paymentInStub = createPaymentInStub(1L, contactId, paymentSource, paymentType)
        InvoiceStub invoiceStub = createInvoiceStub(1L, contactId)
        EnrolmentStub enrolmentStub = createEnrolmentStub(1L)

        PaymentInLineStub paymentInLineStub = createPaymentInLineStub(1L, paymentInStub, invoiceStub)
        InvoiceLineStub invoiceLineStub = createInvoiceLineStub(1L, invoiceStub, enrolmentStub)

        VoucherPaymentInStub voucherPaymentInStub = createVoucherPaymentInStub(1L, paymentInStub, invoiceLineStub)

        TransactionGroup transactionGroup = new TransactionGroup()
        transactionGroup.getReplicationStub().add(paymentInStub)
        transactionGroup.getReplicationStub().add(invoiceStub)
        transactionGroup.getReplicationStub().add(enrolmentStub)
        transactionGroup.getReplicationStub().add(paymentInLineStub)
        transactionGroup.getReplicationStub().add(invoiceLineStub)
        transactionGroup.getReplicationStub().add(voucherPaymentInStub)

        TransactionGroupProcessorImpl transactionGroupProcessor = new TransactionGroupProcessorImpl(cayenneService, new AngelUpdaterImpl())
        transactionGroupProcessor.processGroup(transactionGroup)
    }


    private VoucherPaymentInStub createVoucherPaymentInStub(long willowId, PaymentInStub paymentInStub, InvoiceLineStub invoiceLineStub) {
        VoucherPaymentInStub voucherPaymentInStub = new VoucherPaymentInStub()
        voucherPaymentInStub.setCreated(new Date())
        voucherPaymentInStub.setEntityIdentifier(VoucherPaymentIn.class.getSimpleName())
        voucherPaymentInStub.setInvoiceLineId(invoiceLineStub.getWillowId())
        voucherPaymentInStub.setPaymentInId(paymentInStub.getWillowId())
        voucherPaymentInStub.setInvoiceLineId(invoiceLineStub.getWillowId())
        voucherPaymentInStub.setVoucherId(1L)
        voucherPaymentInStub.setWillowId(willowId)
        return voucherPaymentInStub
    }

    private InvoiceLineStub createInvoiceLineStub(long willowId, InvoiceStub invoiceStub, EnrolmentStub enrolmentStub) {
        InvoiceLineStub invoiceLineStub = new InvoiceLineStub()
        invoiceLineStub.setCreated(new Date())

        invoiceLineStub.setEntityIdentifier(InvoiceLine.class.getSimpleName())
        invoiceLineStub.setWillowId(willowId)
        invoiceLineStub.setInvoiceId(invoiceStub.getWillowId())
        invoiceLineStub.setPriceEachExTax(BigDecimal.ZERO)
        invoiceLineStub.setTaxEach(BigDecimal.ZERO)
        invoiceLineStub.setEnrolmentId(enrolmentStub.getWillowId())
        invoiceLineStub.setTitle("invoiceLineStub")
        invoiceLineStub.setQuantity(BigDecimal.ONE)
        return invoiceLineStub
    }

    private EnrolmentStub createEnrolmentStub(long willowId) {
        EnrolmentStub enrolmentStub = new EnrolmentStub()
        enrolmentStub.setCreated(new Date())

        enrolmentStub.setSource(PaymentSource.SOURCE_WEB.getDatabaseValue())
        enrolmentStub.setReasonForStudy(StudyReason.STUDY_REASON_BETTER_JOB.getDatabaseValue())
        enrolmentStub.setEntityIdentifier(Enrolment.class.getSimpleName())
        enrolmentStub.setWillowId(willowId)
        enrolmentStub.setStatus(EnrolmentStatus.SUCCESS.name())
        enrolmentStub.setStudentId(1L)
        enrolmentStub.setCourseClassId(1L)
        return enrolmentStub
    }

    private InvoiceStub createInvoiceStub(long willowId, long contactId) {
        InvoiceStub invoiceStub = new InvoiceStub()
        invoiceStub.setCreated(new Date())

        invoiceStub.setAllowAutoPay(false)
        invoiceStub.setInvoiceDate(new Date())
        invoiceStub.setDateDue(new Date())
        invoiceStub.setEntityIdentifier(Invoice.class.getSimpleName())
        invoiceStub.setSource(PaymentSource.SOURCE_WEB.getDatabaseValue())
        invoiceStub.setWillowId(willowId)
        invoiceStub.setContactId(contactId)
        invoiceStub.setTotalExGst(BigDecimal.ZERO)
        invoiceStub.setTotalGst(BigDecimal.ZERO)

        return invoiceStub
    }

    private PaymentInLineStub createPaymentInLineStub(long willowId, PaymentInStub paymentIn, InvoiceStub invoiceStub) {
        PaymentInLineStub paymentInLineStub = new PaymentInLineStub()
        paymentInLineStub.setCreated(new Date())

        paymentInLineStub.setEntityIdentifier(PaymentInLine.class.getSimpleName())
        paymentInLineStub.setAmount(BigDecimal.ZERO)
        paymentInLineStub.setInvoiceId(invoiceStub.getWillowId())
        paymentInLineStub.setPaymentInId(paymentIn.getWillowId())
        paymentInLineStub.setWillowId(willowId)
        return paymentInLineStub
    }

    private PaymentInStub createPaymentInStub(Long willowId, Long contactId, PaymentSource source, PaymentType type) {
        PaymentInStub paymentInStub = new PaymentInStub()
        paymentInStub.setCreated(new Date())

        paymentInStub.setEntityIdentifier(PaymentIn.class.getSimpleName())
        paymentInStub.setAmount(BigDecimal.ZERO)
        paymentInStub.setContactId(contactId)
        paymentInStub.setWillowId(willowId)
        paymentInStub.setSource(source.getDatabaseValue())
        paymentInStub.setType(type.getDatabaseValue())
        paymentInStub.setStatus(PaymentStatus.SUCCESS.getDatabaseValue())
        Calendar calendar = Calendar.getInstance()
        calendar.set(2100, Calendar.DECEMBER, 31)

        paymentInStub.setDateBanked(calendar.getTime())

        return paymentInStub
    }
}
