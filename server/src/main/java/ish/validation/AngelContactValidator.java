/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.validation;

import ish.oncourse.server.cayenne.Contact;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static java.lang.Boolean.TRUE;

/**
 * Created by anarut on 10/25/16.
 */
public class AngelContactValidator {

    private static final Logger logger = LogManager.getLogger();

    private org.apache.cayenne.validation.ValidationResult validationResult;
    private Contact contact;

    private AngelContactValidator() {

    }

    public static AngelContactValidator valueOf(org.apache.cayenne.validation.ValidationResult validationResult, Contact contact) {
        AngelContactValidator angelContactValidator = new AngelContactValidator();
        angelContactValidator.validationResult = validationResult;
        angelContactValidator.contact = contact;
        return angelContactValidator;
    }

    public void validate() {
        ContactValidator contactValidator = ContactValidator.valueOf(contact);
        Map<String, ContactErrorCode> errors =  contactValidator.validate();

        errors.entrySet().stream().map(this::createFailureBy).forEach(validationResult::addFailure);

        if (StringUtils.isNotBlank(contact.getMessage()) && contact.getMessage().length() > 500) {
            validationResult.addFailure(new ValidationFailure(contact, Contact.MESSAGE_KEY, "The message cannot be longer than 500 characters."));
        }
    }

    private ValidationFailure createFailureBy(Map.Entry<String, ContactErrorCode> error) {
        String message;
        switch (error.getValue()) {
            case birthDateCanNotBeInFuture:
                message = "The birth date cannot be in future.";
                break;
            case firstNameNeedToBeProvided:
                message = "You need to enter a contact first name.";
                break;
            case lastNameNeedToBeProvided:
                if (TRUE.equals(contact.getIsCompany())) {
                    message = "You need to enter a company name.";
                } else {
                    message = "You need to enter a contact last name.";
                }
                break;
            case incorrectEmailFormat:
                message = "Please enter an email address in the correct format.";
                break;
            case incorrectPropertyLength:
                ContactValidator.Property property = ContactValidator.Property.valueOf(error.getKey());
                message = String.format("%s is restricted to %d characters.", error.getKey(), property.getLength());
                break;
            default:
                throw new IllegalArgumentException();

        }
        logger.info("Contact with objectId={} : {}", contact.getObjectId(), message);
        return new ValidationFailure(contact, error.getKey(), message);
    }
}
