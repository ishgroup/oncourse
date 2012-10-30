package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.model.Contact;

import java.util.List;
import java.util.Map;

public interface ContactEditorDelegate {

	Contact getContact();

	boolean isFillRequiredProperties();

	void saveContact(Map<String, String> errors);

	List<String> getVisibleFields();

	void cancelContact();


}
