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

import ish.oncourse.server.cayenne.Tax
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class TaxDao implements CayenneLayer<Tax> {

    public static final String NON_SUPPLY_TAX_CODE = "*"

    @Override
    Tax newObject(ObjectContext context) {
        context.newObject(Tax)
    }

    @Override
    Tax getById(ObjectContext context, Long id) {
        SelectById.query(Tax, id)
                .selectOne(context)
    }

    Tax getNonSupplyTax(ObjectContext context) {
        ObjectSelect.query(Tax)
                .where(Tax.TAX_CODE.eq(NON_SUPPLY_TAX_CODE))
                .selectOne(context)
    }
}
