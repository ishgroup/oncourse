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

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.aql.AqlService
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.dao.CayenneLayer
import ish.oncourse.server.api.traits._DTOTrait
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.validation.EntityValidator
import ish.util.EntityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.Persistent
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.server.api.function.EntityFunctions.parseSearchQuery

@CompileStatic
abstract class EntityApiService<T extends _DTOTrait, K extends Persistent, M extends CayenneLayer<K>> {

    @Inject
    protected ICayenneService cayenneService

    @Inject AqlService aqlService

    @Inject
    protected EntityValidator validator

    @Inject
    protected M entityDao

    final K create(T dto) {
        ObjectContext context = cayenneService.newContext
        validateModelBeforeSave(dto, context, null)
        K cayenneModel = toCayenneModel(dto, entityDao.newObject(context))
        save(cayenneModel.objectContext) // context can be different from above
        return cayenneModel
    }

    final T get(Long id) {
        ObjectContext context = cayenneService.newContext
        Persistent cayenneModel = getEntityAndValidateExistence(context, id)
        toRestModel(cayenneModel)
    }

    final void update(Long id, T restModel) {
        ObjectContext context = cayenneService.newContext
        Persistent cayenneModel = getEntityAndValidateExistence(context, id)
        validateModelBeforeSave(restModel, context, id)
        toCayenneModel(restModel, cayenneModel)
        save(context)
    }

    final void remove(Long id) {
        ObjectContext context = cayenneService.newContext
        Persistent cayenneModel = getEntityAndValidateExistence(context, id)
        validateModelBeforeRemove(cayenneModel)
        remove(cayenneModel, context)
        save(context)
    }

    final K getEntityAndValidateExistence(ObjectContext context, Long id) {
        K entity = entityDao.getById(context, id)
        if (entity == null) {
            validator.throwClientErrorException(id, 'id', "Record with id = '$id' doesn't exist.")
        }
        return entity
    }

    final void save(ObjectContext context) {
        context.commitChanges()
    }

    final void bulkChange(DiffDTO dto) {

        Map.Entry<String, String> nullEntry = dto.diff.entrySet().find { it.value == null }
        if (nullEntry) {
            validator.throwClientErrorException('diff', "Attribute ${nullEntry.key} has null value")
        }
        ObjectContext context = cayenneService.newContext

        if (dto.search ||  dto.filter || !dto.tagGroups.empty) {
            List<K> entities = getBulkEntities(dto, context)

            entities.each { entity ->
                dto.diff.entrySet().each { entry ->
                    Closure action = getAction(entry.key, entry.value)
                    action.call(entity)
                }
            }

        } else {

            dto.ids.each { id ->
                dto.diff.entrySet().each { entry ->
                    Closure action = getAction(entry.key, entry.value)
                    action.call(getEntityAndValidateExistence(context, id))
                }
            }

        }

        save(context)
    }

    final void bulkRemove(DiffDTO dto) {
        ObjectContext context = cayenneService.newContext
        List<K> entities = null

        if(dto.filter || dto.search)
            entities = getBulkEntities(dto, context)
        else
            entities = EntityUtil.getObjectsByIds(context, getPersistentClass() as Class<? extends PersistentObjectI>, dto.ids).collect {it as K}

        try {
            context.deleteObjects(entities)
            context.commitChanges()
        } catch (Exception e) {
            validator.throwClientErrorException("diff", "Unexpected error during delete : ${e.message}")
        }
    }

    final List<K> getBulkEntities(DiffDTO dto, ObjectContext context){
        Class<K> clzz = getPersistentClass()
        ObjectSelect query = ObjectSelect.query(clzz)
        query = parseSearchQuery(query as ObjectSelect, context, aqlService, clzz.simpleName, dto.search, dto.filter, dto.tagGroups)
        query.select(context) as List<K>
    }

    void remove (K persistent, ObjectContext context) {
        context.deleteObject(persistent)
    }

    List<T> getList(Long parentId, Object... extraParams = []) {
        []
    }

    Closure getAction(String key, String value) {
        return null
    }

    abstract Class<K> getPersistentClass()

    abstract T toRestModel(K cayenneModel)

    abstract K toCayenneModel(T dto, K cayenneModel)

    abstract void validateModelBeforeSave(T dto, ObjectContext context, Long id)

    abstract void validateModelBeforeRemove(K cayenneModel)
}
