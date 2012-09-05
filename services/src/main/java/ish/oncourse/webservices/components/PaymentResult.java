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
	private Exception unexpectedException;

	@Inject
	private ComponentResources componentResources;
	
	public boolean isPaymentFailed() {
		return isFailedPayment(payment) && !Payment.isPaymentCanceled(payment);
	}
	
	public static boolean isFailedPayment(final PaymentIn paymentIn) {
		return PaymentStatus.STATUSES_FAILED.contains(paymentIn.getStatus());
	}
	
	public static boolean isNewPayment(final PaymentIn paymentIn) {
		return PaymentStatus.IN_TRANSACTION.equals(paymentIn.getStatus()) || PaymentStatus.CARD_DETAILS_REQUIRED.equals(paymentIn.getStatus());
	}
	
	public static boolean isSuccessPayment(final PaymentIn paymentIn) {
		return PaymentStatus.SUCCESS.equals(paymentIn.getStatus());
	}
	
	public boolean isNotProcessed() {
		return isNewPayment(payment);
	}
	
	public boolean isPaymentSuccess() {
		return isSuccessPayment(payment);
	}

	public boolean isPaymentStatusNodeNullTransactionResponse() {
		return "Null transaction response".equals(payment.getStatusNotes());
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

	/**
	 * @return the unexpectedException
	 */
	public Exception getUnexpectedException() {
		return unexpectedException;
	}

	/**
	 * @param unexpectedException the unexpectedException to set
	 */
	public void setUnexpectedException(Exception unexpectedException) {
		this.unexpectedException = unexpectedException;
	}
}
