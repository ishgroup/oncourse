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

package ish.oncourse.server.api.dao


import ish.oncourse.server.cayenne.UnavailableRule
import ish.oncourse.server.cayenne.UnavailableRuleRelation
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class UnavailableRuleDao implements CayenneLayer<UnavailableRule> {



    @Override
    UnavailableRule newObject(ObjectContext context) {
        return context.newObject(UnavailableRule)
    }

    @Override
    UnavailableRule getById(ObjectContext context, Long id) {
        return SelectById.query(UnavailableRule, id)
                .prefetch(UnavailableRule.RULE_RELATION.joint())
                .selectOne(context)
    }

    List<UnavailableRule> getHolidays(ObjectContext context) {
        return ObjectSelect.query(UnavailableRule)
                .where(UnavailableRule.RULE_RELATION.outer().dot(UnavailableRuleRelation.RULE).isNull())
                .orderBy(UnavailableRule.START_DATE_TIME.asc())
                .select(context)

    }
}
