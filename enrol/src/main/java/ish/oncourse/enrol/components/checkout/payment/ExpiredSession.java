package ish.oncourse.enrol.components.checkout.payment;

import ish.oncourse.enrol.pages.Checkout;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectPage;

/**
 * The class was introduced to show expired session message and reset persist properties after the message is rendered.
 */
public class ExpiredSession {

	@InjectPage
	private Checkout checkoutPage;

	@AfterRender
	void afterRender() {
		checkoutPage.resetPersistProperties();
	}

	public String getCoursesLink()
	{
		return checkoutPage.getCoursesLink();
	}
}
