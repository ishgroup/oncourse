package ish.oncourse.enrol.checkout.model;

import ish.math.Money;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.model.*;

import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class GetAmounts {

	private PurchaseController purchaseController;
	private PurchaseModel model;

	private Money defaultInvoiceAmount = Money.ZERO;

	private Money paymentPlansAmount = Money.ZERO;
	private Money minPaymentPlansAmount = Money.ZERO;
	private Money maxPaymentPlansAmount = Money.ZERO;

	private Money totalAmount = Money.ZERO;
	private Money minTotalAmount = Money.ZERO;
	private Money maxTotalAmount = Money.ZERO;


	public GetAmounts get() {
		for (Contact contact : model.getContacts()) {
			amountEnrolments(contact);
			amountProducts(contact);
		}

		if (purchaseController.isApplyPrevOwing()) {
			Money previousOwing = purchaseController.getPreviousOwing();
			defaultInvoiceAmount = defaultInvoiceAmount.add(previousOwing);
		}


		if (purchaseController.isEditCorporatePass())
			defaultInvoiceAmount = Money.ZERO;
		else
			defaultInvoiceAmount = (defaultInvoiceAmount.isLessThan(Money.ZERO) ? Money.ZERO : defaultInvoiceAmount);

		List<PaymentIn> paymentIns = model.getVoucherPayments();
		for (PaymentIn paymentIn : paymentIns) {
			defaultInvoiceAmount = defaultInvoiceAmount.subtract(paymentIn.getAmount());
			defaultInvoiceAmount = (defaultInvoiceAmount.isLessThan(Money.ZERO) ? Money.ZERO : defaultInvoiceAmount);
		}

		totalAmount = defaultInvoiceAmount.add(paymentPlansAmount);
		minTotalAmount = defaultInvoiceAmount.add(minPaymentPlansAmount);
		maxTotalAmount = defaultInvoiceAmount.add(maxPaymentPlansAmount);
		return this;
	}

	private void amountProducts(Contact contact) {
		for (ProductItem enabledProductItem : model.getEnabledProductItems(contact)) {
			InvoiceLine invoiceLine = enabledProductItem.getInvoiceLine();
			defaultInvoiceAmount = defaultInvoiceAmount.add(invoiceLine.getPriceTotalIncTax().subtract(invoiceLine.getDiscountTotalIncTax()));
		}
	}

	private void amountEnrolments(Contact contact) {
		for (Enrolment enabledEnrolment : model.getEnabledEnrolments(contact)) {
			if (model.isPaymentPlanEnrolment(enabledEnrolment)) {
				InvoiceNode node = model.getPaymentPlanInvoiceBy(enabledEnrolment);
				paymentPlansAmount = paymentPlansAmount.add(node.getPaymentAmount());
				minPaymentPlansAmount = minPaymentPlansAmount.add(node.getMinPaymentAmount());
				maxPaymentPlansAmount = maxPaymentPlansAmount.add(node.getInvoice().getTotalGst());
			}
			else {
				for (InvoiceLine invoiceLine : enabledEnrolment.getInvoiceLines()) {
					defaultInvoiceAmount = defaultInvoiceAmount.add(invoiceLine.getPriceTotalIncTax().subtract(invoiceLine.getDiscountTotalIncTax()));
				}
			}

		}
	}

	public Money getDefaultInvoiceAmount() {
		return defaultInvoiceAmount;
	}

	public Money getPaymentPlansAmount() {
		return paymentPlansAmount;
	}

	public Money getMinPaymentPlansAmount() {
		return minPaymentPlansAmount;
	}

	public Money getTotalAmount() {
		return totalAmount;
	}

	public Money getMinTotalAmount() {
		return minTotalAmount;
	}

	public Money getMaxPaymentPlansAmount() {
		return maxPaymentPlansAmount;
	}

	public Money getMaxTotalAmount() {
		return maxTotalAmount;
	}


	public static GetAmounts valueOf(PurchaseController purchaseController) {
		GetAmounts result = new GetAmounts();
		result.purchaseController = purchaseController;
		result.model = purchaseController.getModel();
		return result;
	}

}
