package ish.validation;

import ish.oncourse.cayenne.ContactInterface;
import org.apache.cayenne.reflect.PropertyUtils;
import org.apache.cayenne.validation.BeanValidationFailure;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;


public class ContactValidator implements Validator {

    private ContactInterface contact;
    private ValidationResult result;

    static final String LENGTH_FAILURE_MESSAGE = "Field %s exceeds maximum allowed length (%d chars): %d))";

    private ContactValidator() {
    }

    public static ContactValidator valueOf(ContactInterface contact, ValidationResult validationResult) {
        ContactValidator contactValidator = new ContactValidator();
        contactValidator.contact = contact;
        contactValidator.result = validationResult;

        return contactValidator;
    }

    @Override
    public ValidationResult validate() {
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
            addFailure(property.name(),
                    String.format(LENGTH_FAILURE_MESSAGE, property.name(), property.getLength(), value.length()));
        }
    }


    private void validateEmail() {
        if (!StringUtils.isBlank(contact.getEmail()) && !ValidationUtil.isValidEmailAddress(contact.getEmail())) {
            addFailure(ContactInterface.EMAIL_KEY, "Please enter an email address in the correct format.");
        }
    }

    private void validateLastName() {
        if (StringUtils.isBlank(contact.getLastName())) {
            addFailure(ContactInterface.LAST_NAME_KEY, "You need to enter a last name.");
        }
    }

    private void validateFirstName() {
        if (StringUtils.isBlank(contact.getFirstName())) {
            addFailure(ContactInterface.FIRST_NAME_KEY, "You need to enter a first name.");
        }
    }

    private void validateBirthDate() {
        if (contact.getBirthDate() != null) {
            Date birthDateTruncated = DateUtils.truncate(contact.getBirthDate(), Calendar.DAY_OF_MONTH);
            Date currentDateTruncated = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
            if (birthDateTruncated.after(DateUtils.addDays(currentDateTruncated, -1))) {
                addFailure(ContactInterface.BIRTH_DATE_KEY, "The birth date cannot be in future.");
            }
        }
    }

    private void addFailure(String propertyKey, String message) {
        result.addFailure(new BeanValidationFailure(this, propertyKey, message));
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