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

package ish.util;

import ish.oncourse.server.cayenne.NotableTrait;
import ish.oncourse.server.cayenne.Note;
import ish.oncourse.server.cayenne.NoteRelation;

import java.util.List;

public class NoteUtil {

    public static <K extends NotableTrait, T extends NoteRelation> Note createNewNote(
            K notableEntity,
            Class<T> relationClass
    ) {

        var context = notableEntity.getContext();
        var relation = relationClass.cast(context.newObject(relationClass));

        if (!notableEntity.getEntityName().equals(relation.getEntityIdentifier())) {
            throw new IllegalArgumentException(String.format("cannot set noted relation of %s to %s", notableEntity, relationClass));
        }

        var note = context.newObject(Note.class);
        relation.setNote(note);
        relation.setNotableEntity(notableEntity);
        return note;
    }

    public static <K extends NotableTrait, T extends NoteRelation> void copyNotes(
            K fromEntity,
            K toEntity,
            Class<T> relationClass
    ) {
        for (var relation : fromEntity.getNoteRelations()) {
            var oldNote = relation.getNote();
            var newNote = NoteUtil.createNewNote(toEntity, relationClass);

            newNote.setNote(oldNote.getNote());
            newNote.setSystemUser(oldNote.getSystemUser());
            newNote.setCreatedOn(oldNote.getCreatedOn());
            newNote.setInteractionDate(oldNote.getInteractionDate());
            if (oldNote.getChangedBy() != null) {
                newNote.setChangedBy(oldNote.getChangedBy());
            }

        }
    }
}
