package ish.validation;

import ish.oncourse.cayenne.ContactInterface;
import org.apache.cayenne.reflect.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static ish.validation.ContactValidationErrorCode.BIRTH_DATE_CAN_NOT_BE_IN_FUTURE;
import static ish.validation.ContactValidationErrorCode.FIRST_NAME_NEED_TO_BE_PROVIDED;
import static ish.validation.ContactValidationErrorCode.INCORRECT_EMAIL_FORMAT;
import static ish.validation.ContactValidationErrorCode.INCORRECT_PROPERTY_LENGTH;
import static ish.validation.ContactValidationErrorCode.LAST_NAME_NEED_TO_BE_PROVIDED;


public class ContactValidator implements Validator<ContactValidationErrorCode> {

    private ContactInterface contact;
    private Map<String, ContactValidationErrorCode> result;

    private ContactValidator() {
    }

    public static ContactValidator valueOf(ContactInterface contact) {
        ContactValidator contactValidator = new ContactValidator();
        contactValidator.contact = contact;
        contactValidator.result = new HashMap<>();

        return contactValidator;
    }

    @Override
    public Map<String, ContactValidationErrorCode> validate() {
        validateBirthDate();

        if (Boolean.TRUE.equals(contact.getIsCompany())) {
            validateLastName();
        } else {
            validateFirstName();
            validateLastName();
        }

        validateEmail();

        validatePropertyLength();
        return result;
    }

    /**
     * On willow and angel sides database columns have different length.
     */
    private void validatePropertyLength() {
        for (Property property : Property.values()) {
            validateLength(property);
        }
    }

    private void validateLength(Property property) {
        String value = (String) PropertyUtils.getProperty(contact, property.name());
        if (value != null && value.length() > property.getLength()) {
            result.put(property.name(), INCORRECT_PROPERTY_LENGTH);
        }
    }


    private void validateEmail() {
        if (!StringUtils.isBlank(contact.getEmail()) && !ValidationUtil.isValidEmailAddress(contact.getEmail())) {
            result.put(ContactInterface.EMAIL_KEY, INCORRECT_EMAIL_FORMAT);
        }
    }

    private void validateLastName() {
        if (StringUtils.isBlank(contact.getLastName())) {
            result.put(ContactInterface.LAST_NAME_KEY, LAST_NAME_NEED_TO_BE_PROVIDED);
        }
    }

    private void validateFirstName() {
        if (StringUtils.isBlank(contact.getFirstName())) {
            result.put(ContactInterface.FIRST_NAME_KEY, FIRST_NAME_NEED_TO_BE_PROVIDED);
        }
    }

    private void validateBirthDate() {
        if (contact.getBirthDate() != null) {
            Date birthDateTruncated = DateUtils.truncate(contact.getBirthDate(), Calendar.DAY_OF_MONTH);
            Date currentDateTruncated = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
            if (birthDateTruncated.after(DateUtils.addDays(currentDateTruncated, -1))) {
                result.put(ContactInterface.BIRTH_DATE_KEY, BIRTH_DATE_CAN_NOT_BE_IN_FUTURE);

            }
        }
    }

    enum Property {
        firstName(128),
        lastName(128),
        postcode(20),
        state(20),
        mobilePhone(20),
        homePhone(20),
        fax(20),
        email(100),
        street(200);

        private int length;

        Property(int length) {
            this.length = length;
        }

        int getLength() {
            return length;
        }
    }
}