package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.model.Contact;

public interface ContactEditorDelegate {

	Contact getContact();

	boolean isFillRequiredProperties();
}
