package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.model.Discount;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class AddCode {

	public static final String FIELD_ADD_CODE = "add_code";

    public static final String MESSAGE_KEY_discountAdded = "message-discountAdded";

	@Parameter(required = true)
	@Property
	private PurchaseController purchaseController;

	@Property
	private Discount discount;

    @Parameter(required = true)
    @Property
    private Block blockToRefresh;

    @Inject
	private Request request;

    @Inject
    private Messages messages;

	@OnEvent(value = "addCodeEvent")
	public Object addCode()
	{
		if (!request.isXHR())
			return null;

		String code = StringUtils.trimToEmpty(request.getParameter(FIELD_ADD_CODE));
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.addCode);
		actionParameter.setValue(code);
		purchaseController.performAction(actionParameter);
		return blockToRefresh;
	}

    public String getDiscountAddedMessage()
    {
        return messages.format(MESSAGE_KEY_discountAdded,discount.getCode());
    }
}
