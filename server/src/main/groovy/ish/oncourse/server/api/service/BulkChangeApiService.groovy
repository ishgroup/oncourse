/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service


import com.google.inject.Inject
import groovy.transform.CompileDynamic
import ish.common.types.MessageStatus
import ish.oncourse.aql.AqlService
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.cayenne.WaitingList
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.util.EntityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.server.api.function.EntityFunctions.parseSearchQuery

@CompileDynamic
class BulkChangeApiService {

    private static final List<Class<? extends CayenneDataObject>> ALLOWED_BULK_DELETE_ENTITIES = List.of(WaitingList, Message)
    private static final String MESSAGE_BULK_DELETE_AQL = "status is QUEUED"

    @Inject
    private Set<EntityApiService> entityApiServices

    @Inject
    private EntityValidator validator

    @Inject
    private ICayenneService cayenneService

    @Inject
    private AqlService aql


    void bulkChange(String entityName, DiffDTO dto){
        Map.Entry<String, String> nullEntry = dto.diff.entrySet().find { it.value == null }
        if (nullEntry) {
            validator.throwClientErrorException('diff', "Attribute ${nullEntry.key} has null value")
        }

        ObjectContext context = cayenneService.newContext
        Class<? extends PersistentObjectI> clzz = EntityUtil.entityClassForName(entityName)

        def apiService = entityApiServices.find {it.persistentClass == clzz}
        if(!apiService)
            validator.throwClientErrorException('entity', "Support of bulk change endpoint for entity not found")

        if (dto.search || dto.filter || !dto.tagGroups.empty) {
            List<? extends PersistentObjectI> entities = getBulkEntities(clzz, dto, context)

            entities.each { entity ->
                dto.diff.entrySet().each { entry ->
                    Closure action = apiService.getAction(entry.key, entry.value)
                    action.call(entity)
                }
            }
        } else {
            dto.ids.each { id ->
                dto.diff.entrySet().each { entry ->
                    Closure action = apiService.getAction(entry.key, entry.value)
                    action.call(apiService.getEntityAndValidateExistence(context, id))
                }
            }
        }

        context.commitChanges()
    }


    void bulkDelete(String entity, DiffDTO dto) {
        ObjectContext context = cayenneService.newContext
        Class<? extends PersistentObjectI> clzz = EntityUtil.entityClassForName(entity)

        if(!ALLOWED_BULK_DELETE_ENTITIES.contains(clzz))
            validator.throwClientErrorException("diff", "Bulk remove of ${entity} is not allowed")

        if(clzz.equals(Message) && !dto.search?.contains(MESSAGE_BULK_DELETE_AQL))
            validator.throwClientErrorException("diff", "Bulk remove of messages that are not queued is not allowed")

        List<? extends PersistentObjectI> entities = getBulkEntities(clzz, dto, context)

        if (entities.empty) {
            validator.throwClientErrorException("diff", "Records for bulk delete are not found")
        }

        if(clzz.equals(Message)){
            if(entities.find {(it as Message).status != MessageStatus.QUEUED})
                validator.throwClientErrorException("diff", "Request returned messages with disallowed status. Bulk remove of messages that are not queued is not allowed")
        }

        try {
            context.deleteObjects(entities)
            context.commitChanges()
        } catch (Exception e) {
            validator.throwClientErrorException("diff", "Unexpected error during delete : ${e.message}")
        }
    }


    private List<? extends PersistentObjectI> getBulkEntities(Class<? extends PersistentObjectI> clzz, DiffDTO dto, ObjectContext context){
        List<? extends PersistentObjectI> entities = []

        if (dto.ids)
            entities = EntityUtil.getObjectsByIds(context, clzz, dto.ids)
        else if (dto.filter || dto.search){
            ObjectSelect query = ObjectSelect.query(clzz)
            query = parseSearchQuery(query as ObjectSelect, context, aql, clzz.simpleName, dto.search, dto.filter, dto.tagGroups)
            entities = query.select(context)
        }

        if (entities.contains(null)) {
            validator.throwClientErrorException("diff", "Record with id ${dto.ids.get(entities.indexOf(null))} not found")
        }

        return entities
    }
}
