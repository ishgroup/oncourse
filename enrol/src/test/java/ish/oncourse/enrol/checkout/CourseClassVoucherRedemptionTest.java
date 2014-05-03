/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.ProductStatus;
import ish.math.Money;
import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.model.*;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.State.paymentProgress;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class CourseClassVoucherRedemptionTest extends ACheckoutTest{
	
	protected static final String VALID_VOUCHER = "v1003";
	protected static final String INVALID_VOUCHER = "v1004";
	
	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/CourseClassVoucherRedemptionTest.xml");

	}
	
	@Test
	public void testValidVoucher() throws InterruptedException {
		purchaseController = init(Arrays.asList(1001L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		assertTrue("purchaseController is not in state 'AddContact'", purchaseController.isAddContact());
		addFirstContact(1001L);
		assertTrue("purchaseController is not in state 'EditCheckout'", purchaseController.isEditCheckout());
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		addCode(VALID_VOUCHER);
		assertEquals(1, model.getSelectedVouchers().size());
		proceedToPayment();
		assertTrue("purchaseController is not in state 'EditPayment'", purchaseController.isEditPayment());
		makePayment();
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.isFinished());
		assertModel();
		
		//chekc voucher
		assertVoucher();
	
	}
	
	@Test
	public void testInvalidVoucher() throws InterruptedException {

		purchaseController = init(Arrays.asList(1001L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		addFirstContact(1001L);
		PurchaseModel model = purchaseController.getModel();
		assertTrue(model.getSelectedVouchers().isEmpty());
		//add invalid voucher 
		addCode(INVALID_VOUCHER);
		proceedToPayment();
		//check that invalid voucher was not applayed
		assertEquals(0, purchaseController.getModel().getVoucherPayments().size());
		assertEquals(new Money(450, 0), purchaseController.getModel().getPayment().getAmount());
		assertEquals(1, purchaseController.getModel().getInvoice().getPaymentInLines().size());
	}
	

	private void assertVoucher() {
		
		Voucher voucher = Cayenne.objectForPK(purchaseController.getCayenneService().newContext(), Voucher.class, 1003L);
		assertEquals(0, voucher.getClassesRemaining().intValue());
		assertEquals(ProductStatus.REDEEMED, voucher.getStatus());
		assertTrue(voucher.isFullyRedeemed());
	}

	private void makePayment() throws InterruptedException {
		PaymentEditorDelegate delegate = purchaseController.getPaymentEditorDelegate();
		delegate.makePayment();
		assertEquals(paymentProgress, purchaseController.getState());
		assertTrue(purchaseController.isPaymentProgress());
		assertFalse(purchaseController.isFinished());
		updatePaymentStatus();

	}
	private void assertModel() {
		int FeeExGst= 400;
		int FeeGst= 50;
		ObjectContext context = purchaseController.getCayenneService().newContext();
		Invoice invoice = purchaseController.getModel().getInvoice();
		Invoice invoiceDB = Cayenne.objectForPK(context, Invoice.class, invoice.getId());
		//check Invoice
		assertNotSame(invoice, invoiceDB);
		assertEquals(invoice.getId(), invoiceDB.getId());
		assertEquals(FeeGst+FeeExGst,invoiceDB.getTotalGst().intValue());
		assertEquals(FeeExGst,invoiceDB.getTotalExGst().intValue());
		assertEquals(0,invoiceDB.getAmountOwing().intValue());

		List<InvoiceLine> invoiceLines = invoice.getInvoiceLines();
		List<InvoiceLine> invoiceLinesDB = invoiceDB.getInvoiceLines();
		//check InvoiceLine
		assertEquals(1, invoiceLinesDB.size());
		assertEquals(invoiceLines.size(), invoiceLinesDB.size());
		assertEquals(invoiceLinesDB.get(0).getId(), invoiceLines.get(0).getId());
		assertEquals(FeeExGst,invoiceLinesDB.get(0).getPriceEachExTax().intValue());
		assertEquals(FeeGst,invoiceLinesDB.get(0).getTaxEach().intValue());
		assertEquals(0,invoiceLinesDB.get(0).getDiscountEachExTax().intValue());

		Enrolment enrolment = invoiceLines.get(0).getEnrolment();
		Enrolment enrolmentDB = invoiceLinesDB.get(0).getEnrolment();
		//check Enrolment
		assertNotSame(enrolment, enrolmentDB);
		assertEquals(enrolment.getId(), enrolmentDB.getId());
		assertEquals(EnrolmentStatus.SUCCESS,enrolmentDB.getStatus());

		List<PaymentInLine> paymentInLines = invoice.getPaymentInLines();
		List<PaymentInLine> paymentInLinesDB = invoiceDB.getPaymentInLines();
		assertEquals(2, paymentInLinesDB.size());
		assertEquals(paymentInLines.size(), paymentInLinesDB.size());
		
		//check PaymentIn
		for(PaymentInLine paymentInLine : paymentInLinesDB){
			if (paymentInLine.getPaymentIn().getVoucher() != null) {
				assertEquals(1, paymentInLine.getPaymentIn().getVoucherPaymentIns().size());
				assertEquals(PaymentType.VOUCHER, paymentInLine.getPaymentIn().getType());
				assertEquals(PaymentStatus.SUCCESS, paymentInLine.getPaymentIn().getStatus());
				assertEquals(VALID_VOUCHER, paymentInLine.getPaymentIn().getVoucher().getCode());
				assertEquals(FeeGst + FeeExGst, paymentInLine.getAmount().intValue());
			} else {
				assertEquals(Money.ZERO, paymentInLine.getAmount());
				assertEquals(PaymentType.INTERNAL, paymentInLine.getPaymentIn().getType());
				assertEquals(PaymentStatus.SUCCESS, paymentInLine.getPaymentIn().getStatus());
			}
		}
	}
	
}
