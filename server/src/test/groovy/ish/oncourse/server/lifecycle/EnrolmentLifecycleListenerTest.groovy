/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.lifecycle


import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.*
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistenceState
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
class EnrolmentLifecycleListenerTest extends CayenneIshTestCase {

    private ICayenneService cayenneService

    
    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        this.cayenneService = injector.getInstance(ICayenneService.class)

        InputStream st = EnrolmentLifecycleListenerTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/lifecycle/enrolmentLifecycleTest.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)

        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))
        rDataSet.addReplacementObject("[null]", null)

        executeDatabaseOperation(rDataSet)
    }

    @AfterEach
    void tearDown() {
        wipeTables()
    }

    
    @Test
    void testUnsubscribeWaitingList() {

        ObjectContext context = cayenneService.getNewContext()

        Course course1 = SelectById.query(Course.class, 1).selectOne(context)
        Course course2 = SelectById.query(Course.class, 2).selectOne(context)

        CourseClass courseClass1 = SelectById.query(CourseClass.class, 1).selectOne(context)
        SelectById.query(CourseClass.class, 2).selectOne(context)

        Student student = SelectById.query(Student.class, 1).selectOne(context)

        Expression studentCourse1WaitListQualifier = WaitingList.COURSE.eq(course1).andExp(WaitingList.STUDENT.eq(student))
        Expression studentCourse2WaitListQualifier = WaitingList.COURSE.eq(course2).andExp(WaitingList.STUDENT.eq(student))

        Assertions.assertEquals(2, context.select(SelectQuery.query(WaitingList.class)).size())

        Enrolment enrol1 = context.newObject(Enrolment.class)
        enrol1.setStudent(student)
        enrol1.setCourseClass(courseClass1)
        enrol1.setEligibilityExemptionIndicator(false)
        enrol1.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrol1.setStudyReason(StudyReason.STUDY_REASON_NOT_STATED)
        enrol1.setVetFeeIndicator(false)
        enrol1.setVetIsFullTime(false)
        enrol1.setStatus(EnrolmentStatus.IN_TRANSACTION)

        context.commitChanges()

        Assertions.assertFalse(context.select(SelectQuery.query(WaitingList.class, studentCourse1WaitListQualifier)).isEmpty())

        enrol1.setStatus(EnrolmentStatus.SUCCESS)

        context.commitChanges()

        Assertions.assertTrue(context.select(SelectQuery.query(WaitingList.class, studentCourse1WaitListQualifier)).isEmpty())
        Assertions.assertFalse(context.select(SelectQuery.query(WaitingList.class, studentCourse2WaitListQualifier)).isEmpty())
        Assertions.assertEquals(1, context.select(SelectQuery.query(WaitingList.class)).size())
    }

    
    @Test
    void testCancelEnrolmentWithCertificates() {

        ObjectContext context = cayenneService.getNewContext()

        Student student1 = SelectById.query(Student.class, 1).selectOne(context)
        Student student2 = SelectById.query(Student.class, 2).selectOne(context)

        CourseClass courseClass1 = SelectById.query(CourseClass.class, 1).selectOne(context)

        Enrolment enrol1 = context.newObject(Enrolment.class)
        enrol1.setStudent(student1)
        enrol1.setCourseClass(courseClass1)
        enrol1.setEligibilityExemptionIndicator(false)
        enrol1.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrol1.setStudyReason(StudyReason.STUDY_REASON_NOT_STATED)
        enrol1.setVetFeeIndicator(false)
        enrol1.setVetIsFullTime(false)
        enrol1.setStatus(EnrolmentStatus.IN_TRANSACTION)
        enrol1.setConfirmationStatus(ConfirmationStatus.NOT_SENT)
        enrol1.setAttendanceType(CourseClassAttendanceType.NO_INFORMATION)

        context.commitChanges()

        Enrolment enrol2 = context.newObject(Enrolment.class)
        enrol2.setStudent(student2)
        enrol2.setCourseClass(courseClass1)
        enrol2.setEligibilityExemptionIndicator(false)
        enrol2.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrol2.setStudyReason(StudyReason.STUDY_REASON_NOT_STATED)
        enrol2.setVetFeeIndicator(false)
        enrol2.setVetIsFullTime(false)
        enrol2.setStatus(EnrolmentStatus.IN_TRANSACTION)
        enrol2.setConfirmationStatus(ConfirmationStatus.NOT_SENT)
        enrol2.setAttendanceType(CourseClassAttendanceType.NO_INFORMATION)

        context.commitChanges()

        enrol1.setStatus(EnrolmentStatus.SUCCESS)
        enrol1.setStatus(EnrolmentStatus.SUCCESS)

        context.commitChanges()

        enrol1.setPersistenceState(PersistenceState.HOLLOW)
        enrol2.setPersistenceState(PersistenceState.HOLLOW)
        Assertions.assertFalse(enrol1.getOutcomes().isEmpty())
        Assertions.assertFalse(enrol2.getOutcomes().isEmpty())

        Certificate cert = context.newObject(Certificate.class)
        cert.setCertificateNumber(1L)
        cert.setAwardedOn(cert.getCreatedOn().toLocalDate())
        cert.setIsQualification(true)
        cert.setStudent(student1)

        CertificateOutcome co = context.newObject(CertificateOutcome.class)
        co.setCertificate(cert)
        co.setOutcome(enrol1.getOutcomes().get(0))

        context.commitChanges()

        enrol1.setStatus(EnrolmentStatus.CANCELLED)
        enrol2.setStatus(EnrolmentStatus.CANCELLED)

        context.commitChanges()

        enrol1.setPersistenceState(PersistenceState.HOLLOW)
        enrol2.setPersistenceState(PersistenceState.HOLLOW)

        enrol1 = SelectById.query(Enrolment, enrol1.objectId).selectOne(context)
        enrol2 = SelectById.query(Enrolment, enrol2.objectId).selectOne(context)

        // do not delete outcomes when user perform cancel/refund
        Assertions.assertFalse(enrol1.getOutcomes().isEmpty())
        Assertions.assertNotNull(enrol1.getOutcomes().get(0).getCertificateOutcomes())
        Assertions.assertFalse(enrol2.getOutcomes().isEmpty())
    }
}
