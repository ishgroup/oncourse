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

package ish.oncourse.server.api.function

import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.Persistent
import org.apache.cayenne.query.PrefetchTreeNode
import org.apache.cayenne.query.SelectById
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.ServerErrorException
import javax.ws.rs.core.Response

class CayenneFunctions {

    private static final Logger logger = LogManager.getLogger(CayenneFunctions)

    static void deleteRecord(ObjectContext context, Persistent... objectToDelete) {
        try {
            context.deleteObjects(objectToDelete)
            context.commitChanges()
        } catch (Exception e) {
            logger.catching(e)
            throw new ServerErrorException("Unexpected error during delete '${objectToDelete.class.simpleName}': ${e.message}", Response.Status.INTERNAL_SERVER_ERROR)
        }
    }

    static <T extends Persistent> T getRecordById(ObjectContext context, Class<T> dbClass, Long id, PrefetchTreeNode... prefetch = null) {
        SelectById query = SelectById.query(dbClass, id)

        if (prefetch) {
            prefetch.each { p ->
                query.prefetch(p)
            }
        }

        Persistent result = query.selectOne(context)

        if(!result) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ValidationErrorDTO(id?.toString(), dbClass.simpleName, "${dbClass.simpleName} with id:$id doesn't exist"))
                    .build())
        }

        return result
    }
}
