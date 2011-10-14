package ish.oncourse.webservices.components;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.webservices.pages.Payment;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PaymentResult {

	@Parameter
	private PaymentIn payment;

	private boolean isTryOtherCard;

	private boolean isAbandonReverse;

	@Inject
	private ComponentResources componentResources;

	public boolean isPaymentFailed() {
		return payment.getStatus() != PaymentStatus.SUCCESS;
	}

	public boolean isPaymentStatusNodeNullTransactionResponse() {
		return payment.getStatusNotes().equals("Null transaction response");
	}

	private void cleanSelections() {
		this.isTryOtherCard = this.isAbandonReverse = false;
	}

	@OnEvent(component = "tryOtherCard", value = "selected")
	void submitTryOtherCard() {
		cleanSelections();
		this.isTryOtherCard = true;
	}

	@OnEvent(component = "abandonReverse", value = "selected")
	void submitAbandonAndReverse() {
		cleanSelections();
		this.isAbandonReverse = true;
	}

	@OnEvent(component = "paymentResultForm", value = "success")
	Object submitted() {
		
		Payment paymentPage = (Payment) componentResources.getPage();

		if (isTryOtherCard) {
			return paymentPage.tryOtherCard();
		} else if (isAbandonReverse) {
			return paymentPage.abandonPaymentReverseInvoice();
		} else {
			return paymentPage.abandonPaymentKeepInvoice();
		}
	}
}
