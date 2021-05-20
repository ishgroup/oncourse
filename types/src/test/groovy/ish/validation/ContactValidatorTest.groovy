
/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.validation

import groovy.transform.CompileStatic
import ish.oncourse.cayenne.ContactInterface
import ish.validation.ContactValidator.Property
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import static org.mockito.Mockito.when

@CompileStatic
class ContactValidatorTest {

    /**
     * birthDate same as new Date
     *
     * @throws Exception
     */
    @Test
    void testBirthDateValidation1() throws Exception {

        ContactInterface contact = Mockito.mock(ContactInterface.class)
        when(contact.getDateOfBirth()).thenReturn(new Date())

        String lastName = "test"
        String firstName = "test"
        when(contact.getLastName()).thenReturn(lastName)
        when(contact.getFirstName()).thenReturn(firstName)

        ContactValidator contactValidator = ContactValidator.valueOf(contact)

        Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate()

        Assertions.assertEquals(1, errorCodeMap.size())
    }


    /**
     * birthDate is (new Date + 1day)
     *
     * @throws Exception
     */
    @Test
    void testBirthDateValidation2() throws Exception {

        ContactInterface contact = Mockito.mock(ContactInterface.class)
        Date birthDate = DateUtils.addDays(new Date(), 1)
        when(contact.getDateOfBirth()).thenReturn(birthDate)

        String lastName = "test"
        String firstName = "test"
        when(contact.getLastName()).thenReturn(lastName)
        when(contact.getFirstName()).thenReturn(firstName)

        ContactValidator contactValidator = ContactValidator.valueOf(contact)

        Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate()

        Assertions.assertEquals(1, errorCodeMap.size())
        Assertions.assertNotNull(errorCodeMap.get(ContactInterface.BIRTH_DATE_KEY))
    }


    /**
     * birthDate is (new Date - 1day)
     *
     * @throws Exception
     */
    @Test
    void testBirthDateValidation3() throws Exception {

        ContactInterface contact = Mockito.mock(ContactInterface.class)
        Date birthDate = DateUtils.addDays(new Date(), -1)
        when(contact.getDateOfBirth()).thenReturn(birthDate)

        String lastName = "test"
        String firstName = "test"
        when(contact.getLastName()).thenReturn(lastName)
        when(contact.getFirstName()).thenReturn(firstName)

        ContactValidator contactValidator = ContactValidator.valueOf(contact)

        Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate()

        Assertions.assertEquals(0, errorCodeMap.size())
    }

    @Test
    void testIncorrectFirstNameLastName() throws Exception {

        ContactInterface contact = Mockito.mock(ContactInterface.class)
        String lastName = StringUtils.repeat("a", 250)
        String firstName = StringUtils.repeat("a", 250)
        when(contact.getLastName()).thenReturn(lastName)
        when(contact.getFirstName()).thenReturn(firstName)


        ContactValidator contactValidator = ContactValidator.valueOf(contact)

        Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate()

        Assertions.assertEquals(2, errorCodeMap.size())
    }

    @Test
    void testCorrectFirstNameLastName() throws Exception {

        ContactInterface contact = Mockito.mock(ContactInterface.class)
        String lastName = StringUtils.repeat("a", 127)
        String firstName = StringUtils.repeat("a", 127)

        when(contact.getLastName()).thenReturn(lastName)
        when(contact.getFirstName()).thenReturn(firstName)


        ContactValidator contactValidator = ContactValidator.valueOf(contact)

        Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate()

        Assertions.assertEquals(0, errorCodeMap.size())
    }

