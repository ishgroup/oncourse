/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.DatabaseSetup
import ish.common.types.AttendanceType
import ish.common.types.EnrolmentStatus
import ish.common.types.PaymentSource
import org.apache.cayenne.PersistenceState
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/cayenne/sessionTest.xml")
class SessionTest extends CayenneIshTestCase {

    @Override
    protected void dataSourceReplaceValues(ReplacementDataSet rDataSet) {
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -1)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))
    }
    
    @Test
    void testCreateNotMarkedAttendance() {

        // create "not marked" attendance records for all valid enrolments
        Student student = cayenneContext.select(SelectQuery.query(Student.class, ExpressionFactory.matchExp(Student.ID_PROPERTY, 1L))).get(0)
        Student student2 = cayenneContext.select(SelectQuery.query(Student.class, ExpressionFactory.matchExp(Student.ID_PROPERTY, 2L))).get(0)
        Student student3 = cayenneContext.select(SelectQuery.query(Student.class, ExpressionFactory.matchExp(Student.ID_PROPERTY, 3L))).get(0)
        CourseClass cc = cayenneContext.select(SelectQuery.query(CourseClass.class, ExpressionFactory.matchExp(CourseClass.ID_PROPERTY, 1L)))
                .get(0)
        Session session = cayenneContext.select(SelectQuery.query(Session.class, ExpressionFactory.matchExp(Session.ID_PROPERTY, 100L))).get(0)

        Enrolment enrolment = cayenneContext.newObject(Enrolment.class)
        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        enrolment.setStudent(student)
        enrolment.setCourseClass(cc)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)

        Assertions.assertEquals(0, session.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(0, student.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(0, student2.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(0, student3.getAttendances().size(), "Check attendances ")

        cayenneContext.commitChanges()
        student.setPersistenceState(PersistenceState.HOLLOW)
        session.setPersistenceState(PersistenceState.HOLLOW)
        Assertions.assertEquals(1, session.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(2, student.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(0, student2.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(0, student3.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(AttendanceType.UNMARKED, student.getAttendances().get(0).getAttendanceType(), "Check attendances type ")
        Assertions.assertEquals(session, session.getAttendance().get(0).getSession(), "Check attendances session ")
        Assertions.assertEquals(student, student.getAttendances().get(0).getStudent(), "Check attendances student ")

        Enrolment enrolment2 = cayenneContext.newObject(Enrolment.class)
        enrolment2.setStatus(EnrolmentStatus.SUCCESS)
        enrolment2.setStudent(student2)
        enrolment2.setCourseClass(cc)
        enrolment2.setSource(PaymentSource.SOURCE_ONCOURSE)

        cayenneContext.commitChanges()
        student.setPersistenceState(PersistenceState.HOLLOW)
        student2.setPersistenceState(PersistenceState.HOLLOW)
        session.setPersistenceState(PersistenceState.HOLLOW)
        Assertions.assertEquals(2, session.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(2, student.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(2, student2.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(0, student3.getAttendances().size(), "Check attendances ")

        Enrolment enrolment3 = cayenneContext.newObject(Enrolment.class)
        enrolment3.setStatus(EnrolmentStatus.IN_TRANSACTION)
        enrolment3.setStudent(student3)
        enrolment3.setCourseClass(cc)
        enrolment3.setSource(PaymentSource.SOURCE_ONCOURSE)

        Enrolment enrolment4 = cayenneContext.newObject(Enrolment.class)
        enrolment4.setStatus(EnrolmentStatus.FAILED)
        enrolment4.setStudent(student3)
        enrolment4.setCourseClass(cc)
        enrolment4.setSource(PaymentSource.SOURCE_ONCOURSE)

        cayenneContext.commitChanges()

        student.setPersistenceState(PersistenceState.HOLLOW)
        student2.setPersistenceState(PersistenceState.HOLLOW)
        student3.setPersistenceState(PersistenceState.HOLLOW)
        session.setPersistenceState(PersistenceState.HOLLOW)
        Assertions.assertEquals(3, session.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(2, student.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(2, student2.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(2, student3.getAttendances().size(), "Check attendances ")

        // add second session
        Session session3 = cayenneContext.newObject(Session.class)
        session3.setCourseClass(cc)
        session3.setPayAdjustment(4)
        cc.addToSessions(session3)

        Assertions.assertEquals(0, session3.getAttendance().size(), "Check attendances ")

        cayenneContext.commitChanges()
        student.setPersistenceState(PersistenceState.HOLLOW)
        student2.setPersistenceState(PersistenceState.HOLLOW)
        student3.setPersistenceState(PersistenceState.HOLLOW)
        session.setPersistenceState(PersistenceState.HOLLOW)
        session3.setPersistenceState(PersistenceState.HOLLOW)
        Assertions.assertEquals(3, session.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(0, session3.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(2, student.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(2, student2.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(2, student3.getAttendances().size(), "Check attendances ")

        // set SUCESS to 3th entity
        enrolment3.setStatus(EnrolmentStatus.SUCCESS)

        cayenneContext.commitChanges()
        student.setPersistenceState(PersistenceState.HOLLOW)
        student2.setPersistenceState(PersistenceState.HOLLOW)
        student3.setPersistenceState(PersistenceState.HOLLOW)
        session.setPersistenceState(PersistenceState.HOLLOW)
        session3.setPersistenceState(PersistenceState.HOLLOW)
        Assertions.assertEquals(3, session.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(1, session3.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(2, student.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(2, student2.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(3, student3.getAttendances().size(), "Check attendances ")

        // remove enrolment2
        enrolment2.setStatus(EnrolmentStatus.CANCELLED)
        cayenneContext.commitChanges()
        student.setPersistenceState(PersistenceState.HOLLOW)
        student2.setPersistenceState(PersistenceState.HOLLOW)
        student3.setPersistenceState(PersistenceState.HOLLOW)
        session.setPersistenceState(PersistenceState.HOLLOW)
        session3.setPersistenceState(PersistenceState.HOLLOW)
        Assertions.assertEquals(2, session.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(1, session3.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(2, student.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(0, student2.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(3, student3.getAttendances().size(), "Check attendances ")

        cayenneContext.deleteObjects(enrolment3)

        cayenneContext.commitChanges()
        session.setPersistenceState(PersistenceState.HOLLOW)
        session3.setPersistenceState(PersistenceState.HOLLOW)
        Assertions.assertEquals(1, session.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(0, session3.getAttendance().size(), "Check attendances ")
    }
}
