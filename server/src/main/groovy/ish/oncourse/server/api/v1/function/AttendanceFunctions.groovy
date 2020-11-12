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

package ish.oncourse.server.api.v1.function

import ish.common.types.AttendanceType
import ish.oncourse.server.cayenne.Attendance
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.Student

class AttendanceFunctions {

    static void mergeAttendances(Enrolment a, Enrolment b) {
        // when enrolments have same classes for different students
        if (a.courseClass.id == b.courseClass.id) {
            Student aStudent = a.student
            Student bStudent = b.student

            // for all sessions of second enrolment
            b.courseClass.sessions.each { Session sessionOfSecondStudent ->
                // we get attendance for second student
                Attendance deletable = sessionOfSecondStudent.attendance.findAll { Attendance att ->
                    att.student.id == bStudent.id}[0]
                // and search attendance with same session for first student
                Attendance updatable = sessionOfSecondStudent.attendance.findAll { Attendance att ->
                    att.student.id == aStudent.id}[0]

                if (deletable != null){
                    if (updatable != null){
                        if (compareAttendanceType(updatable.attendanceType, deletable.attendanceType) == 1){
                            copyAttendanceBtoA(updatable, deletable)
                        }
                        sessionOfSecondStudent.context.deleteObject(deletable)
                    } else {
                        deletable.setStudent(aStudent)
                    }
                }
            }
        }
    }

    //if a < b returns 1, a > b returns -1, a == b returns 0
    static int compareAttendanceType(AttendanceType a, AttendanceType b) {
        List<Integer> compareVector = [0, 3, 2, 4, 1]
        if (a != b) {
            if (compareVector.indexOf(a.ordinal()) < compareVector.indexOf(b.ordinal()))
                return 1
            else
                return -1
        }
        return 0
    }

    private void copyAttendanceBtoA(Attendance a, Attendance b) {
        a.setAttendanceType(b.getAttendanceType())
        a.setAttendedFrom(b.getAttendedFrom())
        a.setAttendedUntil(b.getAttendedUntil())
        a.setNote(b.getNote())
        a.setCreatedOn(b.getCreatedOn())
        a.setModifiedOn(b.getModifiedOn())
        a.setDurationMinutes(b.getDurationMinutes())
        a.setMarkedByTutor(b.getMarkedByTutor())
        a.setMarkedByTutorDate(b.getMarkedByTutorDate())

    }
}
