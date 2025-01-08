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

import javax.inject.Inject
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.dao.RoomDao
import ish.oncourse.server.api.v1.function.RoomFunctions
import ish.oncourse.server.api.v1.model.RoomDTO
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.document.DocumentService
import org.apache.cayenne.ObjectContext

import java.time.ZoneOffset

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.GetKioskUrl.getKioskUrl
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.updateDocuments
import static ish.oncourse.server.api.v1.function.HolidayFunctions.toRestHoliday
import static ish.oncourse.server.api.v1.function.HolidayFunctions.updateAvailabilityRules
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import static org.apache.commons.lang3.StringUtils.trimToNull

class RoomApiService extends TaggableApiService<RoomDTO, Room, RoomDao> {

    @Inject
    private DocumentService documentService

    @Inject
    private PreferenceController preferenceController

    @Override
    Class<Room> getPersistentClass() {
        return Room
    }

    @Override
    RoomDTO toRestModel(Room dbRoom) {
        new RoomDTO().with { room ->
            room.id = dbRoom.id
            room.name = dbRoom.name
            room.seatedCapacity = dbRoom.seatedCapacity
            room.siteId = dbRoom.site.id
            room.directions = dbRoom.directions
            room.facilities = dbRoom.facilities
            room.kioskUrl = getKioskUrl(preferenceController.collegeURL, 'room', dbRoom.id)
            room.tags = dbRoom.allTags.collect { it.id }
            room.documents = dbRoom.activeAttachments.collect { toRestDocument(it.document, documentService) }
            room.rules = dbRoom.unavailableRuleRelations*.rule.collect{ toRestHoliday(it as UnavailableRule) }
            room.siteTimeZone = dbRoom.site.localTimezone
            room.createdOn = dbRoom.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            room.modifiedOn = dbRoom.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
            room.customFields = dbRoom?.customFields?.collectEntries { [(it.customFieldType.key) : it.value] }
            room
        }
    }

    @Override
    Room toCayenneModel(RoomDTO dto, Room dbRoom) {
        dbRoom.name = trimToNull(dto.name)
        dbRoom.seatedCapacity = dto.seatedCapacity
        dbRoom.site = getRecordById(dbRoom.context, Site, dto.siteId)
        dbRoom.directions = trimToNull(dto.directions)
        dbRoom.facilities = trimToNull(dto.facilities)

        updateTags(dbRoom, dbRoom.taggingRelations, dto.tags, RoomTagRelation, dbRoom.context)
        updateAvailabilityRules(dbRoom, dbRoom.unavailableRuleRelations*.rule, dto.rules, RoomUnavailableRuleRelation)
        updateDocuments(dbRoom, dbRoom.attachmentRelations, dto.documents, RoomAttachmentRelation, dbRoom.context)
        updateCustomFields(dbRoom.context, dbRoom, dto.customFields, RoomCustomField)

        dbRoom
    }

    @Override
    void validateModelBeforeSave(RoomDTO roomDTO, ObjectContext context, Long id) {
        RoomFunctions.validateForSave(roomDTO, context, id, validator)
    }

    @Override
    void validateModelBeforeRemove(Room cayenneModel) {
        if (!cayenneModel.sessions.empty) {
            validator.throwClientErrorException(cayenneModel.id, 'id', "Cannot delete room assigned to sessions.")
        }
    }


    @Override
    Closure getAction (String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            validator.throwClientErrorException(key, "Unsupported attribute")
        }
        action
    }
}
