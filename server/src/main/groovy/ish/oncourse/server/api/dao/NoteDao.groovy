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

package ish.oncourse.server.api.dao


import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactNoteRelation
import ish.oncourse.server.cayenne.Note
import ish.oncourse.server.cayenne.SystemUser
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

class NoteDao implements CayenneLayer<Note> {

    @Override
    Note newObject(ObjectContext context) {
        context.newObject(Note)
    }

    @Override
    Note getById(ObjectContext context, Long id) {
        SelectById.query(Note, id).selectOne(context)
    }

    Note createNoteRelatedWithContact(ObjectContext context, Contact relatedContact, String noteContent, SystemUser createdBy) {
        Note note = this.newObject(context)
        note.note = noteContent
        if (createdBy) {
            note.systemUser = context.localObject(createdBy)
        }

        note.interactionDate = new Date()

        ContactNoteRelation relation = context.newObject(ContactNoteRelation)
        relation.setNote(note)
        relation.setNotedContact(context.localObject(relatedContact))
        return note
    }
}
