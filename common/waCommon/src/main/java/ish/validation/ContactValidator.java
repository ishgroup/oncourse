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

    public static final int NAME_MAX_LENGTH = 128;
    public static final int POST_CODE_MAX_LENGTH = 20;
    public static final int STATE_MAX_LENGTH = 20;
    public static final int MOBILE_PHONE_NUMBER_MAX_LENGTH = 20;
    public static final int HOME_PHONE_NUMBER_MAX_LENGTH = 20;
    public static final int FAX_MAX_LENGTH = 20;

    private ContactValidator() {
    }

    public static ContactValidator valueOf(ContactInterface contact, ValidationResult validationResult) {
        ContactValidator contactValidator = new ContactValidator();
        contactValidator.contact = contact;
        contactValidator.result = validationResult;

        return contactValidator;
    }

    @Override
    public void validate() {
        validateBirthDate();
        if (Boolean.TRUE.equals(contact.getIsCompany())) {
            validateLastName();
        } else {
            validateFirstName();
            validateLastName();
        }
        validateStreet();
        validateEmail();
        validateWillowAngelPropertyLength();
    }

    /**
     * On willow and angel sides database columns have different length.
     */
    private void validateWillowAngelPropertyLength() {
        validatePropertyLength(contact.getPostcode(), ContactInterface.POSTCODE_KEY, "Post code too long", POST_CODE_MAX_LENGTH);
        validatePropertyLength(contact.getState(), ContactInterface.STATE_KEY, "State too long", STATE_MAX_LENGTH);
        validatePropertyLength(contact.getMobilePhone(), ContactInterface.MOBILE_PHONE_KEY, "Mobile phone number too long", MOBILE_PHONE_NUMBER_MAX_LENGTH);
        validatePropertyLength(contact.getHomePhone(), ContactInterface.PHONE_HOME_KEY,  "Home phone number too long", HOME_PHONE_NUMBER_MAX_LENGTH);
        validatePropertyLength(contact.getFax(), ContactInterface.FAX_KEY,  "Fax number too long", FAX_MAX_LENGTH);

    }

    private void validatePropertyLength(String property, String propertyKey, String message, int length) {
        if (property != null && property.length() > length) {
            addFailure(propertyKey, message);
        }
    }

    private void validateEmail() {
        if (!StringUtils.isBlank(contact.getEmail())) {
            if (!ValidationUtil.isValidEmailAddress(contact.getEmail())) {
                addFailure(ContactInterface.EMAIL_KEY, "Please enter an email address in the correct format.");
            } else if (contact.getEmail().length() > 100) {
                addFailure(ContactInterface.EMAIL_KEY, "Email addresses are restricted to 100 characters.");
            }
        }
    }

    private void validateLastName() {
        if (StringUtils.isBlank(contact.getLastName())) {
            addFailure(ContactInterface.LAST_NAME_KEY, "You need to enter a last name.");
        } else if (contact.getLastName().length() > NAME_MAX_LENGTH) {
            addFailure(ContactInterface.LAST_NAME_KEY,
                    String.format("LastName exceeds maximum allowed length (%d chars): %d))", NAME_MAX_LENGTH, contact.getLastName().length()));
        }
    }

    private void validateFirstName() {
        if (StringUtils.isBlank(contact.getFirstName())) {
            addFailure(ContactInterface.FIRST_NAME_KEY, "You need to enter a first name.");
        } else if (contact.getFirstName().length() > NAME_MAX_LENGTH) {
            addFailure(contact.FIRST_NAME_KEY,
                    String.format("FirstName exceeds maximum allowed length (%d chars): %d))", NAME_MAX_LENGTH, contact.getFirstName().length()));
        }
    }

    private void validateStreet() {
        if (contact.getStreet() != null && contact.getStreet().length() > 200) {
            addFailure(ContactInterface.STREET_KEY, "Street addresses are restricted to 200 characters.");
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