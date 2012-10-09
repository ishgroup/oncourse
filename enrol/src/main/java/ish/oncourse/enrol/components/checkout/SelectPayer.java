package ish.oncourse.enrol.components.checkout;

import java.net.MalformedURLException;
import java.net.URL;

import ish.oncourse.enrol.utils.PurchaseController;
import ish.oncourse.enrol.utils.PurchaseController.Action;
import ish.oncourse.enrol.utils.PurchaseController.ActionParameter;
import ish.oncourse.model.Contact;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.Request;

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
	private Contact currentSelection;
	
	@InjectComponent
	private Zone contactsZone;
	
	@Parameter(required = false)
	@Property
	private String componentForRerender;
		
	@SetupRender
	void beforeRender() {
		currentSelection = purchaseController.getModel().getPayer();
		if (StringUtils.trimToNull(componentForRerender) == null) {
			componentForRerender = null;
		}
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
		
	public ListSelectModel<Contact> getPayersModel() {
		return new ListSelectModel<Contact>(purchaseController.getModel().getContacts(), PROPERTY_CONTACT_FULL_NAME, propertyAccess);
	}

	public ListValueEncoder<Contact> getPayersEncoder() {
		return new ListValueEncoder<Contact>(purchaseController.getModel().getContacts(), Contact.ID_PK_COLUMN, propertyAccess);
	}

}
