package ish.oncourse.enrol.components.checkout.contact;

import ish.oncourse.enrol.checkout.ValidateHandler;
import ish.oncourse.enrol.checkout.contact.ContactEditorDelegate;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.DateFormat;
import java.util.Date;

import static ish.oncourse.enrol.checkout.contact.ContactEditorParser.LABEL_TEMPLATE;
import static ish.oncourse.enrol.pages.Checkout.DATE_FIELD_FORMAT;

public class ContactEditorFieldSet {

	@Parameter(required = true)
	@Property
	private ValidateHandler validateHandler;

	@Parameter(required = true)
	@Property
	private ContactEditorDelegate delegate;

	@Inject
	private PreferenceController preferenceController;

	@Property
	private ContactFieldHelper contactFieldHelper = new ContactFieldHelper(preferenceController);

	@Property
	private String fieldName;

	@Inject
	private Messages messages;


	public Messages getMessages()
	{
		return messages;
	}

	public boolean required(String fieldName)
	{
		return contactFieldHelper.isRequiredField(ContactFieldHelper.FieldDescriptor.valueOf(fieldName));
	}


	public String getValue()
	{
		ContactFieldHelper.FieldDescriptor fieldDescriptor = ContactFieldHelper.FieldDescriptor.valueOf(fieldName);

		Object value = delegate.getContact().readProperty(fieldName);
		if (fieldDescriptor.propertyClass == Date.class && value != null)
			value = getDateFormat().format((Date) value);
		return value == null ? FormatUtils.EMPTY_STRING : value.toString();
	}


	public String getDateOfBirth() {
		Date dateOfBirth = delegate.getContact().getDateOfBirth();
		if (dateOfBirth == null) {
			return null;
		}
		return getDateFormat().format(dateOfBirth);
	}

	/**
	 * The method needs only to allow use the property as value parameter in tapestry textfield.
	 */
	public void setDateOfBirth(String dateOfBirth) {
	}

	public String label(String fieldName)
	{
		return messages.get(String.format(LABEL_TEMPLATE, fieldName));
	}


	public DateFormat getDateFormat()
	{
		return  FormatUtils.getDateFormat(DATE_FIELD_FORMAT, delegate.getContact().getCollege().getTimeZone());
	}
}
