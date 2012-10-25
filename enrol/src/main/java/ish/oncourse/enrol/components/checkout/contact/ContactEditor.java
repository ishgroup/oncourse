package ish.oncourse.enrol.components.checkout.contact;

import ish.oncourse.enrol.checkout.contact.ContactEditorDelegate;
import ish.oncourse.model.Contact;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ContactEditor {

	@Parameter(required = true)
	private ContactEditorDelegate delegate;


	@Inject
	private PreferenceController preferenceController;

	@Property
	private ContactFieldHelper contactFieldHelper = new ContactFieldHelper(preferenceController);

	@Inject
	private Messages messages;

	@Parameter
	private Block blockToRefresh;

	public boolean isShowAdditionalInfoMessage() {
		return delegate.isFillRequiredProperties();
	}

	public Contact getContact() {
		return delegate.getContact();
	}

	public boolean visible(String fieldName)
	{
		if (!delegate.isFillRequiredProperties())
			return contactFieldHelper.isShowField(ContactFieldHelper.FieldDescriptor.valueOf(fieldName));
		else
			return contactFieldHelper.isRequiredField(ContactFieldHelper.FieldDescriptor.valueOf(fieldName)) && delegate.getContact().readProperty(fieldName) == null;
	}

	public boolean required(String fieldName)
	{
		return contactFieldHelper.isRequiredField(ContactFieldHelper.FieldDescriptor.valueOf(fieldName));
	}
}
