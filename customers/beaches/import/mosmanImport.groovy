/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/*(
enrolmentFile=Please select enrolment CSV file...
contactFile=Please select contact CSV file...
)*/


import ish.oncourse.server.cayenne.*
import ish.oncourse.server.imports.CreateUserFriendlyMessage
import ish.oncourse.server.imports.CsvParser
import org.apache.cayenne.query.ObjectSelect

import javax.script.ScriptException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

def readerContact = new CsvParser(new InputStreamReader(new ByteArrayInputStream(contactFile)))
def readerEnrolment = new CsvParser(new InputStreamReader(new ByteArrayInputStream(enrolmentFile)))


rowNumber = 0

try {
    readerContact.eachLine { contactLine ->

        rowNumber++
        def contact = context.newObject(Contact)
        contact.firstName = contactLine.FirstName ?: ""
        contact.lastName = contactLine.Surname ?: ""
        contact.street = contactLine.Street ?: ""
        contact.suburb =  contactLine.Suburb ?: ""
        contact.postcode = contactLine.PostCode ?: ""
        contact.homePhone = contactLine.HomePhone ?: ""
        // contact.workPhone = contactLine.WorkPhone ?: ""
        contact.email = contactLine.EmailAddress ?: ""
        contact.isMale = (contactLine.Sex == 'M') ? true : false
        contact.birthDate = contactLine.BirthDate ? LocalDate.parse(processDate(contactLine.BirthDate), DateTimeFormatter.ofPattern("d/MM/yyyy")) : null
        contact.allowEmail = (!contactLine.SuppressAdvertEmails.toBoolean())  ?: ""

        def student = context.newObject(Student)
        student.contact = contact
        contact.student = student
        // student.indigenousStatus = AvetmissStudentIndigenousStatus.values().find { value -> value.databaseValue == Integer.valueOf(contactLine.IndigenousStatus) } ?: student.indigenousStatus
        // student.priorEducationCode = AvetmissStudentPriorEducation.values().find { value -> value.databaseValue == Integer.valueOf(contactLine.PriorEducationalAchievement) } ?: student.priorEducationCode
        // Language languageSpokenAtHome = ObjectSelect.query(Language.class).where(Language.NAME.likeIgnoreCase(contactLine.Language)).selectFirst(context)
        // student.setLanguage(languageSpokenAtHome)
        // Country countryOfBirth = ObjectSelect.query(Country.class).where(Country.NAME.eq(contactLine.CountryOfBirth)).selectFirst(context)
        // student.setCountryOfBirth(countryOfBirth)

        readerEnrolment.eachLine { enrolmentLine ->
            if(contactLine.ID == enrolmentLine.ClientID) {

                if(enrolmentLine.Category) {
                    context.newObject(ContactCustomField).with { customField ->
                    CustomFieldType cft = ObjectSelect.query(CustomFieldType).where(CustomFieldType.KEY.eq('category')).selectOne(context)
                    customField.customFieldType = cft
                    customField.relatedObject = contact
                    customField.value = enrolmentLine.Category
                    }
                }

                if(enrolmentLine.note) {
                    ContactNoteRelation contactNoteRelation = context.newObject(ContactNoteRelation)
                    Note note = context.newObject(Note)
                    contactNoteRelation.note = note
                    contactNoteRelation.notableEntity = contact
                    note.note = enrolmentLine.note
                }
            }
        }
    }
} catch (Exception e){
    throw new ScriptException(CreateUserFriendlyMessage.valueOf(e, rowNumber).getMessage())
}

context.commitChanges()


def processDate(date) {
    def dateArray = date.split('/')

    if(Integer.valueOf(dateArray[2]) >= 2018) {
        dateArray[2] = '1900'
    }
    newDate = dateArray.join('/')
    return newDate
}
