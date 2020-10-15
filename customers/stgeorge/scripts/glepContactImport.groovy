/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/*(
contactFile=Please select contact CSV file...
)*/


import CustomField
import CustomFieldType
import Student
import CsvParser
import ValidationUtil
import ObjectSelect
import WordUtils
import org.apache.logging.log4j.LogManager

import java.text.SimpleDateFormat

def reader = new CsvParser(new InputStreamReader(new ByteArrayInputStream(contactFile)))
def cfGLEPid = "GLEPid"
CustomFieldType type = ObjectSelect.query(CustomFieldType).and(CustomFieldType.NAME.eq(cfGLEPid)).selectOne(context)
def Logger = LogManager.getLogger("glepContactImport.groovy")

if (type == null) {
    type = context.newObject(CustomFieldType)
    type.name = cfGLEPid
    context.commitChanges()
}

int count = 0
reader.eachLine { line ->

    def glepId = line.ID
    def fContact = ObjectSelect.query(Contact).where(Contact.CUSTOM_FIELDS.dot(CustomField.VALUE).eq(glepId))
            .and(Contact.CUSTOM_FIELDS.dot(CustomField.CUSTOM_FIELD_TYPE).dot(CustomFieldType.NAME).eq("GLEPid")).selectFirst(context)

    if (fContact == null) {
        Logger.warn("Importing glepId:{}",glepId)
        context.newObject(Contact).with { contact ->
            //ID,FirstName,Surname,Street,Suburb,PostCode,HomePhone,WorkPhone,Fax,Sex,BirthDate,EmailAddress,MobilePhone,SuppressAdvertEmails,SuppressAdvertSMS,State
            contact.createdOn = new Date()
            contact.modifiedOn = new Date()
            contact.lastName = WordUtils.capitalize(line.Surname)
            contact.firstName = WordUtils.capitalize(line.FirstName)
            contact.street = WordUtils.capitalize(line.Street)
            contact.suburb = line.Suburb
            contact.postcode = line.PostCode
            contact.homePhone = line.HomePhone
            contact.workPhone = line.WorkPhone
            contact.fax = line.Fax
            contact.isMale = line.Sex?.toBoolean()

            try {
                contact.birthDate = line.BirthDate ? new SimpleDateFormat("dd/MM/yyyy").parse(line.BirthDate) : null
                if (contact.birthDate && contact.birthDate.after(new Date())) {
                    contact.birthDate = null
                    println(String.format("The birth date cannot be in future. %s: %s ", line.ID, line.BirthDate))
                }
            } catch (Exception e) {
                Logger.error(String.format("Wrong date format for %s: %s", line.ID, line.BirthDate), e)
            }

            if (line.EmailAddress) {
                if (ValidationUtil.isValidEmailAddress(line.EmailAddress)) {
                    contact.email = line.EmailAddress
                } else {
                    Logger.error(String.format("Wring email format for %s: %s", line.ID, line.EmailAddress))
                }
            }

            contact.mobilePhone = line.MobilePhone
            contact.allowEmail = !(line.SuppressAdvertEmails?.toBoolean())
            contact.allow = !(line.SuppressAdvertSMS?.toBoolean())
            contact.state = line.State
            contact.isStudent = Boolean.TRUE

            if (contact.isStudent) {
                context.newObject(Student).with { student ->
                    student.contact = contact
                }
            }

            CustomField customField = context.newObject(CustomField.class)
            customField.value = line.ID
            customField.relatedObject = contact
            customField.customFieldType = type
            contact.addToCustomFields(customField)

            try {
                if (count % 10 == 0) {
                    context.commitChanges()
                }
            } catch (Exception e) {
                context.rollbackChanges()
                Logger.error(String.format("Wrong contact: %s", line.ID), e)
            }
        }
    }
    try {
        context.commitChanges()
    } catch (Exception e) {
        context.rollbackChanges()
        Logger.error(String.format("Wrong contact: %s", line.ID), e)
    }
}
