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

package ish.oncourse.server.function;

import ish.oncourse.server.cayenne.CustomField;
import ish.oncourse.server.cayenne.CustomFieldType;
import ish.oncourse.server.cayenne.Field;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

public class DeleteCustomFieldTypeWithRelatedFields {
    private ObjectContext context;
    private CustomFieldType customFieldType;

    private DeleteCustomFieldTypeWithRelatedFields() {
    }

    public static DeleteCustomFieldTypeWithRelatedFields valueOf (CustomFieldType type, ObjectContext context){
        var deleteFields = new DeleteCustomFieldTypeWithRelatedFields();
        deleteFields.customFieldType = type;
        deleteFields.context = context;

        return deleteFields;
    }

    public void deleteFieldTypeWithRelatedFields() {

        while (customFieldType.getCustomFields().size() > 0) {
            List<CustomField> fields = ObjectSelect.query(CustomField.class)
                    .where(CustomField.CUSTOM_FIELD_TYPE.dot(CustomFieldType.ID).eq(customFieldType.getId()))
                    .limit(100)
                    .select(context);
            context.deleteObjects(fields);
            context.commitChanges();
        }
        context.deleteObject(customFieldType);
    }
}
