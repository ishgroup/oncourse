package ish.oncourse.enrol.components.checkout.payment;

import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
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

	@OnEvent(value = "abandonEvent")
	public Object abandon()
	{
		if (!request.isXHR())
			return null;
		delegate.abandon();
		return checkout.getCheckoutBlock();
	}

	@OnEvent(value = "tryAgainEvent")
	public Object tryAgain()
	{
		if (!request.isXHR())
			return null;
		delegate.tryAgain();
		return checkout.getCheckoutBlock();
	}

}
