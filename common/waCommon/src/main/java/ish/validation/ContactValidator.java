package ish.validation;

import ish.oncourse.cayenne.ContactInterface;
import org.apache.cayenne.validation.BeanValidationFailure;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;


public class ContactValidator implements Validator {

    private ContactInterface contact;
    private ValidationResult result;

    public static final String LENGTH_FAILURE_FORMAT_STRING = "Field %s exceeds maximum allowed length (%d chars): %d))";

    public static final int NAME_MAX_LENGTH = 128;
    public static final int POST_CODE_MAX_LENGTH = 20;
    public static final int STATE_MAX_LENGTH = 20;
    public static final int MOBILE_PHONE_NUMBER_MAX_LENGTH = 20;
    public static final int HOME_PHONE_NUMBER_MAX_LENGTH = 20;
    public static final int FAX_MAX_LENGTH = 20;
    public static final int EMAIL_MAX_LENGTH = 100;
    public static final int STREET_MAX_LENGTH = 200;

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
        validateWillowAngelPropertyLength();
        return result;
    }

    /**
     * On willow and angel sides database columns have different length.
     */
    private void validateWillowAngelPropertyLength() {
        validatePropertyLength(contact.getPostcode(), ContactInterface.POSTCODE_KEY, POST_CODE_MAX_LENGTH);
        validatePropertyLength(contact.getState(), ContactInterface.STATE_KEY, STATE_MAX_LENGTH);
        validatePropertyLength(contact.getMobilePhone(), ContactInterface.MOBILE_PHONE_KEY, MOBILE_PHONE_NUMBER_MAX_LENGTH);
        validatePropertyLength(contact.getHomePhone(), ContactInterface.PHONE_HOME_KEY, HOME_PHONE_NUMBER_MAX_LENGTH);
        validatePropertyLength(contact.getFax(), ContactInterface.FAX_KEY, FAX_MAX_LENGTH);
        validatePropertyLength(contact.getStreet(), ContactInterface.STREET_KEY, STREET_MAX_LENGTH);
        validatePropertyLength(contact.getEmail(), ContactInterface.EMAIL_KEY, EMAIL_MAX_LENGTH);
        validatePropertyLength(contact.getLastName(), ContactInterface.LAST_NAME_KEY, NAME_MAX_LENGTH);
        validatePropertyLength(contact.getFirstName(), ContactInterface.FIRST_NAME_KEY, NAME_MAX_LENGTH);
    }

    private void validatePropertyLength(String property, String propertyKey, int maxLength) {
        if (property != null && property.length() > maxLength) {
            addFailure(propertyKey, String.format(LENGTH_FAILURE_FORMAT_STRING, propertyKey, maxLength, property.length()));
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
}