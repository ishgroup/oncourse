package ish.validation;

import ish.oncourse.cayenne.ContactInterface;
import org.apache.cayenne.validation.BeanValidationFailure;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;


public class ContactValidator implements Validator {

    private static final Logger logger = LogManager.getLogger();
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

    public static ContactValidator valueOf(ContactInterface contact) {
        ContactValidator contactValidator = new ContactValidator();
        contactValidator.contact = contact;
        contactValidator.result = new ValidationResult();

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
        validateStreet();
        validateEmail();
        validateWillowAngelPropertyLength();

        return result;
    }

    /**
     * On willow and angel sides database columns have different length.
     */
    private void validateWillowAngelPropertyLength() {
        if (contact.getPostcode() != null && contact.getPostcode().length() > POST_CODE_MAX_LENGTH) {
            result.addFailure(new BeanValidationFailure(this, ContactInterface.POSTCODE_KEY, "Post code too long"));
        }
        if (contact.getState() != null && contact.getState().length() > STATE_MAX_LENGTH) {
            result.addFailure(new BeanValidationFailure(this, ContactInterface.STATE_KEY, "State too long"));
        }
        if (contact.getMobilePhone() != null && contact.getMobilePhone().length() > MOBILE_PHONE_NUMBER_MAX_LENGTH) {
            result.addFailure(new BeanValidationFailure(this, ContactInterface.MOBILE_PHONE_KEY, "Mobile phone number too long"));
        }
        if (contact.getHomePhone() != null && contact.getHomePhone().length() > HOME_PHONE_NUMBER_MAX_LENGTH) {
            result.addFailure(new BeanValidationFailure(this, ContactInterface.PHONE_HOME_KEY, "Home phone number too long"));
        }
        if (contact.getFax() != null && contact.getFax().length() > FAX_MAX_LENGTH) {
            result.addFailure(new BeanValidationFailure(this, ContactInterface.FAX_KEY, "Fax number too long"));
        }
    }

    private void validateEmail() {
        if (contact.getEmail() != null && contact.getEmail().length() > 0 && !ValidationUtil.isValidEmailAddress(contact.getEmail())) {
            result.addFailure(new BeanValidationFailure(this, ContactInterface.EMAIL_KEY, "Please enter an email address in the correct format."));
        } else if (contact.getEmail() != null && contact.getEmail().length() > 100) {
            result.addFailure(new BeanValidationFailure(this, ContactInterface.EMAIL_KEY, "Email addresses are restricted to 100 characters."));
        }
    }

    private void validateLastName() {
        if (contact.getLastName().length() > NAME_MAX_LENGTH) {
            result.addFailure(new BeanValidationFailure(this, ContactInterface.LAST_NAME_KEY,
                    String.format("LastName exceeds maximum allowed length (%d chars): %d))", NAME_MAX_LENGTH, contact.getLastName().length())));
            logger.warn(String.format("LastName exceeds maximum allowed length (%d chars): %d))", NAME_MAX_LENGTH, contact.getLastName().length()));
        }

        if (contact.getLastName() == null || contact.getLastName().trim().length() == 0) {
            result.addFailure(new BeanValidationFailure(this, ContactInterface.LAST_NAME_KEY, "You need to enter a company name."));
        }
    }

    private void validateFirstName() {
        if (contact.getFirstName() == null || contact.getFirstName().trim().length() == 0) {
            result.addFailure(new BeanValidationFailure(this, ContactInterface.FIRST_NAME_KEY, "You need to enter a contact first name."));
        }

        if (contact.getFirstName().length() > NAME_MAX_LENGTH) {
            result.addFailure(new BeanValidationFailure(this, contact.FIRST_NAME_KEY,
                    String.format("FirstName exceeds maximum allowed length (%d chars): %d))", NAME_MAX_LENGTH, contact.getFirstName().length())));
            logger.warn(String.format("FirstName exceeds maximum allowed length (%d chars): %d))", NAME_MAX_LENGTH, contact.getFirstName().length()));
        }
    }

    private void validateStreet() {
        if (contact.getStreet() != null && contact.getStreet().length() > 200) {
            result.addFailure(new BeanValidationFailure(this, ContactInterface.STREET_KEY, "Street addresses are restricted to 200 characters."));
        }
    }

    private void validateBirthDate() {
        if (contact.getBirthDate() != null) {
            if (contact.getBirthDate().after(DateUtils.addDays(new Date(), -1))) {
                result.addFailure(new BeanValidationFailure(this, ContactInterface.BIRTH_DATE_KEY, "The birth date cannot be in future."));
                logger.warn(String.format("Incorrect birth date. Contact birth date : %s. Current date : %s", contact.getBirthDate(), new Date()));
            }
        }
    }
}