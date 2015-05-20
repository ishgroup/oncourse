package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.CustomField;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.util.HTMLUtils;
import ish.oncourse.util.MessagesNamingConvention;
import ish.oncourse.utils.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ish.oncourse.enrol.services.Constants.DATE_FIELD_PARSE_FORMAT;
import static ish.oncourse.enrol.services.Constants.MIN_DATE_OF_BIRTH;
import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor;

public class ContactEditorParser {

	private Request request;
	private Contact contact;
	private ContactFieldHelper contactFieldHelper;
	private Messages messages;
	private DateFormat dateFormat = new SimpleDateFormat(DATE_FIELD_PARSE_FORMAT);
	private ICountryService countryService;

	private static final String KEY_ERROR_MESSAGE_fieldRequired = "message-fieldRequired";
	private static final String KEY_ERROR_MESSAGE_birthdate_hint = "message-birthdateHint";
	static final String KEY_ERROR_MESSAGE_birthdate_old = "message-oldbirthdate";

	static final String KEY_ERROR_dateOfBirth_youngAge = "message-dateOfBirth-youngAge";
    static final String KEY_ERROR_dateOfBirth_shouldBeInPast = "message-dateOfBirth-shouldBeInPast";
	static final String KEY_ERROR_error_countryOfBirth = "message-countryOfBirth";

	private List<String> visibleFields;

	private Map<String, String> errors = new HashMap<>();


	public void parse() {
		parseContactFields();
		validateContactFields();
		Boolean value = parseBooleanParameter(Contact.IS_MALE.getName());
		if (value != null)
			contact.setIsMale(value);
		parseMarketingFields();
	}

	private void validateContactFields() {
		for (String visibleField : visibleFields) {
			FieldDescriptor fieldDescriptor = FieldDescriptor.valueOf(visibleField);
			String error = validate(fieldDescriptor);
			if (error != null) {
				errors.put(fieldDescriptor.name(), error);
			}
		}
	}


	void parseMarketingFields() {
		Boolean value = parseBooleanParameter(Contact.IS_MARKETING_VIA_EMAIL_ALLOWED.getName());
		contact.setIsMarketingViaEmailAllowed(value == null ? Boolean.TRUE: value);
		value = parseBooleanParameter(Contact.IS_MARKETING_VIA_POST_ALLOWED.getName());
		contact.setIsMarketingViaPostAllowed(value == null ? Boolean.TRUE: value);
		value = parseBooleanParameter(Contact.IS_MARKETING_VIA_SMSALLOWED.getName());
		contact.setIsMarketingViaSMSAllowed(value == null ? Boolean.TRUE: value);
	}

	private Boolean parseBooleanParameter(String parameterName) {
        String value = StringUtilities.cutToNull(request.getParameter(parameterName));
        if (value == null)
            return null;
        else
		    return HTMLUtils.parserBooleanValue(value);
	}


	void parseContactFields() {
		for (String visibleField : visibleFields) {

			FieldDescriptor fieldDescriptor = FieldDescriptor.valueOf(visibleField);
			String stringValue = StringUtilities.cutToNull(request.getParameter(visibleField));
            Object value = stringValue;

			if (stringValue == null) {
				if (contactFieldHelper.isRequiredField(fieldDescriptor, contact))
					errors.put(fieldDescriptor.name(), getRequiredMessage(fieldDescriptor));
			} else if (fieldDescriptor.propertyClass == Date.class) {
				try {
					value = DateUtils.truncate(dateFormat.parse(stringValue), Calendar.DAY_OF_MONTH);
				} catch (ParseException e) {
                    value = null;
					errors.put(fieldDescriptor.name(), messages.get(KEY_ERROR_MESSAGE_birthdate_hint));
				}
			}

			if (fieldDescriptor.propertyClass == Country.class)
			{
				if (stringValue == null)
					value = getCountryBy(ICountryService.DEFAULT_COUNTRY_NAME);
				else
				{
					value = getCountryBy(stringValue);
					if (value == null) {
						errors.put(fieldDescriptor.name(),
							messages.format(KEY_ERROR_error_countryOfBirth,stringValue));
						value = getCountryBy(ICountryService.DEFAULT_COUNTRY_NAME);
					}
				}
			}

			contact.writeProperty(fieldDescriptor.propertyName, value);
		}
		
		// validate custom fields
		for (CustomField customField : contact.getCustomFields()) {
			String value = StringUtils.trimToNull(customField.getValue());
			
			if (value == null && contactFieldHelper.isCustomFieldRequired(customField)) {
				errors.put(customField.getCustomFieldType().getName(), 
						messages.format(KEY_ERROR_MESSAGE_fieldRequired, customField.getCustomFieldType().getName()));
			}
		}

		/**
		 * I introduced this code to be sure that contact.country field is not empty for any cases.
		 * There are a lot of old contacts which have null value in this field.
		 * When our contact editor requires to fill required fields we can get NullPointerException,
		 * if country field is null.
		 */
		if (contact.getCountry() == null)
		{
			contact.setCountry(getCountryBy(ICountryService.DEFAULT_COUNTRY_NAME));
		}
	}


	private Country getCountryBy(String name)
	{
		Country country = countryService.getCountryByName(name);
		if (country != null)
		{
			country = contact.getObjectContext().localObject(country);
		}
		return country;
	}

	private String getRequiredMessage(FieldDescriptor fieldDescriptor) {
		return messages.format(KEY_ERROR_MESSAGE_fieldRequired,
                messages.get(String.format(MessagesNamingConvention.LABEL_KEY_TEMPLATE, fieldDescriptor.name())));
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
                        if (date.compareTo(new Date()) > -1)
                            return messages.format(KEY_ERROR_dateOfBirth_shouldBeInPast);
						if (MIN_DATE_OF_BIRTH.compareTo(date) > 0)
							return messages.get(KEY_ERROR_MESSAGE_birthdate_old);
                    }
				}
				return error;
			case country:
				return null;
			case specialNeeds:
				return null;
			case abn:
				return null;
			default:
				throw new IllegalArgumentException(String.format("Field descriptor %s is not supported", fieldDescriptor));
		}
	}

	public void setCountryService(ICountryService countryService) {
		this.countryService = countryService;
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}
}
