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

import ish.common.types.ContactDuplicateStatus
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactDuplicate
import ish.oncourse.server.cayenne.SystemUser
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

class ContactDuplicateDao implements CayenneLayer<ContactDuplicate> {

    @Override
    ContactDuplicate newObject(ObjectContext context) {
        context.newObject(ContactDuplicate)
    }

    @Override
    ContactDuplicate getById(ObjectContext context, Long id) {
        SelectById.query(ContactDuplicate, id)
                .selectOne(context)
    }

    ContactDuplicate newObjectForContacts(ObjectContext context, Contact a, Contact b, SystemUser createdBy) {
        ContactDuplicate contactDuplicate = this.newObject(context)

        contactDuplicate.setContactToUpdate(context.localObject(a))
        contactDuplicate.setContactToDeleteId(b.id)
        contactDuplicate.setContactToDeleteWillowId(b.willowId)
        contactDuplicate.setCreatedByUser(createdBy ? context.localObject(createdBy) : null)
        contactDuplicate.setStatus(ContactDuplicateStatus.IN_TRANSACTION)
        contactDuplicate.setDescription(String.format("To update contactId:%d, studentId:%d, tutorId:%d. To delete contactId:%d, studentId:%d, tutorId:%d.",
                a.id, a.student != null ? a.student.id : null, a.tutor != null ? a.tutor.id : null,
                b.id, b.student != null ? b.student.id : null, b.tutor != null ? b.tutor.id : null))
        contactDuplicate
    }
}
