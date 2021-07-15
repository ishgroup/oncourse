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

import ish.common.types.AttendanceType
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.TutorAttendance
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class TutorAttendanceDao implements ClassRelatedDao<TutorAttendance> {


    TutorAttendance newObject(ObjectContext context, Session session, CourseClassTutor classTutor) {
        TutorAttendance tutorAttendance = newObject(context)
        tutorAttendance.session = session
        tutorAttendance.courseClassTutor = classTutor
        tutorAttendance.attendanceType = AttendanceType.UNMARKED
        return tutorAttendance
    }

    @Override
    TutorAttendance newObject(ObjectContext context) {
        return context.newObject(TutorAttendance)
    }

    @Override
    TutorAttendance getById(ObjectContext context, Long id) {
        return SelectById.query(TutorAttendance, id).selectOne(context)
    }

    List<TutorAttendance> getByClassId(ObjectContext context, Long courseClassId) {
        return ObjectSelect.query(TutorAttendance)
                .where(TutorAttendance.SESSION.dot(Session.COURSE_CLASS).dot(CourseClass.ID).eq(courseClassId))
                .select(context)
    }
}
