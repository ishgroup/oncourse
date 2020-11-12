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

import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactRelation
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class ContactRelationDao implements CayenneLayer<ContactRelation> {
    @Override
    ContactRelation newObject(ObjectContext context) {
        context.newObject(ContactRelation)
    }

    @Override
    ContactRelation getById(ObjectContext context, Long id) {
        SelectById.query(ContactRelation, id)
                .selectOne(context)
    }

    List<ContactRelation> getAllContactRelations(ObjectContext context, Long id) {
        ObjectSelect.query(ContactRelation)
                .where(ContactRelation.FROM_CONTACT.dot(Contact.ID).eq(id).orExp(ContactRelation.TO_CONTACT.dot(Contact.ID).eq(id)))
                .select(context)
    }
}
