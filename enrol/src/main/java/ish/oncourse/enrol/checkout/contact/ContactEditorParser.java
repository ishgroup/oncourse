package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.enrol.checkout.HTMLUtils;
import ish.oncourse.model.Contact;
import ish.oncourse.services.preference.ContactFieldHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.Request;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor;

public class ContactEditorParser {
	private Request request;
	private Contact contact;
	private ContactFieldHelper contactFieldHelper;
	private Messages messages;
	private DateFormat dateFormat;

	public static final String LABEL_TEMPLATE = "label-%s";

	private static final String KEY_ERROR_MESSAGE_required = "required";
	private static final String KEY_ERROR_MESSAGE_birthdate_hint = "birthdate-hint";

	static final String KEY_ERROR_dateOfBirth_youngAge = "error-dateOfBirth-youngAge";
    static final String KEY_ERROR_dateOfBirth_shouldBeInPast = "error-dateOfBirth-shouldBeInPast";

	private List<String> visibleFields;

	private Map<String, String> errors = new HashMap<String, String>();


	public void parse() {
		parseContactFields();
		contact.writeProperty(Contact.IS_MALE_PROPERTY, parseBooleanParameter(Contact.IS_MALE_PROPERTY));
		parseMarketingFields();
	}


	void parseMarketingFields() {
		contact.writeProperty(Contact.IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY, parseBooleanParameter(Contact.IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY));
		contact.writeProperty(Contact.IS_MARKETING_VIA_POST_ALLOWED_PROPERTY, parseBooleanParameter(Contact.IS_MARKETING_VIA_POST_ALLOWED_PROPERTY));
		contact.writeProperty(Contact.IS_MARKETING_VIA_SMSALLOWED_PROPERTY, parseBooleanParameter(Contact.IS_MARKETING_VIA_SMSALLOWED_PROPERTY));
	}

	private boolean parseBooleanParameter(String parameterName) {
		return HTMLUtils.parserBooleanValue(StringUtils.trimToNull(request.getParameter(parameterName)));
	}


	void parseContactFields() {
		for (String visibleField : visibleFields) {
			FieldDescriptor fieldDescriptor = FieldDescriptor.valueOf(visibleField);
			String stringValue = StringUtils.trimToNull(request.getParameter(visibleField));
            Object value = stringValue;
			if (stringValue == null) {
				if (contactFieldHelper.isRequiredField(fieldDescriptor))
					errors.put(fieldDescriptor.propertyName, getRequiredMessage(fieldDescriptor));
			} else if (fieldDescriptor.propertyClass == Date.class) {
				try {
					value = dateFormat.parse(stringValue);
				} catch (ParseException e) {
                    value = null;
					errors.put(fieldDescriptor.propertyName, messages.get(KEY_ERROR_MESSAGE_birthdate_hint));
				}
			}
            contact.writeProperty(fieldDescriptor.propertyName, value);
            String error = validate(fieldDescriptor);
            if (error != null) {
                errors.put(fieldDescriptor.propertyName, error);
            }
		}
	}

	private String getRequiredMessage(FieldDescriptor fieldDescriptor) {
		return messages.format(KEY_ERROR_MESSAGE_required, messages.get(String.format(LABEL_TEMPLATE, fieldDescriptor.propertyName)));
	}


	public void setRequest(Request request) {
		this.request = request;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

    Contact getContact()
    {
        return contact;
    }

	public Map<String, String> getErrors() {
		return errors;
	}


	public void setContactFieldHelper(ContactFieldHelper contactFieldHelper) {
		this.contactFieldHelper = contactFieldHelper;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}


	public void setVisibleFields(List<String> visibleFields) {
		this.visibleFields = visibleFields;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}


	String validate(FieldDescriptor fieldDescriptor) {

		switch (fieldDescriptor) {
			case street:
				return null;
			case suburb:
				return contact.validateSuburb();
			case state:
				return contact.validateState();
			case postcode:
				return contact.validatePostcode();
			case homePhoneNumber:
				return contact.validateHomePhone();
			case businessPhoneNumber:
				return contact.validateBusinessPhone();
			case faxNumber:
				return contact.validateFax();
			case mobilePhoneNumber:
				return contact.validateMobilePhone();
			case dateOfBirth:
				String error = contact.validateBirthDate();
				if (error == null)
				{
					Date date  = contact.getDateOfBirth();
                    if (date != null)
                    {
                        Integer minAge = contactFieldHelper.getPreferenceController().getEnrolmentMinAge();

                        Integer age = Years.yearsBetween(new DateTime(date.getTime()), new DateTime(new Date().getTime())).getYears();
                        if (minAge > age)
                            return messages.format(KEY_ERROR_dateOfBirth_youngAge, minAge);
                        if (date.compareTo(new Date()) > -1)
                            return messages.format(KEY_ERROR_dateOfBirth_shouldBeInPast);
                    }
				}
				return error;
			default:
				throw new IllegalArgumentException();
		}
	}
}
