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
import ish.oncourse.server.api.v1.model.RoomDTO
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Site
import ish.validation.ValidationUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

@CompileStatic
class RoomFunctions {

    static RoomDTO toRestRoomMinimized(Room dbRoom) {
        new RoomDTO().with { room ->
            room.id = dbRoom.id
            room.siteId = dbRoom.site.id
            room.name = dbRoom.name
            room.seatedCapacity = dbRoom.seatedCapacity
            room
        }
    }

    static void validateForSave(RoomDTO roomDTO, ObjectContext context, Long dbRoomId = null, EntityValidator validator, boolean isSiteExists = true) {
        if (StringUtils.isBlank(roomDTO.name)) {
            validator.throwClientErrorException(roomDTO?.id, 'name', 'Name is required.')
        } else if (roomDTO.name.size() > 150) {
            validator.throwClientErrorException(roomDTO?.id, 'name', 'Name can\'t be more than 150 chars.')
        }

        if (roomDTO.seatedCapacity == null) {
            validator.throwClientErrorException(roomDTO?.id, 'seatedCapacity', 'Seated capacity is required.')
        } else if (roomDTO.seatedCapacity < 0) {
            validator.throwClientErrorException(roomDTO?.id, 'seatedCapacity', 'Seated capacity must be positive.')
        } else if (roomDTO.seatedCapacity > Integer.MAX_VALUE) {
            validator.throwClientErrorException(roomDTO?.id, 'seatedCapacity', 'Seated capacity value is too big.')
        }

        if (isSiteExists && !roomDTO.siteId) {
            validator.throwClientErrorException(roomDTO?.id, 'siteId', 'Site is required.')
        }

        def site = ObjectSelect.query(Site)
                .where(Site.ID.eq(roomDTO.siteId))
                .selectOne(context)

        String virtualRoomUrl = StringUtils.trimToNull(roomDTO.virtualRoomUrl)
        if (isSiteExists) {
            if (!site?.id) {
                validator.throwClientErrorException(site?.id, 'siteId', "Can't bind room to nonexistent site")
            }

            if (!site.isVirtual && virtualRoomUrl != null)
                validator.throwClientErrorException(virtualRoomUrl, 'virtualRoomUrl', "Cannot set virtual room url for not virtual site")
        }

        if (virtualRoomUrl != null) {
            if (!ValidationUtil.isValidUrl(virtualRoomUrl))
                validator.throwClientErrorException(roomDTO?.virtualRoomUrl, 'virtualRoomUrl', 'The virtual room url is incorrect.')
        }

        Long roomId = ObjectSelect.query(Room)
                .where(Room.SITE.dot(Site.ID).eq(roomDTO.siteId))
                .and(Room.NAME.eq(roomDTO.name))
                .selectOne(context)?.id

        if (roomId && roomId != dbRoomId) {
            validator.throwClientErrorException(roomDTO?.id, 'name', 'The name of the room must be unique within the site.')
        }
    }
}
