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

package ish.oncourse.server.api.service

import com.google.inject.Inject
import ish.oncourse.server.api.dao.NoteDao
import ish.oncourse.server.api.v1.function.NoteFunctions
import ish.oncourse.server.api.v1.model.NoteDTO
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.users.SystemUserService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.Persistent
import org.apache.cayenne.query.SelectById

public class NoteApiService extends EntityApiService<NoteDTO, Note, NoteDao> {

    private Map<String, List<Class>> NOTABLE_MAP  =
            [
                    (Application.simpleName)              : [Application, ApplicationNoteRelation],
                    (Assessment.simpleName)               : [Assessment, AssessmentNoteRelation],
                    (AssessmentSubmission.simpleName)     : [AssessmentSubmission, AssessmentSubmissionNoteRelation],
                    (Contact.simpleName)                  : [Contact, ContactNoteRelation],
                    (CourseClass.simpleName)              : [CourseClass, CourseClassNoteRelation],
                    (Course.simpleName)                   : [Course, CourseNoteRelation],
                    (Enrolment.simpleName)                : [Enrolment, EnrolmentNoteRelation],
                    (Invoice.simpleName)                  : [Invoice, InvoiceNoteRelation],
                    (Lead.simpleName)                     : [Lead, LeadNoteRelation],
                    (Room.simpleName)                     : [Room, RoomNoteRelation],
                    (Site.simpleName)                     : [Site, SiteNoteRelation]
            ]

    @Inject
    private SystemUserService systemUserService

    @Override
    Class<Note> getPersistentClass() {
        return Note
    }

    @Override
    NoteDTO toRestModel(Note cayenneModel) {
        return NoteFunctions.toRestNote(cayenneModel)
    }

    List<NoteDTO> getNotesBy(String entityName, Long id) {
        NotableTrait notable = validateNotable(entityName, id, cayenneService.newContext)
        (notable.noteRelations as List<NoteRelation>).collect { toRestModel(it.note) }
    }

    @Override
    Note toCayenneModel(NoteDTO dto, Note cayenneModel) {
        NotableTrait notable
        Class<? extends NoteRelation> noteRelationClass
        if (cayenneModel.newRecord) {
            Class<? extends NotableTrait> notableClass = NOTABLE_MAP[dto.entityName][0] as Class<? extends NotableTrait>
            noteRelationClass = NOTABLE_MAP[dto.entityName][1] as Class<? extends NoteRelation>

            notable = SelectById.query(notableClass, dto.entityId).selectOne(cayenneModel.context)
        } else {
            noteRelationClass = cayenneModel.noteRelations.class
            notable = cayenneModel.noteRelations.notableEntity
        }

        return NoteFunctions.toDbNote(dto, cayenneModel,cayenneModel.context.localObject( systemUserService.currentUser),  notable, noteRelationClass)
    }

    @Override
    void validateModelBeforeSave(NoteDTO dto, ObjectContext context, Long id) {
        def error = NoteFunctions.validateForSave(dto)
        if (error) {
            validator.throwClientErrorException(error)
        }
        if (id == null) {
            validateNotable(dto.entityName, dto.entityId, context)
        }
    }

    NotableTrait validateNotable(String entityName, Long entityId, ObjectContext context) {

        if (entityId == null) {
            validator.throwClientErrorException('entityId', 'Related object id is required')
        }
        if (entityName == null) {
            validator.throwClientErrorException('entityName', 'Related object name is required')
        }

        def entry = NOTABLE_MAP[entityName]

        if (entry == null) {
            validator.throwClientErrorException('entityName', 'Related object name is wrong')
        }
        NotableTrait notable = SelectById.query(entry[0] as Class<? extends NotableTrait>, entityId).selectOne(context)
        if (notable == null) {
            validator.throwClientErrorException('entityId', 'Related object doesn\'t exist')
        }
        return notable

    }

    @Override
    void validateModelBeforeRemove(Note cayenneModel) {
        //no restrictions
    }

    @Override
    void remove(Note note, ObjectContext context) {
        context.deleteObjects(note.noteRelations, note)
    }

}
