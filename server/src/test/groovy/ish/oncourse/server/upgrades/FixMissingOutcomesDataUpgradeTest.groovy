/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.upgrades

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.EnrolmentStatus
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Attendance
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Student
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.SelectQuery
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
class FixMissingOutcomesDataUpgradeTest extends CayenneIshTestCase {
    private static final Logger logger = LogManager.getLogger()

    private ICayenneService cayenneService

    
    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        this.cayenneService = injector.getInstance(ICayenneService.class)
    }

    
    @Test
    void testDataUpgrade() throws Exception {
        InputStream st = FixMissingOutcomesDataUpgradeTest.class.getClassLoader()
                .getResourceAsStream("ish/oncourse/server/upgrades/atttendanceUpgradeDataSet.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        executeDatabaseOperation(dataSet)

        DataContext context = this.cayenneService.getNewContext()
        List<Attendance> attendance = context.select(SelectQuery.query(Attendance.class))
        Assertions.assertEquals(24, attendance.size(), "there should be some attendance defined")

        fixEnrolments(context)

        attendance = context.select(SelectQuery.query(Attendance.class))
        Assertions.assertEquals(105, attendance.size(), "now there should be all the attendance")

        fixEnrolments(context)

        attendance = context.select(SelectQuery.query(Attendance.class))
        Assertions.assertEquals(105, attendance.size(), "now there should be all the attendance")

        List<Student> students = context.select(SelectQuery.query(Student.class))

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

    
    private void fixEnrolments(ObjectContext context) {
        SelectQuery<Enrolment> sq = SelectQuery.query(Enrolment.class)
        List<Enrolment> enrolments = context.select(sq)
        for (Enrolment e : enrolments) {
            e.setModifiedOn(new Date())
        }
        context.commitChanges()

    }
}
