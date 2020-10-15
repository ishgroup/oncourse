/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {
    Enrolment currentEnrolment = args.value
    String[] PROVIDED_CODES = ['rw2.5m1', 'rw2.5m', 'rw2.5t', 'rw2.5w', 'w5day', 'rw5pm']
    if (currentEnrolment.originalInvoiceLine.invoiceLineDiscounts?.find { ild -> ild.discount.code == 'repeat'}) {
        if (PROVIDED_CODES.contains(currentEnrolment.courseClass.course.code.toLowerCase())) {
            List<Enrolment> previousEnrolments  = currentEnrolment.student.enrolments.findAll { e ->
                PROVIDED_CODES.contains(e.courseClass.course.code.toLowerCase()) && !e.equalsIgnoreContext(currentEnrolment) && EnrolmentStatus.STATUSES_LEGIT.contains(e.status) && e.courseClass.sessionsCount == 5
            }

            if (previousEnrolments) {
                Enrolment previousEnrolment = previousEnrolments.sort { e -> e.createdOn }.reverse()[0]

                List<Attendance> currentAttendances = currentEnrolment.courseClass.sessions*.attendance.flatten().findAll { a -> a.student.equalsIgnoreContext(previousEnrolment.student) }.sort{ at -> at.session.startDatetime }
                List<Attendance> previousAttendances = previousEnrolment.courseClass.sessions*.attendance.flatten().findAll { a -> a.student.equalsIgnoreContext(previousEnrolment.student) }.sort{ at -> at.session.startDatetime }

                if (currentAttendances.size() == 5 && previousAttendances.size() == 5) {
                    for (int i = 0; i < 5; i++) {
                        if ([AttendanceType.ATTENDED, AttendanceType.PARTIAL, AttendanceType.DID_NOT_ATTEND_WITH_REASON].contains(previousAttendances[i].attendanceType)) {
                            currentAttendances[i].attendanceType = AttendanceType.DID_NOT_ATTEND_WITH_REASON
                        }
                    }
                    currentEnrolment.context.commitChanges()
                }
            }
        }
    }
}
