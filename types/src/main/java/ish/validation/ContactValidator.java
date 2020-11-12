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

import ish.oncourse.cayenne.ContactInterface;
import org.apache.cayenne.reflect.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static ish.validation.ContactErrorCode.*;


public class ContactValidator implements Validator<ContactErrorCode> {

    private ContactInterface contact;
    private Map<String, ContactErrorCode> result;

    private ContactValidator() {
    }

    public static ContactValidator valueOf(ContactInterface contact) {
        ContactValidator contactValidator = new ContactValidator();
        contactValidator.contact = contact;
        contactValidator.result = new HashMap<>();

        return contactValidator;
    }

    @Override
    public Map<String, ContactErrorCode> validate() {
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
            result.put(property.name(), incorrectPropertyLength);
        }
    }


    private void validateEmail() {
        if (!StringUtils.isBlank(contact.getEmail()) && !ValidationUtil.isValidEmailAddress(contact.getEmail())) {
            result.put(ContactInterface.EMAIL_KEY, incorrectEmailFormat);
        }
    }

    private void validateLastName() {
        if (StringUtils.isBlank(contact.getLastName())) {
            result.put(ContactInterface.LAST_NAME_KEY, lastNameNeedToBeProvided);
        }
    }

    private void validateFirstName() {
        if (StringUtils.isBlank(contact.getFirstName())) {
            result.put(ContactInterface.FIRST_NAME_KEY, firstNameNeedToBeProvided);
        }
    }

    private void validateBirthDate() {
        if (contact.getDateOfBirth() != null) {
            Date birthDateTruncated = DateUtils.truncate(contact.getDateOfBirth(), Calendar.DAY_OF_MONTH);
            Date currentDateTruncated = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
            if (birthDateTruncated.after(DateUtils.addDays(currentDateTruncated, -1))) {
                result.put(ContactInterface.BIRTH_DATE_KEY, birthDateCanNotBeInFuture);

            }
        }
    }

    public enum Property {
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

        public int getLength() {
            return length;
        }
    }
}
