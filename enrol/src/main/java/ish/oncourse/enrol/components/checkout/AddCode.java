package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.pages.Checkout;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class AddCode {

	public static final String FIELD_ADD_CODE = "add_code";
	@Parameter(required = true)
	private PurchaseController purchaseController;

	@Inject
	private Request request;

	@InjectPage
	private Checkout checkout;

	@OnEvent(value = "addCodeEvent")
	public Object addCode()
	{
		if (!request.isXHR())
			return null;

		String code = StringUtils.trimToEmpty(request.getParameter(FIELD_ADD_CODE));
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.addDiscount);
		actionParameter.setValue(code);
		purchaseController.performAction(actionParameter);
		return checkout.getCheckoutBlock();
	}
}
