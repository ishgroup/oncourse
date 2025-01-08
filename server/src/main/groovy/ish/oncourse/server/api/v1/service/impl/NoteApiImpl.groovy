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

package ish.oncourse.server.api.v1.service.impl

import javax.inject.Inject
import ish.oncourse.server.api.service.NoteApiService
import ish.oncourse.server.api.v1.model.NoteDTO
import ish.oncourse.server.api.v1.service.NoteApi
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.sql.SQLTransactionRollbackException

class NoteApiImpl implements NoteApi {

    private static final Logger logger = LogManager.getLogger();

    @Inject
    private NoteApiService service

    @Override
    void create(String entityName, Long entityId, NoteDTO note) {
        note.entityId = entityId
        note.entityName = entityName
        try {
            service.create(note)
        } catch (Exception e) {
            logger.catching(e)
            if (ExceptionUtils.indexOfType(e, SQLTransactionRollbackException.class)) {
                logger.error("Try to restart transaction")
                service.create(note)
            }
        }
    }

    @Override
    List<NoteDTO> get(String entityName, Long entityId) {
        return service.getNotesBy(entityName, entityId)
    }

    @Override
    void remove(Long id) {
        service.remove(id)
    }

    @Override
    void update(Long id, NoteDTO note) {
        service.update(id, note)
    }
}
