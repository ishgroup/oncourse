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

package ish.oncourse.server.querying

import javax.inject.Inject
import ish.oncourse.aql.AqlService
import ish.oncourse.aql.CompilationResult
import ish.oncourse.server.CayenneService
import ish.util.EntityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

class QueryService {

    private final static String  SORT_FIELD = "id"

    @Inject
    AqlService aqlService

    @Inject
    CayenneService cayenneService

    @Inject
    QueryService(AqlService aqlService) {
        this.aqlService = aqlService
    }

    def query(@DelegatesTo(QuerySpec) Closure cl) {
        QuerySpec spec = new QuerySpec()
        Closure build = cl.rehydrate(spec, cl, this)
        build.setResolveStrategy(Closure.DELEGATE_FIRST)
        build()

        ObjectContext context = spec.context?:cayenneService.newContext
        Class<?> entityName = EntityUtil.entityClassForName(spec.entity)

        ObjectSelect query
        if (StringUtils.trimToNull(spec.query)) {
            CompilationResult result = aqlService.compile(spec.query, entityName, context)
            if (!result.errors.empty) {
                throw new IllegalArgumentException(result.errors[0].message)
            }
            query = ObjectSelect.query(entityName)
                    .where(result.cayenneExpression.get())
        }  else {
            query = ObjectSelect.query(entityName)
        }
        if (spec.sort) {
            query = query.orderBy(SORT_FIELD, spec.sort)
        }

        if (spec.first) {
            return query.selectFirst(context)
        } else {
            return query.select(context)
        }
    }

}
