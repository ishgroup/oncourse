package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.utils.PurchaseController;
import ish.oncourse.enrol.utils.PurchaseController.Action;
import ish.oncourse.enrol.utils.PurchaseController.ActionParameter;
import ish.oncourse.model.Contact;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.Request;

import java.net.MalformedURLException;
import java.net.URL;

public class SelectPayer {
	public static final String PROPERTY_CONTACT_FULL_NAME = "fullName";
	
	@Parameter(required = true)
	@Property
	private PurchaseController purchaseController;
	
	@Inject
	private PropertyAccess propertyAccess;
	
	@Inject
    private Request request;
	
	@Property
	private Contact contact;

	@Property
	private Integer index;

	@Parameter(required = false)
	@Property
	private String componentForRerender;
		
	@SetupRender
	void beforeRender() {
	}
	
	@OnEvent(value = EventConstants.VALUE_CHANGED, component = "currentPayer")
    public Object updatePayer(Contact actualPayer) throws MalformedURLException {
		if (!request.isXHR()) {
			return new URL(request.getServerName());
		}
		ActionParameter actionParameter = new ActionParameter(Action.CHANGE_PAYER);
		actionParameter.setValue(actualPayer);
		purchaseController.performAction(actionParameter);
		return componentForRerender != null ? componentForRerender : this;
    }

	public String getItemClass()
	{
		return contact.equals(purchaseController.getModel().getPayer()) ? "payer":"";
	}
}
