package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.PurchaseController;
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

    public static final String MESSAGE_KEY_successMessage = "message-successMessage";

	@Parameter(required = true)
	private PurchaseController purchaseController;

    @Parameter(required = true)
    @Property
    private Block blockToRefresh;

    @Inject
	private Request request;

    @Inject
    private Messages messages;

    private String lastAddDiscountCode;


	@OnEvent(value = "addCodeEvent")
	public Object addCode()
	{
		if (!request.isXHR())
			return null;

		String code = StringUtils.trimToEmpty(request.getParameter(FIELD_ADD_CODE));
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.addDiscount);
		actionParameter.setValue(code);
		purchaseController.performAction(actionParameter);
        if (purchaseController.getErrors().isEmpty())
            lastAddDiscountCode = code;
		return blockToRefresh;
	}

    public String getSuccessMessage()
    {
        if (lastAddDiscountCode != null)
            return messages.format(MESSAGE_KEY_successMessage,lastAddDiscountCode);
        else
            return null;
    }
}
