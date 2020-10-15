/*(
contactFile=Please select contact json file...
)*/


import groovy.json.JsonSlurper
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactNoteRelation
import ish.oncourse.server.cayenne.Note
import ish.oncourse.server.cayenne.Student
import org.apache.commons.lang3.text.WordUtils
import org.apache.logging.log4j.LogManager

def json = new JsonSlurper()
def object = json.parse(new ByteArrayInputStream(contactFile))
def Logger = LogManager.getLogger("jsonContactImport.groovy")

long time = System.currentTimeMillis()
int count = 0
object.each { line ->
    context.newObject(Contact).with { contact ->
        contact.lastName = WordUtils.capitalize(line.SURNAME)
        contact.firstName = WordUtils.capitalize(line.FIRST_NAME)
        contact.title = WordUtils.capitalize(line.TITLE)
        contact.isMale = line.SEX == "M" ? Boolean.TRUE : (line.SEX == "F" ? Boolean.FALSE : null)
        contact.postcode = line.POSTCODE
        contact.street = WordUtils.capitalize(line.ADDRESS)
        contact.suburb = WordUtils.capitalize(line.SUBURB)
        contact.homePhone = line.PHONE_HOME
        contact.workPhone = line.PHONE_BUS
        contact.mobilePhone = line.mobilePhone
        contact.fax = line.PHONE_FAX
        contact.email = line.EMAIL ? line.EMAIL.replaceFirst("\\.\$", "").replace(",", ".").replace("..", ".") : null
        contact.deliveryStatusEmail = line.WEB_MARKETING == "Y" ? 1 : 0
        contact.deliveryStatusPost = line.WEB_MARKETING == "Y" ? 1 : 0
        contact.deliveryStatusSms = line.WEB_MARKETING == "Y" ? 1 : 0
        contact.state = line.STATE
        contact.isStudent = Boolean.TRUE

        if (contact.isStudent) {
            context.newObject(Student).with { student ->
                student.contact = contact
            }
        }

        try {
            def values = line.NOTES ? line.NOTES.split("[\\r\\n]+") : []
            values.each { value ->
                context.newObject(ContactNoteRelation).with { relation ->
                    relation.createdOn = new Date()
                    relation.modifiedOn = new Date()
                    relation.notableEntity = contact
                    context.newObject(Note).with { note ->
                        note.note = value
                        note.createdOn = new Date()
                        note.modifiedOn = new Date()
                        relation.note = note
                    }

                }
                if (count%10 == 0) {
                    context.commitChanges()
                }
            }
        } catch (Exception e) {
            context.rollbackChanges()
            Logger.error(String.format("%s %s %s", line.FIRST_NAME, line.SURNAME, line.EMAIL), e)
        }
    }
}