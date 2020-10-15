/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/*(
contactFile=Please select contact CSV file...
)*/


import org.apache.commons.lang3.StringUtils
import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.imports.CsvParser
import org.apache.cayenne.query.ObjectSelect


import java.time.LocalDate
import java.time.format.DateTimeFormatter

def reader = new CsvParser(new InputStreamReader(new ByteArrayInputStream(contactFile)))

reader.eachLine { line ->

    Contact contact = ObjectSelect.query(Contact)
            .where(Contact.CUSTOM_FIELDS.dot(CustomField.VALUE).eq(line.'contact.customField(AMEP)'))
            .and(Contact.CUSTOM_FIELDS.dot(CustomField.CUSTOM_FIELD_TYPE).dot(CustomFieldType.KEY).eq('amep'))
            .and(Contact.LAST_NAME.eq(line.'contact.lastName'))
            .and(Contact.FIRST_NAME.eq(line.'contact.firstName'))
            .selectFirst(context)

    if (!contact) {
        contact = context.newObject(Contact).with { c ->
            c.lastName = line.'contact.lastName'
            c.firstName = line.'contact.firstName'

            c.allowEmail = false
            c.allowPost = false
            c.allowSms = false

            context.newObject(ContactCustomField).with { customField ->
                CustomFieldType cft = ObjectSelect.query(CustomFieldType)
                        .where(CustomFieldType.KEY.eq('amep'))
                        .selectOne(context)

                customField.customFieldType = cft
                customField.relatedObject = c
                customField.value = line.'contact.customField(AMEP)'
            }
            c
        }
    }

    contact.isMale = line.'contact.isMale'?.toBoolean()
    contact.birthDate = line.'contact.birthDate' ? LocalDate.parse(line.'contact.birthDate', DateTimeFormatter.ofPattern("d/MM/yyyy")) : null
    contact.street = line.'contact.address'
    contact.suburb = line.'contact.suburb'
    contact.postcode = line.'contact.postcode'
    contact.state = line.'contact.state'
    contact.homePhone = line.'contact.homePhone'
    contact.mobilePhone = line.'contact.mobilePhone'
    contact.email = line.'contact.email'

    if (!contact.isStudent) {
        contact.isStudent = true
        context.newObject(Student).with { student ->
            student.contact = contact
        }
    }

    Student student = contact.student
    student.usi = line.'student.usi'
    student.usiStatus = UsiStatus.NON_VERIFIED
    if (StringUtils.isNotBlank(line.'student.labourForceStatus')) {
        student.labourForceStatus = AvetmissStudentLabourStatus.values().find { value -> value.databaseValue == line.'student.labourForceStatus'?.toInteger() }
    }
    Long languageId = line.'language.id'?.trim()?.toLong()
    if (languageId) {
        Language language = ObjectSelect.query(Language)
                .where(Language.WILLOW_ID.eq(languageId))
                .selectFirst(context)
        student.language = language
    }

    Long countryId = line.'country.id'?.trim()?.toLong()
    if (countryId) {
        Country countryOfBirth = ObjectSelect.query(Country)
                .where(Country.WILLOW_ID.eq(countryId))
                .selectFirst(context)
        student.countryOfBirth = countryOfBirth
    }

    AvetmissStudentSchoolLevel highestSchoolLevel

    switch (line.'student.highestSchoolLevel') {
        case '08':
            highestSchoolLevel = AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW
            break
        case '09':
            highestSchoolLevel = AvetmissStudentSchoolLevel.COMPLETED_YEAR_9
            break
        case '10':
            highestSchoolLevel = AvetmissStudentSchoolLevel.COMPLETED_YEAR_10
            break
        case '11':
            highestSchoolLevel = AvetmissStudentSchoolLevel.COMPLETED_YEAR_11
            break
        case '12':
            highestSchoolLevel = AvetmissStudentSchoolLevel.COMPLETED_YEAR_12
            break
        default:
            throw new Exception("highestSchoolLevel not found ${line.'student.highestSchoolLevel'}")
    }
    student.highestSchoolLevel = highestSchoolLevel

    context.commitChanges()
}
