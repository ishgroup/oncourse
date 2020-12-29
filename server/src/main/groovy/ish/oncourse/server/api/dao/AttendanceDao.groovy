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
import ish.common.types.EnrolmentStatus
import ish.oncourse.server.cayenne.Attendance
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.Student
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class AttendanceDao implements ClassRelatedDao<Attendance> {

    Attendance newObject(ObjectContext context, Session session, Student student) {
        Attendance attendance = newObject(context)
        attendance.student = student
        attendance.session = session
        attendance.attendanceType = AttendanceType.UNMARKED
        return attendance
    }

    @Override
    Attendance newObject(ObjectContext context) {
        return context.newObject(Attendance)
    }

    @Override
    Attendance getById(ObjectContext context, Long id) {
        return SelectById.query(Attendance, id).selectOne(context)
    }

    List<Attendance> getByClassId(ObjectContext context, Long courseClassId) {
        List<Long> studentIds = ObjectSelect.columnQuery(Enrolment, Enrolment.STUDENT.dot(Student.ID)).where(Enrolment.COURSE_CLASS.dot(CourseClass.ID).eq(courseClassId))
                .and(Enrolment.STATUS.in(EnrolmentStatus.IN_TRANSACTION, EnrolmentStatus.SUCCESS)).select(context)

        return ObjectSelect.query(Attendance)
                .where(Attendance.SESSION.dot(Session.COURSE_CLASS).dot(CourseClass.ID).eq(courseClassId))
                .and(Attendance.STUDENT.dot(Student.ID).in(studentIds))
                .prefetch(Attendance.STUDENT.dot(Student.CONTACT).joint())
                .orderBy(Attendance.ID.asc())
                .select(context)
    }
}
