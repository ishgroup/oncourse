package ish.oncourse.enrol.components.checkout.payment;

import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.enrol.pages.Payment;
import ish.oncourse.model.Application;
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
	private Payment paymentPage;

	@InjectPage
	private Checkout checkoutPage;
	
	@Property
	private Application application;

	public PaymentResult() {
	}

	public PaymentIn getPaymentIn()
	{
		return delegate.getPaymentIn();
	}

	public String getCoursesLink() {
		return checkoutPage.getCoursesLink();
	}

	public String getSuccessUrl() {
		String url = preferenceController.getEnrolSuccessUrl();
		return url != null ? url : getCoursesLink();
	}

	@OnEvent(value = "abandon")
	public Object abandon()
	{
		delegate.abandon();
		return paymentPage;
	}

	@OnEvent(value = "tryAgain")
	public Object tryAgain()
	{
		delegate.tryAgain();
		return paymentPage;
	}

	public String getMessageThanksForEnrolment()
	{
		return messages.format("message-thanksForEnrolment", delegate.getCollege().getName());
	}

	public String getMessageEnrolmentFailed()
	{
		return messages.format("message-enrolmentFailed", delegate.getCollege().getName());
	}

	public String getMessagePaymentFailed()
	{
		return messages.format("message-paymentFailed", delegate.getCollege().getName());
	}

    @AfterRender
    void afterRender() {
        //the code should be run from the component becase we should rederer the result fist and then reset everithing
        checkoutPage.finalizingProcess();
    }
}
