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

package ish.oncourse.server.entity

import ish.oncourse.server.cayenne.CustomField
import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.server.cayenne.ExpandableTrait
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

class GetCustomField {
    private String fieldName
    private ExpandableTrait entity
    private ObjectContext context

    private GetCustomField(){}

    static GetCustomField valueOf(ExpandableTrait entity, ObjectContext context, String fieldName){
        GetCustomField getCustomField = new GetCustomField()
        getCustomField.fieldName = fieldName
        getCustomField.entity = entity
        getCustomField.context = context

        getCustomField
    }

    String get(){
        // first try to find directly linked custom field record with a matching name
        for (CustomField cf : (List<CustomField>) entity.getCustomFields()) {
            CustomFieldType cft = ((CustomField) cf).customFieldType
            if (cft.key.equalsIgnoreCase(fieldName) || cft.name.equalsIgnoreCase(fieldName))
                return ((CustomField) cf).value
        }


        // if there is no custom field record try to find matching custom field type with default value specified
        CustomFieldType cft = ObjectSelect.query(CustomFieldType)
            .where(CustomFieldType.ENTITY_IDENTIFIER.eq(entity.class.simpleName))
            .and(CustomFieldType.NAME.eq(fieldName).orExp(CustomFieldType.KEY.eq(fieldName)))
            .selectFirst(context)

        cft?.defaultValue
    }
}
