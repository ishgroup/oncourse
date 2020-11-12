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
import ish.oncourse.aql.AqlService
import ish.oncourse.aql.CompilationResult
import ish.oncourse.server.api.v1.model.FilterDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.SavedFind
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.util.EntityUtil
import org.apache.cayenne.ObjectContext

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

@CompileStatic
class FilterFunctions {

    static ValidationErrorDTO validateFilter(AqlService aqlService, ObjectContext context, FilterDTO filter) {
        Class<? extends CayenneDataObject> clzz = EntityUtil.entityClassForName(filter.entity)
        CompilationResult aqlCompile = aqlService.compile(filter.expression, clzz, context)
        if (aqlCompile.errors) {
            return new ValidationErrorDTO(null, null, "Invalid search expression: '${filter.expression}'.")
        }
        null
    }

    static ValidationErrorDTO isFilterNull(SavedFind entity) {
        if (entity) {
            return new ValidationErrorDTO(null, null, "Filter with name '${entity.name}' exists.")
        }
        null
    }

    static ValidationErrorDTO isFilterNotNull(SavedFind entity, Long id) {
        if (!entity) {
            return new ValidationErrorDTO(null, null, "Filter with id '${id}' does not exist.")
        }
        null
    }

    static void checkForBadRequest(ValidationErrorDTO error) {
        if (error) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }
    }
}
