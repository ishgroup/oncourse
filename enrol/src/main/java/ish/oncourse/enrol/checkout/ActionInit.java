package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.model.Contact;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.addContact;

public class ActionInit extends APurchaseAction {
	
	private Contact contact;
	
	@Override
	protected void makeAction() {
		getController().getVoucherRedemptionHelper().setInvoice(getModel().getInvoice());
		
		PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(addContact);		
		if (contact != null) {
			ContactCredentials credentials = new ContactCredentials();
			credentials.setFirstName(contact.getGivenName());
			credentials.setLastName(contact.getFamilyName());
			credentials.setEmail(contact.getEmailAddress());
			parameter.setValue(credentials);
			getController().setState(PurchaseController.State.addContact);
			
		}
		getController().performAction(parameter);
	}

	@Override
	protected void parse() {
		if (getParameter().hasValues()) {
			contact = getParameter().getValue(Contact.class);
		}
	}

	@Override
	protected boolean validate() {
		if (getModel().getClasses().size() < 1 && getModel().getProducts().size() < 1)
		{
			getController().addError(PurchaseController.Message.noSelectedItemForPurchase);
			getController().setState(PurchaseController.State.paymentResult);
			return false;
		}
		return true;
	}
}
