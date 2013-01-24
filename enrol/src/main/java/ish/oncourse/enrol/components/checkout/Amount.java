package ish.oncourse.enrol.components.checkout;

import ish.math.Money;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.Format;

public class Amount {

	public static final String FIELD_PASSWORD = "password";
	@Property
	@Parameter(required = true)
	private PurchaseController purchaseController;

	@Parameter(required = true)
	@Property
	private Block blockToRefresh;

	@Property
	@Parameter
	private boolean showPayerFields;

	@Inject
	private Request request;


	@OnEvent(value = "creditAccessEvent")
	public Object creditAccess() {
		if (!request.isXHR())
			return null;

		String password = StringUtils.trimToEmpty(request.getParameter(FIELD_PASSWORD));
        PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.creditAccess);
        actionParameter.setValue(password);
        purchaseController.performAction(actionParameter);
        return blockToRefresh;
	}

	@OnEvent(value = "removeOwingEvent")
	public Object removeOwing() {
		if (!request.isXHR())
			return null;

		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.owingApply);
		purchaseController.performAction(actionParameter);
		return blockToRefresh;
	}

	public Format moneyFormat(Money money)
	{
		return FormatUtils.chooseMoneyFormat(money);
	}
}
