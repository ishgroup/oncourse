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

package ish.oncourse.server.imports.avetmiss

import groovy.transform.CompileDynamic
import static ish.common.types.AvetmissStudentSchoolLevel.COMPLETED_YEAR_10
import static ish.common.types.AvetmissStudentSchoolLevel.COMPLETED_YEAR_11
import static ish.common.types.AvetmissStudentSchoolLevel.COMPLETED_YEAR_12
import static ish.common.types.AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW
import static ish.common.types.AvetmissStudentSchoolLevel.COMPLETED_YEAR_9
import static ish.common.types.AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION
import static ish.common.types.AvetmissStudentSchoolLevel.DID_NOT_GO_TO_SCHOOL
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactCustomField
import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.cayenne.CustomField
import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.server.cayenne.Language
import ish.oncourse.server.cayenne.Student
import ish.util.DateFormatter
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.LocalDate
import java.time.format.DateTimeFormatter

@CompileDynamic
class AvetmissImportService {
    String fileType
    ObjectContext context
    List<String> errors

    def getLanguageBy(Integer absCode) {
        return ObjectSelect.query(Language).where(Language.ABS_CODE.eq(String.valueOf(absCode)))
                .selectFirst(context)
    }

    Country getCountryBy(Integer countryCode) {
        return ObjectSelect.query(Country).where(Country.SACC_CODE.eq(countryCode)).selectFirst(context)
    }

    private void initStudent(Contact contact) {
        if (contact && !contact.isStudent) {
            contact.isStudent = true
            contact.student = context.newObject(Student)
        }
    }

    Contact getContactBy(String firstName, String lastName, LocalDate birthDate) {
        Contact contact = ObjectSelect.query(Contact).where(Contact.FIRST_NAME.eq(firstName)
                .andExp(Contact.LAST_NAME.eq(lastName))
                .andExp(birthDate == null ? Contact.BIRTH_DATE.isNull() : Contact.BIRTH_DATE.eq(birthDate))).selectFirst(context)
        initStudent(contact)
        return contact
    }

    Contact getContactBy(String firstName, String lastName, String email) {
        Contact contact = ObjectSelect.query(Contact).where(Contact.FIRST_NAME.eq(firstName)
                .andExp(Contact.LAST_NAME.eq(lastName))
                .andExp(Contact.EMAIL.eq(email))).selectFirst(context)
        initStudent(contact)
        return contact
    }

    Contact getContactBy(String studentNumber) {
        return ObjectSelect.query(Contact).where(Contact.STUDENT.dot(Student.STUDENT_NUMBER).eq(Long.valueOf(studentNumber))).selectOne(context)
    }

    Contact getContactBy(String customeFieldName, String customeFieldValue) {
        def contact = ObjectSelect.query(Contact)
                .where(Contact.CUSTOM_FIELDS.dot(CustomField.VALUE).eq(customeFieldValue))
                .and(Contact.CUSTOM_FIELDS.dot(CustomField.CUSTOM_FIELD_TYPE).dot(CustomFieldType.NAME).eq(customeFieldName))
                .selectFirst(context)
        initStudent(contact)
        return contact
    }

    /**
     * parse last, first and middle name
     */
    def parseNames(String name, int lineNumber) {
        def result = [firstName: null, lastName: null, middleName: null]
        if (!StringUtils.isBlank(name)) {
            String[] names

            if (name.indexOf(',') > 0) {
                names = name.split(",", 2)
            } else {
                names = name.split(" ", 2)
            }
            if (names.length > 0) {
                result.lastName = names[0].trim()
            }
            if (names.length > 1) {
                String givenName = names[1].trim()
                if (givenName.indexOf(' ') > 0) {
                    String[] givenNames = givenName.split(" ", 2)
                    result.firstName = givenNames[0].trim()
                    result.middleName = givenNames[1].trim()
                } else {
                    result.firstName = givenName
                }
            }
        } else {
            errors.add("${fileType}: record at line ${lineNumber + 1} doesn't contain student name")
        }
        return result
    }

