/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.dao

import ish.oncourse.server.cayenne.GradingItem
import ish.oncourse.server.cayenne.GradingType
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class GradingTypeDao implements CayenneLayer<GradingType> {

    @Override
    GradingType newObject(ObjectContext context) {
        return context.newObject(GradingType)
    }

    GradingItem newGradingItem(GradingType gradingType, ObjectContext context) {
        return context.newObject(GradingItem).with {item ->
            item.gradingType = gradingType
            item
        }
    }

    @Override
    GradingType getById(ObjectContext context, Long id) {
        return SelectById.query(GradingType, id)
                .selectOne(context)
    }

    static List<GradingType> getGradingTypes(ObjectContext context) {
        return ObjectSelect.query(GradingType)
                .prefetch(GradingType.GRADING_ITEMS.joint())
                .select(context)
    }
}
