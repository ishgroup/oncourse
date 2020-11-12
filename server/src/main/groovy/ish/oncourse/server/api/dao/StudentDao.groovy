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

import ish.oncourse.server.cayenne.Student
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

class StudentDao implements CayenneLayer<Student> {
    @Override
    Student newObject(ObjectContext context) {
        context.newObject(Student)
    }

    @Override
    Student getById(ObjectContext context, Long id) {
        SelectById.query(Student, id)
                .selectOne(context)
    }


}