    def parseHighestSchoolLevel(Integer highestSchoolLevel) {
        if (highestSchoolLevel == null) {
            return DEFAULT_POPUP_OPTION
        } else {
            switch (highestSchoolLevel) {
                case 2:
                    return DID_NOT_GO_TO_SCHOOL
                case 8:
                    return COMPLETED_YEAR_8_OR_BELOW
                case 9:
                    return COMPLETED_YEAR_9
                case 10:
                    return COMPLETED_YEAR_10
                case 11:
                    return COMPLETED_YEAR_11
                case 12:
                    return COMPLETED_YEAR_12
                default:
                    return DEFAULT_POPUP_OPTION
            }
        }
    }

    Contact newContact() {
        Contact aContact = context.newObject(Contact)
        Student aStudent = context.newObject(Student)

        aStudent.contact = aContact
        aContact.isStudent = true
        return aContact
    }

    Contact newContact(String clientIdFieldName, String clientId) {
        Contact aContact = context.newObject(Contact)
        Student aStudent = context.newObject(Student)

        def type = getCustomFieldTypeBy(clientIdFieldName)
        ContactCustomField customField = context.newObject(ContactCustomField)
        customField.value = clientId
        customField.relatedObject = aContact
        customField.customFieldType = type

        aStudent.contact = aContact
        aContact.isStudent = true
        return aContact
    }


    CustomFieldType getCustomFieldTypeBy(String name) {
        CustomFieldType type = context.newObjects().find {o -> o instanceof CustomFieldType && o.key.equals(name)}
        if (type)
            return type
        type = ObjectSelect.query(CustomFieldType).and(CustomFieldType.NAME.eq(name)).selectOne(context)
        if (!type) {
            type = context.newObject(CustomFieldType)
            type.entityIdentifier = Contact.class.getName()
            type.name = name
            type.key = name
            type.isMandatory = false
        }
        return type
    }
}

class InputLine {
    private static final Logger logger = LogManager.getLogger(InputLine)

    private String text
    private int position

    /**
     * @param text
     */
    InputLine(String text) {
        this.text = text
        this.position = 0
    }

    /**
     * @param length
     * @return String
     */
    String readString(int length) {

        if (this.text.length() < this.position + length) {
            logger.debug("Tried to retrieve {} bytes, but not enough left.", length)
            return null
        }
        String value = this.text.substring(this.position, this.position + length).trim()

        this.position += length
        char[] nullString = new char[length]
        Arrays.fill(nullString, '@' as char)
        if (value.compareTo(new String(nullString)) == 0) {
            return null
        }
        return value
    }

    /**
     * @param length
     * @return Integer
     */
    Integer readInteger(int length) {
        String value = readString(length)
        if (value == null) {
            return null
        }

        try {
            return new Integer(value)
        } catch (Exception e) {
            return null
        }
    }

    /**
     * @param length
     * @return Date
     */
    Date readDate(int length) {

        String value = readString(length)
        if (value == null) {
            return null
        }
        if (value.length() != 8) {
            logger.warn("AVETMISS dates must be 8 characters. Got: '{}'", value)
            return null
        }

        return DateFormatter.formatDateToNoon(Date.parse("ddMMyyyy", value))
    }

    LocalDate readLocalDate(int length) {

        String value = readString(length)
        if (value == null) {
            return null
        }
        if (value.length() != 8) {
            logger.warn("AVETMISS dates must be 8 characters. Got: '{}'", value)
            return null
        }

        return LocalDate.parse(value, DateTimeFormatter.ofPattern("ddMMyyyy"))
    }
}


class ContactProvider {
    Map<String, Contact> imported = [:]
    AvetmissImportService service

    String clientIdFieldName
    Map<String, ?> data

    Contact provide() {
        Contact contact = imported[(String) data.clientId]
        if (contact)
            return contact

        contact = service.getContactBy(clientIdFieldName, (String) data.clientId)
        if (contact)
            return contact


        if (data.email) {
            contact = service.getContactBy(
                    (String) data.firstName,
                    (String) data.lastName,
                    (String) data.email)
            if (contact)
                return contact
        }

        if (data.birthDate) {
            contact = service.getContactBy(
                    (String) data.firstName,
                    (String) data.lastName,
                    (LocalDate) data.birthDate)
            if (contact)
                return contact
        }

        contact = service.newContact(clientIdFieldName, (String) data.clientId)
        imported.put((String) data.clientId, contact)
        contact
    }

}

