/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.enrol.checkout;

import ish.common.types.*;
import ish.oncourse.model.*;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MoneyVoucherRedemptionFailTest extends ACheckoutTest {

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/MoneyVoucherRedemptionTest.xml");
	}

	private void assertModel(int feeExGst, int feeGst) {
		ObjectContext context = purchaseController.getCayenneService().newContext();
		Invoice invoice = purchaseController.getModel().getInvoice();
		Invoice invoiceDB = Cayenne.objectForPK(context, Invoice.class, invoice.getId());
		//check Invoice
		assertNotSame(invoice, invoiceDB);
		assertEquals(invoice.getId(), invoiceDB.getId());
		assertEquals(feeGst + feeExGst, invoiceDB.getTotalGst().intValue());
		assertEquals(feeExGst, invoiceDB.getTotalExGst().intValue());
		assertEquals(0, invoiceDB.getAmountOwing().intValue());

		List<InvoiceLine> invoiceLines = invoice.getInvoiceLines();
		List<InvoiceLine> invoiceLinesDB = invoiceDB.getInvoiceLines();
		//check InvoiceLine
		assertEquals(1, invoiceLinesDB.size());
		assertEquals(invoiceLines.size(), invoiceLinesDB.size());
		assertEquals(invoiceLinesDB.get(0).getId(), invoiceLines.get(0).getId());
		assertEquals(feeExGst, invoiceLinesDB.get(0).getPriceEachExTax().intValue());
		assertEquals(feeGst, invoiceLinesDB.get(0).getTaxEach().intValue());
		assertEquals(0, invoiceLinesDB.get(0).getDiscountEachExTax().intValue());

		Enrolment enrolment = invoiceLines.get(0).getEnrolment();
		Enrolment enrolmentDB = invoiceLinesDB.get(0).getEnrolment();
		//check Enrolment
		assertNotSame(enrolment, enrolmentDB);
		assertEquals(enrolment.getId(), enrolmentDB.getId());
		assertEquals(EnrolmentStatus.FAILED, enrolmentDB.getStatus());

		List<PaymentInLine> paymentInLines = invoice.getPaymentInLines();
		List<PaymentInLine> paymentInLinesDB = invoiceDB.getPaymentInLines();
		//VoucherPaymentIn + MoneyPaymentIn + ReversePaymentIn*2
		assertEquals(4, paymentInLinesDB.size());
		assertEquals(paymentInLines.size(), paymentInLinesDB.size());

		int paymentInEqualsCount = 0;
		int payIn_VoucherPayIn_Links = 0;
		for (PaymentInLine paymentInLineDB : paymentInLinesDB) {
			for (PaymentInLine paymentInLine : paymentInLines) {
				if (paymentInLine.getId().equals(paymentInLineDB.getId())) {
					paymentInEqualsCount++;
					PaymentIn paymentIn = paymentInLine.getPaymentIn();
					PaymentIn paymentInDB = paymentInLineDB.getPaymentIn();
					assertEquals(paymentIn.getId(), paymentInDB.getId());

					if (!paymentInDB.getVoucherPaymentIns().isEmpty()) {
						payIn_VoucherPayIn_Links++;
						VoucherPaymentIn voucherPaymentIn = paymentIn.getVoucherPaymentIns().get(0);
						VoucherPaymentIn voucherPaymentInDB = paymentInDB.getVoucherPaymentIns().get(0);
						//check VoucherPaymentIn
						assertEquals(voucherPaymentIn.getId(), voucherPaymentInDB.getId());
						assertEquals("v1004", voucherPaymentInDB.getVoucher().getCode());
						assertEquals(VoucherPaymentStatus.APPROVED, voucherPaymentInDB.getStatus());
						//check PaymentInLine and PaymentIn that has relation with Voucher
						assertEquals(100, paymentInLineDB.getAmount().intValue());
						assertEquals(PaymentStatus.FAILED, paymentInDB.getStatus());
						assertEquals(PaymentType.VOUCHER, paymentInDB.getType());
						assertEquals(1, paymentInDB.getPaymentInLines().size());
						if (voucherPaymentInDB.getVoucher().isMoneyVoucher()) {
							assertNull(voucherPaymentInDB.getInvoiceLine());
						}
					} else if (paymentInDB.getType() == PaymentType.CREDIT_CARD) {
						//check another PaymentInLine and PaymentIn
						assertEquals(450, paymentInLineDB.getAmount().intValue());
						assertEquals(PaymentStatus.FAILED_CARD_DECLINED, paymentInDB.getStatus());
					} else {
						//check reverse PaymentInLine and PaymentIn
						assertEquals(PaymentType.REVERSE, paymentInDB.getType());
						if ((paymentInLineDB.getAmount().intValue() != 100) && (paymentInLineDB.getAmount().intValue() != 450)) {
							assertTrue("Failed amount in reverse PaymentInLines", false);
						}
						assertEquals(PaymentStatus.SUCCESS, paymentInDB.getStatus());
						assertEquals(2, paymentInDB.getPaymentInLines().size());
						PaymentInLine paymentInLineDB1;

						if (paymentInDB.getPaymentInLines().get(0).getObjectId() == paymentInLineDB.getObjectId()) {
							paymentInLineDB1 = paymentInDB.getPaymentInLines().get(1);
						} else {
							paymentInLineDB1 = paymentInDB.getPaymentInLines().get(0);
						}
						Invoice invoiceDB1 = paymentInLineDB1.getInvoice();
						assertEquals("Refund for enrolments", invoiceDB1.getDescription());
						List<InvoiceLine> invoiceLinesDB1 = invoiceDB1.getInvoiceLines();
						assertEquals(1, invoiceLinesDB1.size());
						assertEquals("Refund for enrolment : MSC-1 Microsoft Word", invoiceLinesDB1.get(0).getDescription());
						assertNull(invoiceLinesDB1.get(0).getEnrolment());
					}
				}
			}
		}
		//VoucherPaymentIn + MoneyPaymentIn + ReversePaymentIn*2
		assertEquals(4, paymentInEqualsCount);
		assertEquals(1, payIn_VoucherPayIn_Links);
	}

	private void assertFailQueuedTransaction() {
		ObjectContext context = purchaseController.getCayenneService().newContext();
		SelectQuery selectQuery = new SelectQuery(QueuedTransaction.class);
		List<QueuedTransaction> listQT = context.performQuery(selectQuery);
		assertEquals(2, listQT.size());

		//check first QueuedTransaction contains right QueuedRecords
		assertEquals(4, listQT.get(0).getQueuedRecords().size());
		List<QueuedRecord> qRecords = listQT.get(0).getQueuedRecords();
		Expression matchExp1 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Student");
		assertEquals(1, matchExp1.filterObjects(qRecords).size());
		Expression matchExp2 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Voucher");
		assertEquals(1, matchExp2.filterObjects(qRecords).size());
		Expression matchExp3 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Contact");
		assertEquals(1, matchExp3.filterObjects(qRecords).size());
		Expression matchExp4 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "CourseClass");
		assertEquals(1, matchExp4.filterObjects(qRecords).size());


		//check second QueuedTransaction contains right QueuedRecords
		assertEquals(18, listQT.get(1).getQueuedRecords().size());
		List<QueuedRecord> queuedRecords = listQT.get(1).getQueuedRecords();
		Expression exp1 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Enrolment");
		assertEquals(1, exp1.filterObjects(queuedRecords).size());
		//InvoiceLine + ReverseInvoiceLine
		Expression exp2 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "InvoiceLine");
		assertEquals(2, exp2.filterObjects(queuedRecords).size());
		//Invoice + ReverseInvoice
		Expression exp3 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Invoice");
		assertEquals(2, exp3.filterObjects(queuedRecords).size());
		//VoucherPaymentInLine + MoneyVoucherPaymentInLine + ReversePaymentInLine*4
		Expression exp4 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "PaymentInLine");
		assertEquals(6, exp4.filterObjects(queuedRecords).size());
		//VoucherPaymentIn + MoneyVoucherPaymentIn + ReversePaymentIn*2
		Expression exp5 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "PaymentIn");
		assertEquals(4, exp5.filterObjects(queuedRecords).size());
		Expression exp6 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Voucher");
		assertEquals(1, exp6.filterObjects(queuedRecords).size());
		Expression exp7 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Student");
		assertEquals(1, exp7.filterObjects(queuedRecords).size());
		Expression exp8 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Contact");
		assertEquals(1, exp8.filterObjects(queuedRecords).size());
	}

	@Test
	public void testMoneyVoucherRedemptionWithFail() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1002L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		assertTrue("purchaseController is not in state 'AddContact'", purchaseController.isAddContact());
		addFirstContact(1001L);
		assertTrue("purchaseController is not in state 'EditCheckout'", purchaseController.isEditCheckout());
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode("v1004");
		assertEquals(1, model.getSelectedVouchers().size());
		proceedToPayment();
		assertTrue("purchaseController is not in state 'EditPayment'", purchaseController.isEditPayment());
		makeInvalidPayment();
		assertTrue(purchaseController.isPaymentResult());
		purchaseController.getPaymentEditorDelegate().abandon();
		assertModel(500, 50);
		assertFailQueuedTransaction();
		assertEquals(100, model.getVouchers().get(0).getRedemptionValue().intValue());
		assertEquals(ProductStatus.ACTIVE, model.getVouchers().get(0).getStatus());
	}

	@Test(expected = AssertionError.class)
	public void testExpiredMoneyVoucherFail() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1002L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		assertTrue("purchaseController is not in state 'AddContact'", purchaseController.isAddContact());
		addFirstContact(1001L);
		assertTrue("purchaseController is not in state 'EditCheckout'", purchaseController.isEditCheckout());
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode("v1005");
	}

}
