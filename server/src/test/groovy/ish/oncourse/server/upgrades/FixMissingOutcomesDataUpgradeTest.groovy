/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.upgrades

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.common.types.EnrolmentStatus
import ish.oncourse.server.cayenne.Attendance
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Student
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectQuery
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/upgrades/atttendanceUpgradeDataSet.xml")
class FixMissingOutcomesDataUpgradeTest extends TestWithDatabase {

        @Test
    void testDataUpgrade() throws Exception {
        List<Attendance> attendance = cayenneContext.select(SelectQuery.query(Attendance.class))
        Assertions.assertEquals(24, attendance.size(), "there should be some attendance defined")

        fixEnrolments(cayenneContext)

        attendance = cayenneContext.select(SelectQuery.query(Attendance.class))
        Assertions.assertEquals(105, attendance.size(), "now there should be all the attendance")

        fixEnrolments(cayenneContext)

        attendance = cayenneContext.select(SelectQuery.query(Attendance.class))
        Assertions.assertEquals(105, attendance.size(), "now there should be all the attendance")

        List<Student> students = cayenneContext.select(SelectQuery.query(Student.class))

        for (Student s : students) {
            int expectedNumberOfAttendance = 0

            String enrol = ""
            for (Enrolment e : s.getEnrolments()) {
                if (!(EnrolmentStatus.STATUSES_FAILED.contains(e.getStatus()) || EnrolmentStatus.STATUSES_CANCELLATIONS.contains(e.getStatus()))) {
                    expectedNumberOfAttendance = expectedNumberOfAttendance + e.getCourseClass().getSessionsCount()
                    enrol = enrol + " " + e.getId()
                }

            }
            Assertions.assertEquals(expectedNumberOfAttendance, s.getAttendances().size(), "student " + s.getId() + " enrolments(" + enrol + ")")
        }

    }

    
    private static void fixEnrolments(ObjectContext context) {
        SelectQuery<Enrolment> sq = SelectQuery.query(Enrolment.class)
        List<Enrolment> enrolments = context.select(sq)
        for (Enrolment e : enrolments) {
            e.setModifiedOn(new Date())
        }
        context.commitChanges()

    }
}
