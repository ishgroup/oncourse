package ish.oncourse.enrol.checkout;

import ish.math.Money;
import ish.oncourse.enrol.checkout.model.GetAmounts;
import ish.oncourse.enrol.checkout.model.InvoiceNode;
import ish.oncourse.util.MoneyFormatter;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class ActionChangePayNow extends APurchaseAction {
	private Money payNow;

	@Override
	protected void makeAction() {
		if (payNow.equals(getController().getPayNow())){
			return;
		}

		Money left = payNow.subtract(getModel().getInvoice().getTotalGst());
		for (InvoiceNode invoiceNode: getModel().getPaymentPlanInvoices()) {
			left = left.subtract(invoiceNode.getMinPaymentAmount());
		}
		if (left.isGreaterThan(Money.ZERO)) {
			for (InvoiceNode invoiceNode: getModel().getPaymentPlanInvoices()) {
				Money payNow = invoiceNode.getInvoice().getTotalGst().subtract(invoiceNode.getMinPaymentAmount()).subtract(left);
				if (payNow.isNegative()) {
					invoiceNode.setPaymentAmount(invoiceNode.getInvoice().getTotalGst());
					left = Money.ZERO.subtract(payNow);
				} else {
					invoiceNode.setPaymentAmount(invoiceNode.getMinPaymentAmount().add(left));
					break;
				}
			}
		}
	}

	@Override
	protected void parse() {
	}

	@Override
	protected boolean validate() {
		GetAmounts amounts = GetAmounts.valueOf(getController()).get();
		if (payNow == null || payNow.isGreaterThan(getModel().getTotalGst()) || payNow.isLessThan(amounts.getMinTotalAmount())) {
			MoneyFormatter moneyFormatter = MoneyFormatter.getInstance();
			getController().addError(PurchaseController.Message.payNowWrong, moneyFormatter.valueToString(amounts.getTotalAmount()) ,
					moneyFormatter.valueToString(getModel().getTotalGst()));
			return false;
		}
		return true;
	}

	public void setPayNow(Money payNow) {
		this.payNow = payNow;
	}
}