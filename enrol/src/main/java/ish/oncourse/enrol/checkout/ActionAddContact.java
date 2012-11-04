package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.model.Contact;
import ish.oncourse.services.preference.ContactFieldHelper;

import static ish.oncourse.enrol.checkout.PurchaseController.State.*;

public class ActionAddContact extends APurchaseAction {

	public static final String ERROR_KEY_contactAlreadyAdded = "error-contactAlreadyAdded";
	private Contact contact;
	private ContactCredentials contactCredentials;

	@Override
	protected void makeAction() {
		if (getController().getState().equals(ADD_CONTACT)) {
			boolean isAllRequiredFieldFilled = new ContactFieldHelper(getController().getPreferenceController()).isAllRequiredFieldFilled(contact);
			if (contact.getObjectId().isTemporary() || !isAllRequiredFieldFilled) {
				getController().prepareContactEditor(contact, !isAllRequiredFieldFilled);
				getController().setState(EDIT_CONTACT);
			} else {
				getController().addContactToModel(contact);
				getController().setState(EDIT_CHECKOUT);
			}
		} else if (getController().getState().equals(EDIT_CONTACT)) {
			getController().addContactToModel(contact);
			getController().setState(EDIT_CHECKOUT);
		} else
			throw new IllegalStateException();

	}

	@Override
	protected void parse() {
		if (getParameter() != null) {
			if (getController().getState() == ADD_CONTACT) {
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
		if (getController().getState() == ADD_CONTACT) {
			ContactCredentials contactCredentials = getParameter().getValue(ContactCredentials.class);
			if (getModel().containsContactWith(contactCredentials)) {

				getController().getErrors().add(getController().getMessages().get(ERROR_KEY_contactAlreadyAdded));

				return false;
			}
			return true;
		} else
			return getController().getState() == EDIT_CONTACT;
	}
}
