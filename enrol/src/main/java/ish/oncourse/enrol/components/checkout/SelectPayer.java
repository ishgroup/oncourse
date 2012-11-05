package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseController.Action;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import ish.oncourse.model.Contact;
import org.apache.tapestry5.Block;
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
	public static final String CLASS_payer = "payer";
	public static final String CLASS_contact = "contact";

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
	private Block blockToRefresh;
		
	@SetupRender
	void beforeRender() {
	}

    public Object onActionFromUpdatePayer(Integer contactIndex) throws MalformedURLException {
		if (!request.isXHR() ) {
			return new URL(request.getServerName());
		}
		Contact selectedPayer = purchaseController.getModel().getContacts().get(contactIndex);
		if (selectedPayer != purchaseController.getModel().getPayer())
		{
			ActionParameter actionParameter = new ActionParameter(Action.changePayer);
			actionParameter.setValue(selectedPayer);
			purchaseController.performAction(actionParameter);
		}
		return blockToRefresh != null ? blockToRefresh : this;
    }

	public String getItemClass()
	{
		return contact.equals(purchaseController.getModel().getPayer()) ? CLASS_payer:CLASS_contact;
	}
}

