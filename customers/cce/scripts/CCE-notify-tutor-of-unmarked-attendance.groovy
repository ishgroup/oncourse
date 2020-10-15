/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {
    def endOfDate = Calendar.getInstance().getTime()
    endOfDate.set(hourOfDay: 0, minute: 0, second: 0)
    def startOfDate = endOfDate - 7

    List<Session> sessions = ObjectSelect.query(Session)
            .where(Session.START_DATETIME.between(startOfDate, endOfDate))
            .and(Session.COURSE_CLASS.dot(CourseClass.IS_CANCELLED).eq(false))
            .and(Session.COURSE_CLASS.dot(CourseClass.IS_ACTIVE).eq(true))
            .select(args.context)

    List<Session> sessionsWithUnmarkedAttendance = sessions.findAll { s ->
        s.attendance.findAll { a ->
            AttendanceType.UNMARKED.equals(a.attendanceType)
        }.size() > 0
    }

    sessionsWithUnmarkedAttendance.each { s ->
        s.tutorRoles.each { role ->

            email {
                template "CCE Tutor notice of unmarked attendance"
                to role.tutor.contact
                bindings s:s, tutor: role.tutor.contact, AttendanceType: AttendanceType
            }
        }
    }


}
