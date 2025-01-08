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
import ish.oncourse.server.ICayenneService
import static ish.oncourse.server.api.v1.function.ConcessionTypeFunctions.toRestConcessionType
import static ish.oncourse.server.api.v1.function.ConcessionTypeFunctions.updateConcession
import static ish.oncourse.server.api.v1.function.ConcessionTypeFunctions.validateData
import static ish.oncourse.server.api.v1.function.ConcessionTypeFunctions.validateForDelete
import static ish.oncourse.server.api.v1.function.ConcessionTypeFunctions.validateForUpdate
import ish.oncourse.server.api.v1.model.ConcessionTypeDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.ConcessionApi
import ish.oncourse.server.cayenne.ConcessionType
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.StringUtils

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

class ConcessionApiImpl implements ConcessionApi {

    @Inject
    private ICayenneService cayenneService

    @Override
    List<ConcessionTypeDTO> get() {
        ObjectSelect.query(ConcessionType)
                .select(cayenneService.newContext)
                .collect { toRestConcessionType(it) }
    }

    @Override
    void remove(String id) {
        ObjectContext context = cayenneService.newContext
        if (!StringUtils.isNumeric(id)) {
            throw new ClientErrorException(
                    Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, 'id', "Concession type id is incorrect. It must consist of only numbers")).build())
        }
        ConcessionType dbType = SelectById.query(ConcessionType, id).selectFirst(context)
        ValidationErrorDTO error = validateForDelete(dbType)
        if (error) {
            throw new ClientErrorException(
                    Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        } else {
            context.deleteObject(dbType)
            context.commitChanges()
        }
    }

    @Override
    void update(List<ConcessionTypeDTO> concessionTypes) {
        ObjectContext context = cayenneService.newContext
        ValidationErrorDTO error = validateData(concessionTypes)
        if (error) {
            throw new ClientErrorException(
                    Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }

        concessionTypes.each { type ->
            error = validateForUpdate(type, context, concessionTypes)
            if (error) {
                context.rollbackChanges()
                throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
            }
            updateConcession(type, context)
        }
        context.commitChanges()
    }

}
