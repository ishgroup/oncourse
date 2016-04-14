package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.cayenne.ContactInterface;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.util.HTMLUtils;
import ish.oncourse.util.MessagesNamingConvention;
import ish.oncourse.utils.ContactDelegator;
import ish.oncourse.utils.PhoneValidator;
import ish.oncourse.utils.StringUtilities;
import ish.validation.ContactErrorCode;
import ish.validation.ContactValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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

    private CustomFieldHolder customFieldHolder;

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

            if (fieldDescriptor.propertyClass == Country.class) {
                if (stringValue == null)
                    value = getCountryBy(ICountryService.DEFAULT_COUNTRY_NAME);
                else {
                    value = getCountryBy(stringValue);
                    if (value == null) {
                        errors.put(fieldDescriptor.name(),
                                messages.format(KEY_ERROR_error_countryOfBirth, stringValue));
                        value = getCountryBy(ICountryService.DEFAULT_COUNTRY_NAME);
                    }
                }
            }

            contact.writeProperty(fieldDescriptor.propertyName, value);
        }

        // validate custom fields
        for (String name : customFieldHolder.getCustomFieldNames()) {
            if (customFieldHolder.isCustomFieldRequared(name)
                    && StringUtils.trimToNull(customFieldHolder.getCustomFieldValue(name)) == null) {
                errors.put(name,
                        messages.format(KEY_ERROR_MESSAGE_fieldRequired, name));
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
        return messages.format(KEY_ERROR_MESSAGE_fieldRequired,
                messages.get(String.format(MessagesNamingConvention.LABEL_KEY_TEMPLATE, fieldDescriptor.name())));
    }


    public void setRequest(Request request) {
        this.request = request;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    Contact getContact() {
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

    public void setCustomFieldHolder(CustomFieldHolder customFieldHolder) {
        this.customFieldHolder = customFieldHolder;
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
        ContactValidator contactValidator = ContactValidator.valueOf(ContactDelegator.valueOf(contact));
        Map<String, ContactErrorCode> errorMap = contactValidator.validate();

        switch (fieldDescriptor) {
            case street:
                return validateStreet(errorMap);
            case suburb:
                return contact.validateSuburb();
            case state:
                return validateState(errorMap);
            case postcode:
                return validatePostCode(defaultCountry, errorMap);
            case homePhoneNumber:
                return validateHomePhone(defaultCountry, errorMap);
            case businessPhoneNumber:
                return defaultCountry ? contact.validateBusinessPhone() : validateLength(contact.getBusinessPhoneNumber(), 20);
            case faxNumber:
                return validateFaxNumber(defaultCountry, errorMap);
            case mobilePhoneNumber:
                return validateMobilePhoneNumber(defaultCountry, errorMap);
            case dateOfBirth:
                return validateDateOfBirth(errorMap);
            case country:
                return null;
            case specialNeeds:
                return null;
            case abn:
                return null;
            case isMale:
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

    private String validateLength(String value, int length) {
        value = StringUtils.trimToEmpty(value);
        return value.length() > length ? String.format("Max length of the field is %d chars.", length) : null;
    }

    private String validateLength(String propertyKey, Map<String, ContactErrorCode> errorMap, int maxLength){
        ContactErrorCode contactErrorCode = errorMap.get(propertyKey);
        if (contactErrorCode != null) {
            if (contactErrorCode.equals(ContactErrorCode.incorrectPropertyLength)) {
                return String.format("Max length of the %s field is %d characters", propertyKey, maxLength);
            }
        }
        return null;
    }


   private String validateStreet(Map<String, ContactErrorCode> errorMap) {
       return validateLength(ContactInterface.STREET_KEY, errorMap, ContactValidator.Property.street.getLength());
   }

    private String validateState(Map<String, ContactErrorCode> errorMap) {
        return validateLength(ContactInterface.STATE_KEY, errorMap, ContactValidator.Property.state.getLength());
    }

    private String validatePostCode(boolean defaultCountry, Map<String, ContactErrorCode> errorMap) {
        String lengthFailure = validateLength(ContactInterface.POSTCODE_KEY, errorMap, ContactValidator.Property.postcode.getLength());
        if (lengthFailure != null) {
            return lengthFailure;
        }
        if (defaultCountry) {
            if (StringUtils.isBlank(contact.getPostcode())) {
                return null;
            }
            if (!contact.getPostcode().matches("(\\d){4}")) {
                return "Enter 4 digit postcode for Australian postcodes.";
            }
        }
        return null;
    }

    private String validateHomePhone(boolean defaultCountry, Map<String, ContactErrorCode> errorMap) {
        String lengthFailure = validateLength(ContactInterface.PHONE_HOME_KEY, errorMap, ContactValidator.Property.homePhone.getLength());
        if (lengthFailure != null) {
            return lengthFailure;
        }
        if (defaultCountry) {
            if (StringUtils.isBlank(contact.getHomePhoneNumber())) {
                return null;
            }
            try {
                contact.setHomePhoneNumber(PhoneValidator.validatePhoneNumber("home", contact.getHomePhoneNumber()));
            } catch (Exception ex) {
                return ex.getMessage();
            }
        }
        return null;
    }

    private String validateFaxNumber(boolean defaultCountry, Map<String, ContactErrorCode> errorMap) {
        String lengthFailure = validateLength(ContactInterface.FAX_KEY, errorMap, ContactValidator.Property.fax.getLength());
        if (lengthFailure != null) {
            return lengthFailure;
        }
        if (defaultCountry) {
            if (StringUtils.isBlank(contact.getFaxNumber())) {
                return null;
            }
            try {
                contact.setFaxNumber(PhoneValidator.validatePhoneNumber("fax", contact.getFaxNumber()));
            } catch (Exception ex) {
                return ex.getMessage();
            }
        }
        return null;
    }

    private String validateDateOfBirth(Map<String, ContactErrorCode> errorMap) {
        ContactErrorCode contactErrorCode = errorMap.get(ContactInterface.BIRTH_DATE_KEY);
        if (contactErrorCode != null) {
            if (contactErrorCode.equals(ContactErrorCode.birthDateCanNotBeInFuture)) {
                return String.format("The birth date cannot be in the future.");
            }
        }
        Date date = contact.getDateOfBirth();
        if (date != null) {
            if (MIN_DATE_OF_BIRTH.compareTo(date) > 0)
                return messages.get(KEY_ERROR_MESSAGE_birthdate_old);
        }
        return null;
    }

    private String validateMobilePhoneNumber(boolean defaultCountry, Map<String, ContactErrorCode> errorMap) {
        String lengthFailure = validateLength(ContactInterface.MOBILE_PHONE_KEY, errorMap, ContactValidator.Property.mobilePhone.getLength());
        if (lengthFailure != null) {
            return lengthFailure;
        }
        if (defaultCountry) {
            if (StringUtils.isBlank(contact.getMobilePhoneNumber())) {
                return null;
            }
            try {
                contact.setMobilePhoneNumber(PhoneValidator.validateMobileNumber(contact.getMobilePhoneNumber()));
            } catch (Exception ex) {
                return ex.getMessage();
            }
        }
        return null;
    }
}
