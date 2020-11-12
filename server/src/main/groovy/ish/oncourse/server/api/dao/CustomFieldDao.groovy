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

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactCustomField
import ish.oncourse.server.cayenne.CustomField
import ish.oncourse.server.cayenne.CustomFieldType
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

@CompileStatic
class CustomFieldDao implements CayenneLayer<CustomField> {
    @Override
    CustomField newObject(ObjectContext context) {
        context.newObject(CustomField)
    }

    @Override
    CustomField getById(ObjectContext context, Long id) {
        SelectById.query(CustomField, id)
                .selectOne(context)
    }

    static List<ContactCustomField> getCustomFieldsForContacts(ObjectContext context, Long... ids) {
        ObjectSelect.query(ContactCustomField)
                .where(ContactCustomField.RELATED_OBJECT.in(ids.toList() as Collection<Contact>))
                .select(context)
    }

    static ContactCustomField getContactCustomFieldByKey(ObjectContext context, Contact c, String key) {
        ObjectSelect.query(ContactCustomField)
                .where(ContactCustomField.CUSTOM_FIELD_TYPE.dot(CustomFieldType.KEY).eq(key).andExp(ContactCustomField.RELATED_OBJECT.eq(c)))
                .selectOne(context)
    }

    static boolean isContactCustomFieldExists(ObjectContext context, Contact c, String key) {
        ObjectSelect.query(ContactCustomField)
                .where(ContactCustomField.CUSTOM_FIELD_TYPE.dot(CustomFieldType.KEY).eq(key).andExp(ContactCustomField.RELATED_OBJECT.eq(c)))
                .selectCount(context) > 0
    }
}
