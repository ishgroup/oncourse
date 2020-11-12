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

import ish.oncourse.server.cayenne.Discount
import ish.persistence.CommonExpressionFactory
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class DiscountDao implements CayenneLayer<Discount> {

    @Override
    Discount newObject(ObjectContext context) {
        context.newObject(Discount)
    }

    @Override
    Discount getById(ObjectContext context, Long id) {
        SelectById.query(Discount, id)
                .selectOne(context)
    }

    static List<Discount> getDefaultDiscounts(ObjectContext context) {
        Date now = new Date()
        return ObjectSelect.query(Discount).where(Discount.ADD_BY_DEFAULT.isTrue())
                .and(Discount.VALID_FROM.isNull().orExp(Discount.VALID_FROM.lte(CommonExpressionFactory.previousMidnight(now))))
                .and(Discount.VALID_TO.isNull().orExp(Discount.VALID_TO.gte(CommonExpressionFactory.nextMidnightMinusOne(now))))
                .select(context)
    }
}
