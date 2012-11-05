package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.model.Contact;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class ContactItem {
	@Parameter(required = true)
	@Property
	private PurchaseController purchaseController;

	@Parameter(required = true)
	@Property
	private Contact contact;

	@Parameter(required = true)
	@Property
	private Integer index;


	@Parameter(required = false)
	@Property
	private Block blockToRefresh;


	@Inject
	private Request request;

	public boolean isPayer()
	{
		return purchaseController.getModel().getPayer().equals(contact);
	}

	public Object onActionFromEditConcessionLink(Integer contactIndex)
	{
		if (!request.isXHR())
			return null;
		Contact contact = purchaseController.getModel().getContacts().get(contactIndex);
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.startConcessionEditor);
		actionParameter.setValue(contact);
		purchaseController.performAction(actionParameter);
		if (blockToRefresh != null)
			return blockToRefresh;
		return null;
	}
}
