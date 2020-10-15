/*(
contactFile=Please select contact CSV file...
)*/


import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactNoteRelation
import ish.oncourse.server.cayenne.Note
import ish.oncourse.server.imports.CsvParser
import org.apache.cayenne.query.ObjectSelect

def reader = new CsvParser(new InputStreamReader(new ByteArrayInputStream(contactFile)))

reader.eachLine { line ->

    if (line.email) {
        ObjectSelect.query(Contact)
                .where(Contact.EMAIL.eq(line.email))
                .select(context)
                .each { contact ->

            contact.allowEmail = line.allowEmail?.toBoolean() ?: false
            contact.allowPost = line.allowPost?.toBoolean() ?: false
            contact.allowSms = line.allowSms?.toBoolean() ?: false

            if (line.notes) {
                ContactNoteRelation contactNoteRelation = context.newObject(ContactNoteRelation)
                Note note = context.newObject(Note)
                contactNoteRelation.note = note
                contactNoteRelation.notableEntity = contact
                note.note = line.notes
            }

            context.commitChanges()
        }
    }
}