package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.model.Contact;

import java.util.List;

public interface ContactEditorDelegate {

	Contact getContact();

	boolean isFillRequiredProperties();

	void saveContact();

	List<String> getVisibleFields();

	void cancelContact();


}
