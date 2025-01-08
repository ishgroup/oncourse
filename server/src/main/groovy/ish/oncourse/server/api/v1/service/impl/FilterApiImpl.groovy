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
import ish.oncourse.aql.AqlService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.v1.model.FilterDTO
import ish.oncourse.server.api.v1.service.FilterApi
import ish.oncourse.server.cayenne.SavedFind
import ish.oncourse.server.services.ISystemUserService
import org.apache.cayenne.Cayenne
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.server.api.v1.function.FilterFunctions.checkForBadRequest
import static ish.oncourse.server.api.v1.function.FilterFunctions.isFilterNotNull
import static ish.oncourse.server.api.v1.function.FilterFunctions.isFilterNull
import static ish.oncourse.server.api.v1.function.FilterFunctions.validateFilter

class FilterApiImpl implements FilterApi {

    @Inject
    private ICayenneService cayenneService
    @Inject
    private ISystemUserService userService
    @Inject
    private AqlService aqlService

    @Override
    List<FilterDTO> get(String entity) {
        ObjectContext context = cayenneService.newContext

        ObjectSelect.query(SavedFind)
                .where(SavedFind.TABLE_NAME.eq(entity)
                .andExp(SavedFind.SYSTEM_USER.eq(userService.currentUser).orExp(SavedFind.SYSTEM_USER.isNull())))
                .orderBy(SavedFind.NAME.asc())
                .select(context)
                .findAll { f -> f.aqlExpressionString}
                .collect { f ->
            new FilterDTO(name: f.name, id: Cayenne.longPKForObject(f),
                    entity: f.tableName,expression: f.aqlExpressionString,
                    showForCurrentOnly: f.systemUser ? true : false)
        }
    }

    @Override
    void save(String entity, FilterDTO filter) {
        checkForBadRequest(validateFilter(aqlService, cayenneService.newReadonlyContext, filter))

        ObjectContext context = cayenneService.newContext
        SavedFind find = ObjectSelect.query(SavedFind)
                .where(SavedFind.TABLE_NAME.eq(filter.entity)
                    .andExp(SavedFind.NAME.eq(filter.name))
                    .andExp(SavedFind.AQL_EXPRESSION_STRING.isNotNull()))
                .selectOne(context)
        checkForBadRequest(isFilterNull(find))
        find = context.newObject(SavedFind)
        find.name = filter.name
        find.aqlExpressionString = filter.expression
        find.tableName = filter.entity
        if (filter.showForCurrentOnly) {
            find.systemUser = context.localObject(userService.currentUser)
        }
        context.commitChanges()
    }

    @Override
    void delete(String entity, Long id) {
        ObjectContext context = cayenneService.newContext

        SavedFind find = ObjectSelect.query(SavedFind)
                .where(SavedFind.ID.eq(id)
                    .andExp(SavedFind.SYSTEM_USER.isNull().orExp(SavedFind.SYSTEM_USER.eq(userService.currentUser))))
                .selectOne(context)

        checkForBadRequest(isFilterNotNull(find, id))
        context.deleteObject(find)
        context.commitChanges()
    }
}
