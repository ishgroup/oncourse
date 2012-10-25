package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.model.Contact;

public class ContactEditorController implements ContactEditorDelegate {

	private Contact contact;

	private boolean fillRequiredProperties;
	@Override
	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@Override
	public boolean isFillRequiredProperties() {
		return fillRequiredProperties;
	}


	public void setFillRequiredProperties(boolean fillRequiredProperties) {
		this.fillRequiredProperties = fillRequiredProperties;
	}
}
