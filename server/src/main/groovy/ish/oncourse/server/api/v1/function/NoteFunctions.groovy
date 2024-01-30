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

package ish.oncourse.server.api.v1.function

import groovy.transform.CompileStatic
import ish.oncourse.server.api.function.CayenneFunctions
import ish.oncourse.server.api.v1.model.NoteDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.NotableTrait
import ish.oncourse.server.cayenne.Note
import ish.oncourse.server.cayenne.NoteRelation
import ish.oncourse.server.cayenne.SystemUser
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.trimToNull


@CompileStatic
class NoteFunctions {

    static NoteDTO toRestNote(Note dbNote) {
        new NoteDTO().with { note ->
            note.id = dbNote.id
            note.message = dbNote.note

            if(dbNote.systemUser)
                note.createdBy = "$dbNote.systemUser.firstName $dbNote.systemUser.lastName"
            else if(dbNote.createdByTutor)
                note.createdBy = dbNote.createdByTutor.contact.fullName
            else
                note.createdBy = 'system'

            note.created = LocalDateUtils.dateToTimeValue(dbNote.createdOn)
            note.modified = LocalDateUtils.dateToTimeValue(dbNote.modifiedOn)
            note.entityId = dbNote.noteRelations?.notableEntity?.id
            note.entityName = dbNote.noteRelations?.notableEntity?.class?.simpleName
            note.interactionDate = LocalDateUtils.dateToTimeValue(dbNote.interactionDate)
            if (dbNote.changedBy) {
                note.modifiedBy = "$dbNote.changedBy.firstName $dbNote.changedBy.lastName"
            }

            note.readonly = dbNote.createdByTutor
            note
        }
    }

    static Note toDbNote(ObjectContext context, NoteDTO note, SystemUser currentUser, NotableTrait relatedObject, Class<? extends NoteRelation> relationClass) {
        Note dbNote = note.id ?
                CayenneFunctions.getRecordById(context, Note, note.id) :
                context.newObject(Note)
        toDbNote(note, dbNote, currentUser, relatedObject, relationClass)
    }

    static Note toDbNote(NoteDTO note, Note cayenneModel, SystemUser currentUser, NotableTrait relatedObject, Class<? extends NoteRelation> relationClass) {
        ObjectContext context = cayenneModel.context
        String message = trimToNull(note.message)

        if (cayenneModel.newRecord) {
            cayenneModel.note = message
            cayenneModel.systemUser = context.localObject(currentUser)
        } else if (message != cayenneModel.note) {
            cayenneModel.note = message
            cayenneModel.changedBy = context.localObject(currentUser)
        }

        cayenneModel.interactionDate = note.interactionDate?.toDate() ?: note.created?.toDate()  ?: new Date()

        if (!cayenneModel.noteRelations) {
            cayenneModel.context.newObject(relationClass).with { relation ->
                relation.note = cayenneModel
                relation.notableEntity = relatedObject
                relation
            }
        }

        cayenneModel
    }

    static void updateNotes(NotableTrait relatedObject, List<NoteDTO> notes, Class<? extends NoteRelation> relationClass, SystemUser currentUser) {
        List<Long> notesToSave = notes*.id.findAll() as List<Long>
        List<? extends NoteRelation> noteRelations = relatedObject.getNoteRelations()

        ObjectContext context = relatedObject.context

        noteRelations.findAll { !notesToSave.contains(it.note.id) }.each { context.deleteObjects(it, it.note) }

        notes.collect { toDbNote(context, it, currentUser, relatedObject, relationClass) }
    }

    static ValidationErrorDTO validateForSave(NoteDTO note) {
        if (isBlank(note.message)) {
            return new ValidationErrorDTO(note?.id?.toString(), 'message', 'Text is required.')
        }

        null
    }
}
