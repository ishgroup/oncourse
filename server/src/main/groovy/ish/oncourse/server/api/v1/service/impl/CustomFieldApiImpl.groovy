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

import com.google.inject.Inject
import ish.oncourse.server.ICayenneService
import static ish.oncourse.server.api.v1.function.CustomFieldTypeFunctions.updateCustomField
import static ish.oncourse.server.api.v1.function.CustomFieldTypeFunctions.validateData
import static ish.oncourse.server.api.v1.function.CustomFieldTypeFunctions.validateForDelete
import static ish.oncourse.server.api.v1.function.CustomFieldTypeFunctions.validateForUpdate
import ish.oncourse.server.api.v1.model.CustomFieldTypeDTO
import ish.oncourse.server.api.v1.model.DataTypeDTO
import ish.oncourse.server.api.v1.model.EntityTypeDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.CustomFieldApi
import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.server.function.DeleteCustomFieldTypeWithRelatedFields
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.ServerErrorException
import javax.ws.rs.core.Response
import java.time.ZoneOffset

class CustomFieldApiImpl implements CustomFieldApi {

    private static Logger logger = LogManager.logger

    @Inject
    private ICayenneService cayenneService

    @Override
    List<CustomFieldTypeDTO> get() {
        ObjectSelect.query(CustomFieldType)
                .orderBy(CustomFieldType.SORT_ORDER.asc())
                .select(cayenneService.newContext)
                .collect { dbType ->
            new CustomFieldTypeDTO().with { type ->
                type.id = dbType.id.toString()
                type.created = dbType.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
                type.modified = dbType.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
                type.name = dbType.name
                type.entityType = EntityTypeDTO.fromValue(dbType.entityIdentifier)
                type.fieldKey = dbType.key
                type.defaultValue = dbType.defaultValue
                type.sortOrder = dbType.sortOrder
                type.mandatory = dbType.isMandatory
                type.dataType = DataTypeDTO.values()[0].fromDbType(dbType.dataType)
                type.pattern = dbType.pattern
                type
            }
        }
    }

    @Override
    void remove(String id) {
        ObjectContext context = cayenneService.newContext

        ValidationErrorDTO error = validateForDelete(context, id)
        if (error) {
            throw new ClientErrorException(
                    Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }

        try {
            CustomFieldType dbType = SelectById.query(CustomFieldType, id).selectOne(context)
            DeleteCustomFieldTypeWithRelatedFields.valueOf(dbType, context).deleteFieldTypeWithRelatedFields()
            context.commitChanges()

        } catch (Exception e) {
            logger.error("An exception was thrown when trying to delete CustomFieldTypes.", e)
            context.rollbackChanges()
            throw new ServerErrorException('An exception was thrown when trying to delete CustomFieldTypes', Response.Status.INTERNAL_SERVER_ERROR)
        }
    }

    @Override
    void update(List<CustomFieldTypeDTO> customFieldType) {
        ObjectContext context = cayenneService.newContext
        ValidationErrorDTO error = validateData(customFieldType)
        if (error) {
            throw new ClientErrorException(
                    Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }

        customFieldType.each { type ->
            error = validateForUpdate(type, context, customFieldType)
            if (error) {
                context.rollbackChanges()
                throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
            }
            updateCustomField(context, type)
        }
        context.commitChanges()
    }
}
