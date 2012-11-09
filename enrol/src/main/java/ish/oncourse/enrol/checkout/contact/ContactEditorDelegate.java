package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.enrol.checkout.IDelegate;
import ish.oncourse.model.Contact;

import java.util.List;

public interface ContactEditorDelegate extends IDelegate {

	Contact getContact();

	boolean isFillRequiredProperties();

	void saveContact();

	List<String> getVisibleFields();

	void cancelContact();


}
