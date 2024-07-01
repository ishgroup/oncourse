/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.server.CayenneService
import ish.oncourse.server.api.v1.model.ArchiveDTO
import ish.oncourse.server.api.v1.service.ArchiveApi
import ish.oncourse.server.cayenne.Archive
import ish.oncourse.server.messaging.ArchivingMessagesService
import ish.util.LocalDateUtils
import org.apache.cayenne.query.ObjectSelect

class ArchiveApiImpl implements ArchiveApi {
    @Inject
    private CayenneService cayenneService

    @Inject
    private ArchivingMessagesService messagesService

    @Override
    List<ArchiveDTO> getAllArchives() {
        return ObjectSelect.query(Archive)
                .orderBy(Archive.FILE_NAME.name)
                .select(cayenneService.newReadonlyContext)
                .collect { toRest(it) }
    }

    private static ArchiveDTO toRest(Archive archive) {
        return new ArchiveDTO().with { dto ->
            dto.id = archive.id
            dto.fileName = archive.fileName
            dto.createdOn = LocalDateUtils.dateToTimeValue(archive.createdOn)
            dto.dateFrom = LocalDateUtils.dateToTimeValue(archive.dateFrom)
            dto.dateTo = LocalDateUtils.dateToTimeValue(archive.dateTo)
            dto
        }
    }

    @Override
    String getLink(Long id) {
       messagesService.getLink(id)
    }
}