    @Test
    void testCorrectCompanyName() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class)
        String lastName = "CompanyName"
        when(contact.getLastName()).thenReturn(lastName)
        when(contact.getIsCompany()).thenReturn(true)


        ContactValidator contactValidator = ContactValidator.valueOf(contact)

        Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate()

        Assertions.assertEquals(0, errorCodeMap.size())
    }

    @Test
    void testIncorrectCompanyName() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class)
        String lastName = ""
        when(contact.getLastName()).thenReturn(lastName)
        when(contact.getIsCompany()).thenReturn(true)


        ContactValidator contactValidator = ContactValidator.valueOf(contact)

        Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate()

        Assertions.assertEquals(1, errorCodeMap.size())
    }

    @Test
    void testEmptyFirstNameLastName() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class)
        String firstName = ""
        String lastName = ""
        when(contact.getFirstName()).thenReturn(firstName)
        when(contact.getLastName()).thenReturn(lastName)


        ContactValidator contactValidator = ContactValidator.valueOf(contact)

        Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate()

        Assertions.assertEquals(2, errorCodeMap.size())
    }

    @Test
    void testStreet() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class)
        String firstName = "first"
        String lastName = "last"
        String street = "street"
        when(contact.getFirstName()).thenReturn(firstName)
        when(contact.getLastName()).thenReturn(lastName)
        when(contact.getStreet()).thenReturn(street)


        ContactValidator contactValidator = ContactValidator.valueOf(contact)

        Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate()

        Assertions.assertEquals(0, errorCodeMap.size())
    }

    @Test
    void testIncorrectStreet() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class)
        String firstName = "first"
        String lastName = "last"
        String street = StringUtils.repeat("street", 34)
        when(contact.getFirstName()).thenReturn(firstName)
        when(contact.getLastName()).thenReturn(lastName)
        when(contact.getStreet()).thenReturn(street)


        ContactValidator contactValidator = ContactValidator.valueOf(contact)

        Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate()

        Assertions.assertEquals(1, errorCodeMap.size())
        Assertions.assertNotNull(errorCodeMap.get(ContactInterface.STREET_KEY))
    }

    @Test
    void testCorrectEmail() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class)
        String firstName = "first"
        String lastName = "last"
        String email = "test@com.au"
        when(contact.getFirstName()).thenReturn(firstName)
        when(contact.getLastName()).thenReturn(lastName)
        when(contact.getEmail()).thenReturn(email)


        ContactValidator contactValidator = ContactValidator.valueOf(contact)

        Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate()

        Assertions.assertEquals(0, errorCodeMap.size())
    }

    /**
     * test incorrect email
     *
     * @throws Exception
     */
    @Test
    void testIncorrectEmail1() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class)
        String firstName = "first"
        String lastName = "last"
        String email = "test@com.au@au"
        when(contact.getFirstName()).thenReturn(firstName)
        when(contact.getLastName()).thenReturn(lastName)
        when(contact.getEmail()).thenReturn(email)


        ContactValidator contactValidator = ContactValidator.valueOf(contact)

        Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate()

        Assertions.assertEquals(1, errorCodeMap.size())
        Assertions.assertNotNull(errorCodeMap.get(ContactInterface.EMAIL_KEY))
    }

    /**
     * test too long email
     *
     * @throws Exception
     */
    @Test
    void testIncorrectEmail2() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class)
        String firstName = "first"
        String lastName = "last"
        String email = StringUtils.repeat("test", 30).concat("@com.au")
        when(contact.getFirstName()).thenReturn(firstName)
        when(contact.getLastName()).thenReturn(lastName)
        when(contact.getEmail()).thenReturn(email)


        ContactValidator contactValidator = ContactValidator.valueOf(contact)

        Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate()

        Assertions.assertEquals(1, errorCodeMap.size())
        Assertions.assertNotNull(errorCodeMap.get(ContactInterface.EMAIL_KEY))
    }

    @Test
    void validateWillowAngelPropertyLength() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class)
        String firstName = "first"
        String lastName = "last"
        String postCode = StringUtils.repeat("a", Property.postcode.getLength() + 1)
        String state = StringUtils.repeat("a", Property.state.getLength() + 1)
        String mobilePhone = StringUtils.repeat("a", Property.mobilePhone.getLength() + 1)
        String homePhone = StringUtils.repeat("a", Property.homePhone.getLength() + 1)
        String fax = StringUtils.repeat("a", Property.fax.getLength() + 1)


        when(contact.getFirstName()).thenReturn(firstName)
        when(contact.getLastName()).thenReturn(lastName)
        when(contact.getPostcode()).thenReturn(postCode)
        when(contact.getState()).thenReturn(state)
        when(contact.getMobilePhone()).thenReturn(mobilePhone)
        when(contact.getHomePhone()).thenReturn(homePhone)
        when(contact.getFax()).thenReturn(fax)


        ContactValidator contactValidator = ContactValidator.valueOf(contact)

        Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate()

        Assertions.assertEquals(5, errorCodeMap.size())
    }

    @Test
    void testNullProperties() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class)
        when(contact.getFirstName()).thenReturn(null)
        when(contact.getLastName()).thenReturn(null)
        when(contact.getPostcode()).thenReturn(null)
        when(contact.getState()).thenReturn(null)
        when(contact.getMobilePhone()).thenReturn(null)
        when(contact.getHomePhone()).thenReturn(null)
        when(contact.getFax()).thenReturn(null)
        when(contact.getEmail()).thenReturn(null)


        ContactValidator contactValidator = ContactValidator.valueOf(contact)

        Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate()

        Assertions.assertEquals(2, errorCodeMap.size())
    }
}