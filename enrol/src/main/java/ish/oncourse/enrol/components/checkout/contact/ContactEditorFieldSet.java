package ish.oncourse.enrol.components.checkout.contact;

import ish.oncourse.enrol.checkout.contact.ContactEditorDelegate;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.model.Country;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.MessagesNamingConvention;
import ish.oncourse.util.ValidateHandler;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.DateFormat;
import java.util.Date;

import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor;

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
	private String fieldName;

	@Inject
	private Messages messages;


	public Messages getMessages() {
		return messages;
	}

	public ContactFieldHelper getContactFieldHelper() {
		return delegate.getContactFieldHelper();
	}

	public boolean required(String fieldName) {
		return getContactFieldHelper().isRequiredField(FieldDescriptor.valueOf(fieldName));
	}


	public String getValue() {
		FieldDescriptor fieldDescriptor = FieldDescriptor.valueOf(fieldName);

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


	public String getCountry() {
		Country country = delegate.getContact().getCountry();
		if (country == null) {
			return ICountryService.DEFAULT_COUNTRY_NAME;
		}
		return country.getName();
	}

	public void setCountry(String value) {
	}

	public String hint(String fieldName)
	{
		return messages.get(String.format(MessagesNamingConvention.HINT_KEY_TEMPLATE, fieldName));
	}

	public String label(String fieldName) {
		return messages.get(String.format(MessagesNamingConvention.LABEL_KEY_TEMPLATE, fieldName));
	}


	public DateFormat getDateFormat() {
		return FormatUtils.getDateFormat(Checkout.DATE_FIELD_SHOW_FORMAT, delegate.getContact().getCollege().getTimeZone());
	}
}
