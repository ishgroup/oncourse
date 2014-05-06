/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.enrol.checkout;


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

public class MoneyVoucherRedemptionTest extends ACheckoutTest {

	private static final String VoucherCode_1 = "v1003";
	private static final String VoucherCode_2 = "v1004";
	private static final String VoucherCode_3 = "v1006";
	private static final String Status_SUCCES = "SUCCESS";
	private static final String PaymentTypeVoucher = "Voucher";
	private static final String PaymentTypeZero = "ZERO";
	private static final String VoucherPaymentStatus_APPROVED = "Approved";
	private static final String VoucherStatus_REDEEMED = "REDEEMED";

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/MoneyVoucherRedemptionTest.xml");

	}

	private void assertInvoices(Invoice expected, Invoice actual, int feeExGst, int feeGst) {
		assertNotSame(expected, actual);
		assertEquals(expected.getObjectId(), actual.getObjectId());
		assertEquals(feeGst + feeExGst, actual.getTotalGst().intValue());
		assertEquals(feeExGst, actual.getTotalExGst().intValue());
		assertEquals(0, actual.getAmountOwing().intValue());
	}

	private void assertInvoiceLines(InvoiceLine expected, InvoiceLine actual, int feeExGst, int feeGst) {
		assertEquals(expected.getObjectId(), actual.getObjectId());
		assertEquals(feeExGst, actual.getPriceEachExTax().intValue());
		assertEquals(feeGst, actual.getTaxEach().intValue());
		assertEquals(0, actual.getDiscountEachExTax().intValue());
	}

	private void assertModel(int feeExGst, int feeGst, int money, int voucherCount) {
		ObjectContext context = purchaseController.getCayenneService().newContext();
		Invoice invoice = purchaseController.getModel().getInvoice();
		Invoice invoiceDB = Cayenne.objectForPK(context, Invoice.class, invoice.getId());
		//check Invoice
		assertInvoices(invoice, invoiceDB, feeExGst, feeGst);

		List<InvoiceLine> invoiceLines = invoice.getInvoiceLines();
		List<InvoiceLine> invoiceLinesDB = invoiceDB.getInvoiceLines();
		//check InvoiceLine
		assertEquals(1, invoiceLinesDB.size());
		assertEquals(invoiceLines.size(), invoiceLinesDB.size());
		assertInvoiceLines(invoiceLines.get(0), invoiceLinesDB.get(0), feeExGst, feeGst);

		Enrolment enrolment = invoiceLines.get(0).getEnrolment();
		Enrolment enrolmentDB = invoiceLinesDB.get(0).getEnrolment();
		//check Enrolment
		assertNotSame(enrolment, enrolmentDB);
		assertEquals(enrolment.getId(), enrolmentDB.getId());
		assertEquals(Status_SUCCES, enrolmentDB.getStatus().name().toString());

		List<PaymentInLine> paymentInLines = invoice.getPaymentInLines();
		List<PaymentInLine> paymentInLinesDB = invoiceDB.getPaymentInLines();
		assertEquals(voucherCount + 1, paymentInLinesDB.size());
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
						assertEquals(VoucherPaymentStatus_APPROVED, voucherPaymentInDB.getStatus().toString());
						//check PaymentInLine and PaymentIn that has relation with Voucher						
						if (voucherCount < 2) {
							assertEquals(VoucherCode_1, voucherPaymentInDB.getVoucher().getCode());
							assertEquals(feeExGst + feeGst - money, paymentInLineDB.getAmount().intValue());
						} else {
							if (paymentInLineDB.getAmount().intValue() == money) {
								assertEquals(VoucherCode_2, voucherPaymentInDB.getVoucher().getCode());
							} else {
								assertEquals(feeExGst + feeGst - money, paymentInLineDB.getAmount().intValue());
								assertEquals(VoucherCode_1, voucherPaymentInDB.getVoucher().getCode());
							}

						}
						assertEquals(Status_SUCCES, paymentInDB.getStatus().name().toString());
						assertEquals(PaymentTypeVoucher, paymentInDB.getType().toString());
						if (voucherPaymentInDB.getVoucher().isMoneyVoucher()) {
							assertNull(voucherPaymentInDB.getInvoiceLine());
						} else {
							//check another PaymentInLine and PaymentIn
							assertEquals(money, paymentInLineDB.getAmount().intValue());
							assertEquals(Status_SUCCES, paymentInDB.getStatus().name().toString());
							assertEquals(PaymentTypeZero, paymentInDB.getType().toString());
						}
					}
				}
			}
		}
		assertEquals(voucherCount + 1, paymentInEqualsCount);
		assertEquals(voucherCount, payIn_VoucherPayIn_Links);
	}

	private void assertQueuedTransaction(int voucherCount, int discountCount) {
		ObjectContext context = purchaseController.getCayenneService().newContext();
		SelectQuery selectQuery = new SelectQuery(QueuedTransaction.class);
		List<QueuedTransaction> listQT = context.performQuery(selectQuery);
		assertEquals(2, listQT.size());

		//check first QueuedTransaction contains right QueuedRecords
		assertEquals(3 + voucherCount + discountCount, listQT.get(0).getQueuedRecords().size());
		List<QueuedRecord> qRecords = listQT.get(0).getQueuedRecords();
		Expression matchExp1 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Student");
		assertEquals(1, matchExp1.filterObjects(qRecords).size());
		Expression matchExp2 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Voucher");
		assertEquals(voucherCount, matchExp2.filterObjects(qRecords).size());
		Expression matchExp3 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Contact");
		assertEquals(1, matchExp3.filterObjects(qRecords).size());
		Expression matchExp4 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "CourseClass");
		assertEquals(1, matchExp4.filterObjects(qRecords).size());
		Expression matchExp5 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Discount");
		assertEquals(discountCount, matchExp5.filterObjects(qRecords).size());

		assertEquals(5 + voucherCount * 4 + discountCount, listQT.get(1).getQueuedRecords().size());
		//check second QueuedTransaction contains right QueuedRecords
		List<QueuedRecord> queuedRecords = listQT.get(1).getQueuedRecords();
		Expression exp1 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Enrolment");
		assertEquals(1, exp1.filterObjects(queuedRecords).size());
		Expression exp2 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "InvoiceLine");
		assertEquals(1, exp2.filterObjects(queuedRecords).size());
		Expression exp3 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Invoice");
		assertEquals(1, exp3.filterObjects(queuedRecords).size());
		Expression exp4 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "PaymentInLine");
		assertEquals(voucherCount + 1, exp4.filterObjects(queuedRecords).size());
		Expression exp5 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "PaymentIn");
		assertEquals(voucherCount + 1, exp5.filterObjects(queuedRecords).size());
		Expression exp6 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "VoucherPaymentIn");
		assertEquals(voucherCount, exp6.filterObjects(queuedRecords).size());
		Expression exp7 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "Voucher");
		assertEquals(voucherCount, exp7.filterObjects(queuedRecords).size());
		Expression exp8 = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, "InvoiceLineDiscount");
		assertEquals(discountCount, exp8.filterObjects(queuedRecords).size());
	}

	@Test
	public void testMoneyVoucherRedemptionWithoutMoney() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1001L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		assertTrue("purchaseController is not in state 'AddContact'", purchaseController.isAddContact());
		addFirstContact(1001L);
		assertTrue("purchaseController is not in state 'EditCheckout'", purchaseController.isEditCheckout());
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode(VoucherCode_1);
		assertEquals(1, model.getSelectedVouchers().size());
		proceedToPayment();
		assertTrue("purchaseController is not in state 'EditPayment'", purchaseController.isEditPayment());
		makeInvalidPayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		assertModel(400, 50, 0, 1);
		//check Voucher
		assertEquals(0, model.getVouchers().get(0).getValueRemaining().intValue());
		assertEquals(VoucherStatus_REDEEMED, model.getVouchers().get(0).getStatus().name().toString());

		assertQueuedTransaction(1, 0);
	}

	@Test
	public void testMoneyVoucherRedemptionWithBalance() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1003L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		assertTrue("purchaseController is not in state 'AddContact'", purchaseController.isAddContact());
		addFirstContact(1001L);
		assertTrue("purchaseController is not in state 'EditCheckout'", purchaseController.isEditCheckout());
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode(VoucherCode_1);
		assertEquals(1, model.getSelectedVouchers().size());
		proceedToPayment();
		assertTrue("purchaseController is not in state 'EditPayment'", purchaseController.isEditPayment());
		makeInvalidPayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		assertModel(100, 10, 0, 1);
		//check Voucher
		assertEquals(340, model.getVouchers().get(0).getValueRemaining().intValue());
		assertEquals("ACTIVE", model.getVouchers().get(0).getStatus().name().toString());

		assertQueuedTransaction(1, 0);
	}

	@Test
	public void testMoneyVoucherRedemptionWithMoney() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1002L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		assertTrue("purchaseController is not in state 'AddContact'", purchaseController.isAddContact());
		addFirstContact(1001L);
		assertTrue("purchaseController is not in state 'EditCheckout'", purchaseController.isEditCheckout());
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode(VoucherCode_1);
		assertEquals(1, model.getSelectedVouchers().size());
		proceedToPayment();
		assertTrue("purchaseController is not in state 'EditPayment'", purchaseController.isEditPayment());
		makeValidPayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		assertModel(500, 50, 100, 1);
		//check Voucher
		assertEquals(0, model.getVouchers().get(0).getValueRemaining().intValue());
		assertEquals(VoucherStatus_REDEEMED, model.getVouchers().get(0).getStatus().name().toString());
		assertQueuedTransaction(1, 0);
	}


	@Test
	public void testMoneyVoucherRedemptionWithTwoVouchers() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1002L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		assertTrue("purchaseController is not in state 'AddContact'", purchaseController.isAddContact());
		addFirstContact(1001L);
		assertTrue("purchaseController is not in state 'EditCheckout'", purchaseController.isEditCheckout());
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode(VoucherCode_1);
		addCode(VoucherCode_2);
		assertEquals(2, model.getSelectedVouchers().size());
		proceedToPayment();
		assertTrue("purchaseController is not in state 'EditPayment'", purchaseController.isEditPayment());
		makeInvalidPayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		assertModel(500, 50, 100, 2);
		//check Voucher
		assertEquals(0, model.getVouchers().get(0).getValueRemaining().intValue());
		assertEquals(VoucherStatus_REDEEMED, model.getVouchers().get(0).getStatus().name().toString());
		assertEquals(0, model.getVouchers().get(1).getValueRemaining().intValue());
		assertEquals(VoucherStatus_REDEEMED, model.getVouchers().get(1).getStatus().name().toString());
		assertQueuedTransaction(2, 0);
	}

	@Test
	public void testVoucherRedemptionWithTwoDifferentVouchers() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1001L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		assertTrue("purchaseController is not in state 'AddContact'", purchaseController.isAddContact());
		addFirstContact(1001L);
		assertTrue("purchaseController is not in state 'EditCheckout'", purchaseController.isEditCheckout());
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode(VoucherCode_1);
		addCode(VoucherCode_3);
		assertEquals(2, model.getSelectedVouchers().size());
		proceedToPayment();
		assertTrue("purchaseController is not in state 'EditPayment'", purchaseController.isEditPayment());
		makeInvalidPayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		//check Vouchers
		Voucher moneyVoucher;
		Voucher courseClassVoucher;
		if (model.getVouchers().get(0).isMoneyVoucher()) {
			moneyVoucher = model.getVouchers().get(0);
			courseClassVoucher = model.getVouchers().get(1);
		} else {
			moneyVoucher = model.getVouchers().get(1);
			courseClassVoucher = model.getVouchers().get(0);
		}
		assertEquals(450, moneyVoucher.getValueRemaining().intValue());
		assertEquals("ACTIVE", moneyVoucher.getStatus().name().toString());
		assertEquals(VoucherStatus_REDEEMED, courseClassVoucher.getStatus().name().toString());
	}

	@Test
	public void testMoneyVoucherRedemptionWithDiscount() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1001L), Collections.EMPTY_LIST, Arrays.asList(1001L), false);
		assertTrue("purchaseController is not in state 'AddContact'", purchaseController.isAddContact());
		addFirstContact(1001L);
		assertTrue("purchaseController is not in state 'EditCheckout'", purchaseController.isEditCheckout());
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode(VoucherCode_1);
		assertEquals(1, model.getSelectedVouchers().size());
		addCode("d1001");
		assertEquals(1, model.getDiscounts().size());

		proceedToPayment();
		assertTrue("purchaseController is not in state 'EditPayment'", purchaseController.isEditPayment());
		makeValidPayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		//check Voucher

		assertEquals(113, model.getVouchers().get(0).getValueRemaining().intValue());
		assertEquals("ACTIVE", model.getVouchers().get(0).getStatus().name().toString());

		assertQueuedTransaction(1, 1);
	}

	@Test
	public void testMoneyVoucherRedemptionOnDifferentStages() throws InterruptedException {
		PurchaseController purchaseController = init(Arrays.asList(1001L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		assertTrue("purchaseController is not in state 'AddContact'", purchaseController.isAddContact());
		addFirstContact(1001L);
		assertTrue("purchaseController is not in state 'EditCheckout'", purchaseController.isEditCheckout());
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode(VoucherCode_2);
		assertEquals(1, model.getSelectedVouchers().size());
		proceedToPayment();
		addCode(VoucherCode_1);
		assertEquals(2, model.getSelectedVouchers().size());
		assertTrue("purchaseController is not in state 'EditPayment'", purchaseController.isEditPayment());
		makeInvalidPayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		assertModel(400, 50, 100, 2);
		//check Vouchers
		Voucher moneyVoucher1;
		Voucher moneyVoucher2;
		if (model.getVouchers().get(0).getStatus().name().toString().equals("ACTIVE")) {
			moneyVoucher1 = model.getVouchers().get(0);
			moneyVoucher2 = model.getVouchers().get(1);
		} else {
			moneyVoucher1 = model.getVouchers().get(1);
			moneyVoucher2 = model.getVouchers().get(0);
		}
		assertEquals(100, moneyVoucher1.getValueRemaining().intValue());
		assertEquals(0, moneyVoucher2.getValueRemaining().intValue());
		assertEquals(VoucherStatus_REDEEMED, moneyVoucher2.getStatus().name().toString());
		assertQueuedTransaction(2, 0);
	}
}