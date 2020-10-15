import ish.oncourse.server.imports.CsvParser

def reader = new CsvParser(new InputStreamReader(new ByteArrayInputStream(contactFile)))

reader.eachLine { line ->

    if (line.email) {
        ObjectSelect.query(Contact)
                .where(Contact.EMAIL.eq(line.email))
                .select(context)
                .each { contact ->

            contact.allowEmail = line.allowEmail?.toBoolean() ?: false
            contact.allowPost = line.allowPost?.toBoolean() ?: false

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