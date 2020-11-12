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

import ish.oncourse.server.cayenne.Course
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class CourseDao implements CayenneLayer<Course> {

    @Override
    Course newObject(ObjectContext context) {
        context.newObject(Course)
    }

    @Override
    Course getById(ObjectContext context, Long id) {
        SelectById.query(Course, id)
                .selectOne(context)
    }

    Course getCourseByCode(ObjectContext context, String code) {
        ObjectSelect.query(Course)
                .where(Course.CODE.eq(code))
                .selectOne(context)
    }
}
