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

import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Student
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SortOrder

class EnrolmentDao implements CayenneLayer<Enrolment> {

    @Override
    Enrolment newObject(ObjectContext context) {
        context.newObject(Enrolment)
    }

    @Override
    Enrolment getById(ObjectContext context, Long id) {
        SelectById.query(Enrolment, id)
                .selectOne(context)
    }

    int getEnrolmentsCount(ObjectContext context, Student s) {
        ObjectSelect.query(Enrolment.class)
                .where(Enrolment.STUDENT.eq(s)).selectCount(context)
    }

    Enrolment getLastEnrolmentFor(ObjectContext context, Student s) {
        ObjectSelect.query(Enrolment)
                .where(Enrolment.STUDENT.eq(s)).orderBy(Enrolment.ID.name, SortOrder.DESCENDING)
                .selectFirst(context)
    }
}
