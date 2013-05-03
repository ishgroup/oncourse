package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.util.HTMLUtils;
import ish.oncourse.util.MessagesNamingConvention;
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
	private ICountryService countryService;


	private static final String KEY_ERROR_MESSAGE_fieldRequired = "message-fieldRequired";
	private static final String KEY_ERROR_MESSAGE_birthdate_hint = "message-birthdateHint";

	static final String KEY_ERROR_dateOfBirth_youngAge = "message-dateOfBirth-youngAge";
    static final String KEY_ERROR_dateOfBirth_shouldBeInPast = "message-dateOfBirth-shouldBeInPast";
	static final String KEY_ERROR_error_countryOfBirth = "message-countryOfBirth";

	private List<String> visibleFields;

	private Map<String, String> errors = new HashMap<>();


	public void parse() {
		parseContactFields();
		validateContactFields();
		Boolean value = parseBooleanParameter(Contact.IS_MALE_PROPERTY);
		if (value != null)
			contact.setIsMale(value);
		parseMarketingFields();
	}

	private void validateContactFields() {
		for (String visibleField : visibleFields) {
			FieldDescriptor fieldDescriptor = FieldDescriptor.valueOf(visibleField);
			String error = validate(fieldDescriptor);
			if (error != null) {
				errors.put(fieldDescriptor.propertyName, error);
			}
		}
	}


	void parseMarketingFields() {
		Boolean value = parseBooleanParameter(Contact.IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY);
		if (value != null)
			contact.setIsMarketingViaEmailAllowed(value);
		value = parseBooleanParameter(Contact.IS_MARKETING_VIA_POST_ALLOWED_PROPERTY);
		if (value != null)
			contact.setIsMarketingViaPostAllowed(value);
		value = parseBooleanParameter(Contact.IS_MARKETING_VIA_SMSALLOWED_PROPERTY);
		if (value != null)
			contact.setIsMarketingViaSMSAllowed(value);
	}

	private Boolean parseBooleanParameter(String parameterName) {
        String value = StringUtils.trimToNull(request.getParameter(parameterName));
        if (value == null)
            return null;
        else
		    return HTMLUtils.parserBooleanValue(value);
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

			if (fieldDescriptor.propertyClass == Country.class)
			{
				if (stringValue == null)
					value = countryService.getCountryByName(ICountryService.DEFAULT_COUNTRY_NAME);
				else
				{
					value = countryService.getCountryByName(stringValue);
					if (value == null)
					{
						errors.put(fieldDescriptor.propertyName, messages.format(KEY_ERROR_error_countryOfBirth,stringValue));
						value = countryService.getCountryByName(ICountryService.DEFAULT_COUNTRY_NAME);
					}
				}
			}

			contact.writeProperty(fieldDescriptor.propertyName, value);
		}
	}

	private String getRequiredMessage(FieldDescriptor fieldDescriptor) {
		return messages.format(KEY_ERROR_MESSAGE_fieldRequired,
                messages.get(String.format(MessagesNamingConvention.LABEL_KEY_TEMPLATE, fieldDescriptor.propertyName)));
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

	/**
	 * postcode
	 * homePhoneNumber
	 * businessPhoneNumber
	 * faxNumber
	 * mobilePhoneNumber
	 */
	String validate(FieldDescriptor fieldDescriptor) {
		boolean defaultCountry = ICountryService.DEFAULT_COUNTRY_NAME.equals(contact.getCountry().getName());

		switch (fieldDescriptor) {
			case street:
				return null;
			case suburb:
				return contact.validateSuburb();
			case state:
				return contact.validateState();
			case postcode:
				return defaultCountry ? contact.validatePostcode(): null;
			case homePhoneNumber:
				return defaultCountry ? contact.validateHomePhone(): null;
			case businessPhoneNumber:
				return defaultCountry ? contact.validateBusinessPhone(): null;
			case faxNumber:
				return defaultCountry ? contact.validateFax(): null;
			case mobilePhoneNumber:
				return defaultCountry ? contact.validateMobilePhone(): null;
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
			case country:
				return null;
			default:
				throw new IllegalArgumentException();
		}
	}

	public void setCountryService(ICountryService countryService) {
		this.countryService = countryService;
	}
}
