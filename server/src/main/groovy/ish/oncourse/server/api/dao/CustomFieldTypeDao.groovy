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

import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.server.cayenne.Queueable
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class CustomFieldTypeDao implements CayenneLayer<CustomFieldType> {
    @Override
    CustomFieldType newObject(ObjectContext context) {
        context.newObject(CustomFieldType)
    }

    @Override
    CustomFieldType getById(ObjectContext context, Long id) {
        SelectById.query(CustomFieldType, id)
                .selectOne(context)
    }

    CustomFieldType getByKey(ObjectContext context, String key) {
        ObjectSelect.query(CustomFieldType)
                .where(CustomFieldType.KEY.eq(key))
                .selectOne(context)
    }

    List<CustomFieldType> getMandatoryTypesForClass(ObjectContext context, Class<? extends Queueable> clazz) {
        ObjectSelect.query(CustomFieldType)
                .where(CustomFieldType.ENTITY_IDENTIFIER.eq(clazz.simpleName).andExp(CustomFieldType.IS_MANDATORY.isTrue()))
                .select(context)
    }
}
