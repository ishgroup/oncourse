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

import groovy.transform.CompileStatic
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.v1.model.ModuleDTO
import ish.oncourse.server.api.v1.service.ModuleApi
import ish.oncourse.server.cayenne.Module
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

import javax.inject.Inject

import static ish.oncourse.server.api.function.EntityFunctions.*
import static ish.oncourse.server.api.v1.function.ModuleFunctions.*

@CompileStatic
class ModuleApiImpl implements ModuleApi {

    @Inject private ICayenneService cayenneService

    @Override
    void create(ModuleDTO module) {

        ObjectContext context = cayenneService.newContext
        Module entity = context.newObject(Module)

        checkForBadRequest(validateModelRequiredFields(module, context))

        toDbModule(module, entity)
        context.commitChanges()
    }

    @Override
    ModuleDTO get(Long id) {
        checkForBadRequest(validateIdParam(id))

        toModuleModel(SelectById.query(Module, id)
                .selectOne(cayenneService.newContext))
    }

    @Override
    void update(Long id, ModuleDTO module) {

        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext
        Module entity =
                SelectById.query(Module, id).selectOne(context)

        checkForBadRequest(validateEntityExistence(id, entity))
        checkForBadRequest(validateModelRequiredFields(module, context, entity.isCustom, entity.id))
        checkForBadRequest(validateValues(module, entity))

        toDbModule(module, entity, entity.isCustom)
        context.commitChanges()
    }

    @Override
    void remove(Long id) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext
        Module entity =
                SelectById.query(Module, id).selectOne(context)
        checkForBadRequest(validateEntityExistence(id, entity))
        checkForBadRequest(validateForDelete(entity))

        context.deleteObjects(entity)
        context.commitChanges()
    }
}
