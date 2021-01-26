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
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.function.CayenneFunctions
import ish.oncourse.server.document.DocumentService

import static ish.oncourse.server.api.function.GetKioskUrl.getKioskUrl
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.updateDocuments
import static ish.oncourse.server.api.v1.function.HolidayFunctions.toRestHoliday
import static ish.oncourse.server.api.v1.function.HolidayFunctions.updateAvailabilityRules
import static ish.oncourse.server.api.v1.function.TagFunctions.toRestTagMinimized
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import ish.oncourse.server.api.v1.model.RoomDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.RoomAttachmentRelation
import ish.oncourse.server.cayenne.RoomTagRelation
import ish.oncourse.server.cayenne.RoomUnavailableRuleRelation
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.cayenne.UnavailableRule
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.ZoneOffset
@CompileStatic
class RoomFunctions {

    static RoomDTO toRestRoom(Room dbRoom, PreferenceController preferenceController, DocumentService documentService) {
        new RoomDTO().with { room ->
            room.id = dbRoom.id
            room.name = dbRoom.name
            room.seatedCapacity = dbRoom.seatedCapacity
            room.siteId = dbRoom.site.id
            room.directions = dbRoom.directions
            room.facilities = dbRoom.facilities
            room.kioskUrl = getKioskUrl(preferenceController.collegeURL, 'room', dbRoom.id)
            room.tags = dbRoom.tags.collect { toRestTagMinimized(it) }
            room.documents = dbRoom.activeAttachments.collect { toRestDocument(it.document, it.documentVersion?.id, documentService) }
            room.rules = dbRoom.unavailableRuleRelations*.rule.collect{ toRestHoliday(it as UnavailableRule) }
            room.siteTimeZone = dbRoom.site.localTimezone
            room.createdOn = dbRoom.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            room.modifiedOn = dbRoom.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            room
        }
    }

    static RoomDTO toRestRoomMinimized(Room dbRoom) {
        new RoomDTO().with { room ->
            room.id = dbRoom.id
            room.siteId = dbRoom.site.id
            room.name = dbRoom.name
            room.seatedCapacity = dbRoom.seatedCapacity
            room
        }
    }

    static Room toDbRoom(RoomDTO room, Room dbRoom, ObjectContext context, SystemUser currentUser) {
        dbRoom.name = trimToNull(room.name)
        dbRoom.seatedCapacity = room.seatedCapacity
        dbRoom.site = CayenneFunctions.getRecordById(context, Site, room.siteId)
        dbRoom.directions = trimToNull(room.directions)
        dbRoom.facilities = trimToNull(room.facilities)

        updateTags(dbRoom, dbRoom.taggingRelations, room.tags*.id, RoomTagRelation, context)
        updateAvailabilityRules(dbRoom, dbRoom.unavailableRuleRelations*.rule, room.rules, RoomUnavailableRuleRelation)
        updateDocuments(dbRoom, dbRoom.attachmentRelations, room.documents, RoomAttachmentRelation, context)

        dbRoom
    }


    static ValidationErrorDTO validateForDelete(Room entity) {
        if (!entity.sessions.empty) {
            return new ValidationErrorDTO(entity?.id?.toString(), 'id', "Cannot delete room assigned to sessions.")
        }
        null
    }

    static ValidationErrorDTO validateForSave(RoomDTO room, ObjectContext context, Long dbRoomId = null, boolean isSiteExists = true) {
        if (StringUtils.isBlank(room.name)) {
            return new ValidationErrorDTO(room?.id?.toString(), 'name', 'Name is required.')
        } else if (room.name.size() > 150) {
            return new ValidationErrorDTO(room?.id?.toString(), 'name', 'Name can\'t be more than 150 chars.')
        }

        if (room.seatedCapacity == null) {
            return new ValidationErrorDTO(room?.id?.toString(), 'seatedCapacity', 'Seated capacity is required.')
        } else if (room.seatedCapacity < 0) {
            return new ValidationErrorDTO(room?.id?.toString(), 'seatedCapacity', 'Seated capacity must be positive.')
        } else if (room.seatedCapacity > Integer.MAX_VALUE) {
            return new ValidationErrorDTO(room?.id?.toString(), 'seatedCapacity', 'Seated capacity value is too big.')
        }

        if (isSiteExists && !room.siteId) {
            return new ValidationErrorDTO(room?.id?.toString(), 'siteId', 'Site is required.')
        }

        if (isSiteExists) {
            Long siteId = ObjectSelect.query(Site)
                    .where(Site.ID.eq(room.siteId))
                    .selectOne(context)?.id
            if (!siteId) {
                return new ValidationErrorDTO(room?.siteId?.toString(), 'siteId', "Can't bind room to nonexistent site")
            }
        }

        Long roomId = ObjectSelect.query(Room)
                .where(Room.SITE.dot(Site.ID).eq(room.siteId))
                .and(Room.NAME.eq(room.name))
                .selectOne(context)?.id

        if (roomId && roomId != dbRoomId) {
            return new ValidationErrorDTO(room?.id?.toString(), 'name', 'The name of the room must be unique within the site.')
        }

        ValidationErrorDTO error = null

        return error
    }
}
