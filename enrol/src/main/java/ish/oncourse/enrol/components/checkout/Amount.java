package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.pages.Checkout;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Amount {

	public static final String FIELD_PASSWORD= "password";
	@Property
	@Parameter (required = true)
	private PurchaseController purchaseController;

	@Inject
	private Request request;

	@InjectPage
	private Checkout checkout;

	@OnEvent(value = "creditAccessEvent")
	public Object creditAccess()
	{
		if (!request.isXHR())
			return null;

		String password = StringUtils.trimToNull(request.getParameter(FIELD_PASSWORD));
		if (password != null)
		{
			PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.CREDIT_ACCESS);
			actionParameter.setValue(password);
			purchaseController.performAction(actionParameter);
			return checkout.getCheckoutBlock();
		}
		return null;
	}
}
