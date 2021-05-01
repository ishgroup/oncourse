/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne


import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.AttendanceType
import ish.common.types.EnrolmentStatus
import ish.common.types.PaymentSource
import ish.oncourse.server.ICayenneService
import org.apache.cayenne.PersistenceState
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
class SessionTest extends CayenneIshTestCase {

    private ICayenneService cayenneService

    
    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        this.cayenneService = injector.getInstance(ICayenneService.class)

        InputStream st = SessionTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/cayenne/sessionTest.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -1)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))

        executeDatabaseOperation(rDataSet)
    }

    
    @Test
    void testCreateNotMarkedAttendance() {

        // create "not marked" attendance records for all valid enrolments

        DataContext newContext = cayenneService.getNewNonReplicatingContext()

        Student student = newContext.select(SelectQuery.query(Student.class, ExpressionFactory.matchExp(Student.ID_PROPERTY, 1L))).get(0)
        Student student2 = newContext.select(SelectQuery.query(Student.class, ExpressionFactory.matchExp(Student.ID_PROPERTY, 2L))).get(0)
        Student student3 = newContext.select(SelectQuery.query(Student.class, ExpressionFactory.matchExp(Student.ID_PROPERTY, 3L))).get(0)
        CourseClass cc = newContext.select(SelectQuery.query(CourseClass.class, ExpressionFactory.matchExp(CourseClass.ID_PROPERTY, 1L)))
                .get(0)
        Session session = newContext.select(SelectQuery.query(Session.class, ExpressionFactory.matchExp(Session.ID_PROPERTY, 100L))).get(0)

        Enrolment enrolment = newContext.newObject(Enrolment.class)
        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        enrolment.setStudent(student)
        enrolment.setCourseClass(cc)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)

        Assertions.assertEquals(0, session.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(0, student.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(0, student2.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(0, student3.getAttendances().size(), "Check attendances ")

        newContext.commitChanges()
        student.setPersistenceState(PersistenceState.HOLLOW)
        session.setPersistenceState(PersistenceState.HOLLOW)
        Assertions.assertEquals(1, session.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(2, student.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(0, student2.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(0, student3.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(AttendanceType.UNMARKED, student.getAttendances().get(0).getAttendanceType(), "Check attendances type ")
        Assertions.assertEquals(session, session.getAttendance().get(0).getSession(), "Check attendances session ")
        Assertions.assertEquals(student, student.getAttendances().get(0).getStudent(), "Check attendances student ")

        Enrolment enrolment2 = newContext.newObject(Enrolment.class)
        enrolment2.setStatus(EnrolmentStatus.SUCCESS)
        enrolment2.setStudent(student2)
        enrolment2.setCourseClass(cc)
        enrolment2.setSource(PaymentSource.SOURCE_ONCOURSE)

        newContext.commitChanges()
        student.setPersistenceState(PersistenceState.HOLLOW)
        student2.setPersistenceState(PersistenceState.HOLLOW)
        session.setPersistenceState(PersistenceState.HOLLOW)
        Assertions.assertEquals(2, session.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(2, student.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(2, student2.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(0, student3.getAttendances().size(), "Check attendances ")

        Enrolment enrolment3 = newContext.newObject(Enrolment.class)
        enrolment3.setStatus(EnrolmentStatus.IN_TRANSACTION)
        enrolment3.setStudent(student3)
        enrolment3.setCourseClass(cc)
        enrolment3.setSource(PaymentSource.SOURCE_ONCOURSE)

        Enrolment enrolment4 = newContext.newObject(Enrolment.class)
        enrolment4.setStatus(EnrolmentStatus.FAILED)
        enrolment4.setStudent(student3)
        enrolment4.setCourseClass(cc)
        enrolment4.setSource(PaymentSource.SOURCE_ONCOURSE)

        newContext.commitChanges()

        student.setPersistenceState(PersistenceState.HOLLOW)
        student2.setPersistenceState(PersistenceState.HOLLOW)
        student3.setPersistenceState(PersistenceState.HOLLOW)
        session.setPersistenceState(PersistenceState.HOLLOW)
        Assertions.assertEquals(3, session.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(2, student.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(2, student2.getAttendances().size(), "Check attendances ")
        Assertions.assertEquals(2, student3.getAttendances().size(), "Check attendances ")

        // add second session
        Session session3 = newContext.newObject(Session.class)
        session3.setCourseClass(cc)
        session3.setPayAdjustment(4)
        cc.addToSessions(session3)

        Assertions.assertEquals(0, session3.getAttendance().size(), "Check attendances ")

        newContext.commitChanges()
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

        newContext.commitChanges()
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
        newContext.commitChanges()
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

        newContext.deleteObjects(enrolment3)

        newContext.commitChanges()
        session.setPersistenceState(PersistenceState.HOLLOW)
        session3.setPersistenceState(PersistenceState.HOLLOW)
        Assertions.assertEquals(1, session.getAttendance().size(), "Check attendances ")
        Assertions.assertEquals(0, session3.getAttendance().size(), "Check attendances ")
    }
}
