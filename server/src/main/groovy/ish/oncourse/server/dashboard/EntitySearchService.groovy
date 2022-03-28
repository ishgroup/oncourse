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

package ish.oncourse.server.dashboard

import javax.inject.Inject
import ish.common.types.KeyCode
import ish.common.types.Mask
import ish.oncourse.aql.AqlService
import ish.oncourse.aql.CompilationResult
import ish.oncourse.server.CayenneService
import ish.oncourse.server.api.v1.model.SearchGroupDTO
import ish.oncourse.server.api.v1.model.SearchItemDTO
import ish.oncourse.server.security.api.IPermissionService
import org.apache.cayenne.PersistentObject
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect

abstract class EntitySearchService<T extends PersistentObject> {

    @Inject
    protected AqlService aqlService

    @Inject
    protected CayenneService cayenneService

    @Inject
    protected IPermissionService permissionService

    private Expression getExpression(String search) {
        CompilationResult result = aqlService.compile("~ \"$search\"", entityClass, cayenneService.newReadonlyContext)
        result.errors.empty && result.cayenneExpression.isPresent() ? result.cayenneExpression.get() : null
    }

    abstract Class<T> getEntityClass()

    abstract KeyCode getKeyCode()

    private List<T> getResult(String search) {
        Expression exp = getExpression(search)
        if (exp) {
            ObjectSelect.query(entityClass).where(exp).select(cayenneService.newContext)
        } else {
            []
        }
    }

    SearchGroupDTO getSearchResult(String search) {
        if (!permissionService.currentUserCan(keyCode, Mask.VIEW)) {
            return
        }
        List<T> results = getResult(search)
        if (results.empty) {
            return
        }
        SearchGroupDTO group = new SearchGroupDTO()
        group.entity = entityClass.simpleName
        group.items = results.collect { T t -> toSearchItem(t) } as List<SearchItemDTO>
        group.items.sort{-1*it.id}
        return group
    }

    abstract SearchItemDTO toSearchItem(T obj)

    protected static SearchItemDTO toSearchItem(Long id, String name) {
        new SearchItemDTO().with { si ->
            si.id = id
            si.name = name
            si
        }
    }

}
