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

import ish.oncourse.aql.AqlService
import ish.oncourse.aql.CompilationResult
import ish.oncourse.server.api.v1.model.ScheduleTypeDTO
import ish.oncourse.server.api.v1.model.ScriptDTO
import ish.oncourse.server.api.v1.model.TriggerTypeDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.querying.QuerySpec
import ish.util.EntityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ColumnSelect
import org.apache.cayenne.query.ObjectSelect

import java.util.regex.Matcher
import java.util.regex.Pattern
import ish.oncourse.server.cayenne.Script

class ScriptFunctions {

    static ValidationErrorDTO validateQueries(ObjectContext context, AqlService aqlService, ScriptDTO script) {
        String QUERY_START_RGXP = '(def\\s+\\w+\\s*[=]{1}\\s*query\\s*[{]{1}\\s*)'
        String QUERY_BODY_RGXP = '([a-zA-Z0-9.,"!&|%/*+\\-#=~()<>\\s]+)'
        String QUERY_END_RGXP = '([}]{1})'

        String BODY_ENTITY_RGXP = '(entity\\s+["\']{1})' +
                '(\\w+)' +
                '(["\']{1})' +
                '(\\s+)'
        String BODY_QUERY_RGXP = '(query\\s+["\']{1})' +
                '([a-zA-Z0-9.,"!&|%/*+\\-#=~()<>\\s]+)' +
                '(["\']{1})(\\s+)'

        Closure<String> findCharSequence = { String text, String rgxp, int groupIndex ->
            String result
            Pattern pattern = Pattern.compile(rgxp)
            Matcher matcher = pattern.matcher(text)
            if (matcher.find()) {
                result = matcher.group(groupIndex)
            }
            result
        }

        Closure<ArrayList<QuerySpec>> fetchQueriesFromText = { String body ->
            ArrayList<QuerySpec> list = new ArrayList<>()

            Pattern queryPattern = Pattern.compile(QUERY_START_RGXP + QUERY_BODY_RGXP + QUERY_END_RGXP)
            Matcher queryMatcher = queryPattern.matcher(body)
            while (queryMatcher.find()) {

                String queryBody = queryMatcher.group(2)

                QuerySpec spec = new QuerySpec()
                spec.entity = findCharSequence.call(queryBody, BODY_ENTITY_RGXP, 2)
                spec.query = findCharSequence.call(queryBody, BODY_QUERY_RGXP, 2)
                list.add(spec)
            }
            list
        }

        fetchQueriesFromText.call(script.content).each {spec ->
            if (!spec.entity) {
                EntityValidator.throwClientErrorException('content', 'Query should contain property \'entity\'.')
            }
            if (!spec.query) {
                EntityValidator.throwClientErrorException('content', 'Query should contain property \'query\'.')
            } else {
                Class<?> entityClass = EntityUtil.entityClassForName(spec.entity)
                CompilationResult res = aqlService.compile(spec.query, entityClass, context)
                if (!res.errors.empty) {
                    EntityValidator.throwClientErrorException('content', res.errors[0].message)
                }
            }
        }
        null
    }
}
