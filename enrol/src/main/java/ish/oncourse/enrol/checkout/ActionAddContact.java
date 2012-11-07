package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.model.Contact;
import ish.oncourse.services.preference.ContactFieldHelper;

import static ish.oncourse.enrol.checkout.PurchaseController.Error.contactAlreadyAdded;
import static ish.oncourse.enrol.checkout.PurchaseController.State.*;

public class ActionAddContact extends APurchaseAction {

	private Contact contact;
	private ContactCredentials contactCredentials;

	@Override
	protected void makeAction() {
		if (getController().getState().equals(addContact)) {
			boolean isAllRequiredFieldFilled = new ContactFieldHelper(getController().getPreferenceController()).isAllRequiredFieldFilled(contact);
			if (contact.getObjectId().isTemporary() || !isAllRequiredFieldFilled) {
				getController().prepareContactEditor(contact, !isAllRequiredFieldFilled);
				getController().setState(editContact);
			} else {
				getController().addContactToModel(contact);
				getController().setState(editCheckout);
			}
		} else if (getController().getState().equals(editContact)) {
			getController().addContactToModel(contact);
			getController().setState(editCheckout);
		} else
			throw new IllegalStateException();

	}

	@Override
	protected void parse() {
		if (getParameter() != null) {
			if (getController().getState() == addContact) {
				contactCredentials = getParameter().getValue(ContactCredentials.class);
				ContactCredentialsEncoder contactCredentialsEncoder = new ContactCredentialsEncoder();
				contactCredentialsEncoder.setContactCredentials(contactCredentials);
				contactCredentialsEncoder.setPurchaseController(getController());
				contactCredentialsEncoder.encode();
				contact = contactCredentialsEncoder.getContact();
			} else {
				contact = getParameter().getValue(Contact.class);
			}
		}
	}

	@Override
	protected boolean validate() {
		if (getController().getState() == addContact) {
			ContactCredentials contactCredentials = getParameter().getValue(ContactCredentials.class);
			if (getModel().containsContactWith(contactCredentials)) {

				getController().addError(contactAlreadyAdded);

				return false;
			}
			return true;
		} else
			return getController().getState() == editContact;
	}
}
