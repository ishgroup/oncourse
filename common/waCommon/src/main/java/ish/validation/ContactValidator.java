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

    private static final int NAME_LENGTH = 128;

    private ContactValidator() {
    }

    public static ContactValidator valueOf(ContactInterface contact) {
        ContactValidator contactValidator = new ContactValidator();
        contactValidator.contact = contact;

        return contactValidator;
    }

    @Override
    public void validateForSave(ValidationResult result) {
        validateBirthDate(result);
        validateFirstNameLastName(result);
        validateStreet(result);
    }

    private void validateStreet(ValidationResult result) {
        if (contact.getStreet() != null && contact.getStreet().length() > 200) {
            result.addFailure(new BeanValidationFailure(this, ContactInterface.STREET_KEY, "Street addresses are restricted to 200 characters."));
        }
    }

    private void validateBirthDate(ValidationResult result) {
        if (contact.getBirthDate() != null) {
            if (contact.getBirthDate().after(DateUtils.addDays(new Date(), -1))) {
                result.addFailure(new BeanValidationFailure(this, ContactInterface.BIRTH_DATE_PROPERTY, "The birth date cannot be in future."));
                logger.warn(String.format("Incorrect birth date. Contact birth date : %s. Current date : %s", contact.getBirthDate(), new Date()));
            }
        }
    }

    private void validateFirstNameLastName(ValidationResult result) {

        if (contact.getLastName().length() > NAME_LENGTH) {
            result.addFailure(new BeanValidationFailure(this, ContactInterface.LAST_NAME_KEY,
                    String.format("LastName exceeds maximum allowed length (%d chars): %d))", NAME_LENGTH, contact.getLastName().length())));
            logger.warn(String.format("LastName exceeds maximum allowed length (%d chars): %d))", NAME_LENGTH, contact.getLastName().length()));
        }

        if (Boolean.TRUE.equals(contact.getIsCompany())) {
            if (contact.getLastName() == null || contact.getLastName().trim().length() == 0) {
                result.addFailure(new BeanValidationFailure(this, ContactInterface.LAST_NAME_KEY, "You need to enter a company name."));
            }
        } else {
            if (contact.getLastName() == null || contact.getLastName().trim().length() == 0) {
                result.addFailure(new BeanValidationFailure(this, ContactInterface.LAST_NAME_KEY, "You need to enter a contact last name."));
            }
            if (contact.getFirstName() == null || contact.getFirstName().trim().length() == 0) {
                result.addFailure(new BeanValidationFailure(this, ContactInterface.FIRST_NAME_KEY, "You need to enter a contact first name."));
            }

            if (contact.getFirstName().length() > NAME_LENGTH) {
                result.addFailure(new BeanValidationFailure(this, contact.FIRST_NAME_KEY,
                        String.format("FirstName exceeds maximum allowed length (%d chars): %d))", NAME_LENGTH, contact.getFirstName().length())));
                logger.warn(String.format("FirstName exceeds maximum allowed length (%d chars): %d))", NAME_LENGTH, contact.getFirstName().length()));
            }
        }
    }
}