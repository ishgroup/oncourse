package ish.oncourse.enrol.components.checkout.payment;

import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.enrol.pages.Payment;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class PaymentResult {

	@Parameter(required = true)
	@Property
	private PaymentEditorDelegate delegate;

	@Inject
	private PreferenceController preferenceController;

	@Inject
	private Request request;

	@Inject
	private Messages messages;

	@InjectPage
	private Payment payment;

	@InjectPage
	private Checkout checkout;

	public PaymentResult() {
	}

	public PaymentIn getPaymentIn()
	{
		return delegate.getPaymentIn();
	}

	public String getCoursesLink() {
		return checkout.getCoursesLink();
	}

	public String getSuccessUrl() {
		String url = preferenceController.getEnrolSuccessUrl();
		return url != null ? url : getCoursesLink();
	}

	@OnEvent(value = "abandon")
	public Object abandon()
	{
		delegate.abandon();
		return payment;
	}

	@OnEvent(value = "tryAgain")
	public Object tryAgain()
	{
		delegate.tryAgain();
		return payment;
	}

	@AfterRender
	public void afterRender() {
		if (checkout.getPurchaseController() != null && checkout.getPurchaseController().isFinished())
			checkout.resetPersistProperties();
	}

	public String getMessageThanksForEnrolment()
	{
		return messages.format("thanksForEnrolment", getPaymentIn().getCollege().getName());
	}

	public String getMessageEnrolmentFailed()
	{
		return messages.format("enrolmentFailed", getPaymentIn().getCollege().getName());
	}

	public String getMessagePaymentFailed()
	{
		return messages.format("paymentFailed", getPaymentIn().getCollege().getName());
	}
}
