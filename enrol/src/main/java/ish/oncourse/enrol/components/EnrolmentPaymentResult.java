package ish.oncourse.enrol.components;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class EnrolmentPaymentResult {

	@Inject
	private Messages messages;

	@Inject
	private IWebSiteService webSiteService;

	private PaymentIn payment;

	@Property
	private String thxMsg;

	@Property
	private String paymentFailedMsg;

	@Property
	private String enrolmentFailedMsg;

	private String collegeName;

	@SetupRender
	void beforeRender() {
		collegeName = webSiteService.getCurrentCollege().getName();
		thxMsg = messages.format("thanksForEnrolment", collegeName);
		paymentFailedMsg = messages.format("paymentFailed", collegeName);
		enrolmentFailedMsg = messages.format("enrolmentFailed", collegeName);
	}

	public boolean isEnrolmentSuccessful() {
		return PaymentStatus.SUCCESS.equals(payment.getStatus());
	}

	public boolean isEnrolmentQueued() {
		// Payment.STATUS_QUEUED.equals( getResult() ) ||
		// Payment.STATUS_IN_TRANSACTION.equals( getResult() )
		return false;
	}

	public boolean isEnrolmentFailed() {
		// Payment.STATUSES_FAILED.containsObject( getResult() )
		return PaymentStatus.FAILED.equals(payment.getStatus());
	}

	public boolean isPayment() {
		// BigDecimal totalAmount;

		// totalAmount = getPaymentQuery().totalAmount();
		// return totalAmount != null && totalAmount.doubleValue() != 0.00d;
		return true;
	}

	public String getPaymentId() {
		// TODO show proper id
		return "paymentId";
	}

	public void setPayment(PaymentIn payment) {
		this.payment = payment;
		
	}
}
