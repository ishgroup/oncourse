package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.enrol.checkout.ConcessionDelegate;
import ish.oncourse.enrol.checkout.IDelegate;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CustomField;
import ish.oncourse.services.preference.ContactFieldHelper;

import java.util.List;

public interface ContactEditorDelegate extends IDelegate {

	Contact getContact();

	boolean isFillRequiredProperties();

	void saveContact();

	List<String> getVisibleFields();

	void cancelContact();

	ContactFieldHelper getContactFieldHelper();

	boolean isActiveConcessionTypes();

	ConcessionDelegate  getConcessionDelegate();
	
	List<CustomField> getCustomFields();
}
