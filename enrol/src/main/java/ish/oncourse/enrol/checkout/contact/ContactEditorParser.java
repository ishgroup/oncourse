package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.components.ContactDetailStrings;
import ish.oncourse.enrol.utils.EnrolContactValidator;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.util.HTMLUtils;
import ish.oncourse.util.MessagesNamingConvention;
import ish.oncourse.utils.StringUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ish.oncourse.enrol.services.Constants.DATE_FIELD_PARSE_FORMAT;
import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor;

public class ContactEditorParser {

    private Request request;
    private Contact contact;
    private List<String> visibleFields;

    private Messages messages;
    private ICountryService countryService;
    private ContactFieldHelper contactFieldHelper;
    private ContactCustomFieldHolder contactCustomFieldHolder;

    private DateFormat dateFormat = new SimpleDateFormat(DATE_FIELD_PARSE_FORMAT);

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
		EnrolContactValidator validator = EnrolContactValidator.valueOf(contact, visibleFields, messages);
		validator.validate();
		errors.putAll(validator.getErrors());
	}


    void parseMarketingFields() {
        Boolean value = parseBooleanParameter(Contact.IS_MARKETING_VIA_EMAIL_ALLOWED.getName());
        contact.setIsMarketingViaEmailAllowed(value == null ? Boolean.FALSE : value);
        value = parseBooleanParameter(Contact.IS_MARKETING_VIA_POST_ALLOWED.getName());
        contact.setIsMarketingViaPostAllowed(value == null ? Boolean.FALSE : value);
        value = parseBooleanParameter(Contact.IS_MARKETING_VIA_SMSALLOWED.getName());
        contact.setIsMarketingViaSMSAllowed(value == null ? Boolean.FALSE : value);
    }

    private Boolean parseBooleanParameter(String parameterName) {
        String value = StringUtilities.cutToNull(request.getParameter(parameterName));
        if (value == null)
            return null;
        else
            return HTMLUtils.parserBooleanValue(value);
    }


    private void parseContactFields() {
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
                    errors.put(fieldDescriptor.name(), messages.get(ContactDetailStrings.KEY_ERROR_MESSAGE_birthdate_hint));
                }
            }

            if (fieldDescriptor.propertyClass == Country.class) {
                if (stringValue == null)
                    value = getCountryBy(ICountryService.DEFAULT_COUNTRY_NAME);
                else {
                    value = getCountryBy(stringValue);
                    if (value == null) {
                        errors.put(fieldDescriptor.name(),
                                messages.format(ContactDetailStrings.KEY_ERROR_error_countryOfBirth, stringValue));
                        value = getCountryBy(ICountryService.DEFAULT_COUNTRY_NAME);
                    }
                }
            }

            contact.writeProperty(fieldDescriptor.propertyName, value);
        }

        // validate custom fields
        for (String name : contactCustomFieldHolder.getCustomFieldNames()) {
            if (contactCustomFieldHolder.isCustomFieldRequared(name)
                    && StringUtils.trimToNull(contactCustomFieldHolder.getCustomFieldValue(name)) == null) {
                errors.put(name,
                        messages.format(ContactDetailStrings.KEY_ERROR_MESSAGE_fieldRequired, name));
            }
        }

        /**
         * I introduced this code to be sure that contact.country field is not empty for any cases.
         * There are a lot of old contacts which have null value in this field.
         * When our contact editor requires to fill required fields we can get NullPointerException,
         * if country field is null.
         */
        if (contact.getCountry() == null) {
            contact.setCountry(getCountryBy(ICountryService.DEFAULT_COUNTRY_NAME));
        }
    }


    private Country getCountryBy(String name) {
        Country country = countryService.getCountryByName(name);
        if (country != null) {
            country = contact.getObjectContext().localObject(country);
        }
        return country;
    }

    private String getRequiredMessage(FieldDescriptor fieldDescriptor) {
        return messages.format(ContactDetailStrings.KEY_ERROR_MESSAGE_fieldRequired,
                messages.get(String.format(MessagesNamingConvention.LABEL_KEY_TEMPLATE, fieldDescriptor.name())));
    }


    Contact getContact() {
        return contact;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public Messages getMessages() {
        return messages;
    }

    public static ContactEditorParser valueOf(Request request, Contact contact , List<String> visibleFields,
                                              Messages messages, ICountryService countryService, ContactFieldHelper contactFieldHelper,
                                              ContactCustomFieldHolder contactCustomFieldHolder) {
        ContactEditorParser result = new ContactEditorParser();
        result.request = request;
        result.contact = contact;
        result.visibleFields = visibleFields;

        result.messages = messages;
        result.countryService = countryService;
        result.contactFieldHelper = contactFieldHelper;
        result.contactCustomFieldHolder = contactCustomFieldHolder;
        return result;
    }

}
