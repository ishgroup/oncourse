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

package ish.oncourse.server.cayenne

import ish.oncourse.API
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

trait NotableTrait {

    abstract Long getId()

    abstract String getEntityName()

    abstract ObjectContext getContext()

    abstract List<? extends NoteRelation> getNoteRelations()

    /**
     * @return concatenated private notes
     */
    @API
    String getNotes() {
        List<NoteRelation> relations = noteRelations
        NoteRelation.NOTE.dot(Note.CREATED_ON).desc().orderList(relations)

        if (relations.size() > 0) {
            StringBuilder builder = new StringBuilder()
            for (NoteRelation relation : relations) {
                if (StringUtils.trimToNull(builder.toString()) != null) {
                    builder.append('\n')
                }
                builder.append(relation.note.note)
            }
            return builder.toString()
        }
        return null
    }

    /**
     * @return all related notes
     */
    @API
    List<Note> getAllNotes() {
        return noteRelations*.note
    }
}
