/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.lifecycle

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.common.types.*
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.PersistenceState
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/lifecycle/enrolmentLifecycleTest.xml")
class EnrolmentLifecycleListenerTest extends TestWithDatabase {

    @Override
    protected void dataSourceReplaceValues(ReplacementDataSet rDataSet) {
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))
        rDataSet.addReplacementObject("[null]", null)
    }
    
    @Test
    void testUnsubscribeWaitingList() {
        Course course1 = SelectById.query(Course.class, 1).selectOne(cayenneContext)
        Course course2 = SelectById.query(Course.class, 2).selectOne(cayenneContext)

        CourseClass courseClass1 = SelectById.query(CourseClass.class, 1).selectOne(cayenneContext)
        SelectById.query(CourseClass.class, 2).selectOne(cayenneContext)

        Student student = SelectById.query(Student.class, 1).selectOne(cayenneContext)

        Expression studentCourse1WaitListQualifier = WaitingList.COURSE.eq(course1).andExp(WaitingList.STUDENT.eq(student))
        Expression studentCourse2WaitListQualifier = WaitingList.COURSE.eq(course2).andExp(WaitingList.STUDENT.eq(student))

        Assertions.assertEquals(2, cayenneContext.select(SelectQuery.query(WaitingList.class)).size())

        Enrolment enrol1 = cayenneContext.newObject(Enrolment.class)
        enrol1.setStudent(student)
        enrol1.setCourseClass(courseClass1)
        enrol1.setEligibilityExemptionIndicator(false)
        enrol1.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrol1.setStudyReason(StudyReason.STUDY_REASON_NOT_STATED)
        enrol1.setVetFeeIndicator(false)
        enrol1.setVetIsFullTime(false)
        enrol1.setStatus(EnrolmentStatus.IN_TRANSACTION)

        cayenneContext.commitChanges()

        Assertions.assertFalse(cayenneContext.select(SelectQuery.query(WaitingList.class, studentCourse1WaitListQualifier)).isEmpty())

        enrol1.setStatus(EnrolmentStatus.SUCCESS)

        cayenneContext.commitChanges()

        Assertions.assertTrue(cayenneContext.select(SelectQuery.query(WaitingList.class, studentCourse1WaitListQualifier)).isEmpty())
        Assertions.assertFalse(cayenneContext.select(SelectQuery.query(WaitingList.class, studentCourse2WaitListQualifier)).isEmpty())
        Assertions.assertEquals(1, cayenneContext.select(SelectQuery.query(WaitingList.class)).size())
    }

    
    @Test
    void testCancelEnrolmentWithCertificates() {
        Student student1 = SelectById.query(Student.class, 1).selectOne(cayenneContext)
        Student student2 = SelectById.query(Student.class, 2).selectOne(cayenneContext)

        CourseClass courseClass1 = SelectById.query(CourseClass.class, 1).selectOne(cayenneContext)

        Enrolment enrol1 = cayenneContext.newObject(Enrolment.class)
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

        cayenneContext.commitChanges()

        Enrolment enrol2 = cayenneContext.newObject(Enrolment.class)
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

        cayenneContext.commitChanges()

        enrol1.setStatus(EnrolmentStatus.SUCCESS)
        enrol1.setStatus(EnrolmentStatus.SUCCESS)

        cayenneContext.commitChanges()

        enrol1.setPersistenceState(PersistenceState.HOLLOW)
        enrol2.setPersistenceState(PersistenceState.HOLLOW)
        Assertions.assertFalse(enrol1.getOutcomes().isEmpty())
        Assertions.assertFalse(enrol2.getOutcomes().isEmpty())

        Certificate cert = cayenneContext.newObject(Certificate.class)
        cert.setCertificateNumber(1L)
        cert.setAwardedOn(cert.getCreatedOn().toLocalDate())
        cert.setIsQualification(true)
        cert.setStudent(student1)

        CertificateOutcome co = cayenneContext.newObject(CertificateOutcome.class)
        co.setCertificate(cert)
        co.setOutcome(enrol1.getOutcomes().get(0))

        cayenneContext.commitChanges()

        enrol1.setStatus(EnrolmentStatus.CANCELLED)
        enrol2.setStatus(EnrolmentStatus.CANCELLED)

        cayenneContext.commitChanges()

        enrol1.setPersistenceState(PersistenceState.HOLLOW)
        enrol2.setPersistenceState(PersistenceState.HOLLOW)

        enrol1 = SelectById.query(Enrolment, enrol1.objectId).selectOne(cayenneContext)
        enrol2 = SelectById.query(Enrolment, enrol2.objectId).selectOne(cayenneContext)

        // do not delete outcomes when user perform cancel/refund
        Assertions.assertFalse(enrol1.getOutcomes().isEmpty())
        Assertions.assertNotNull(enrol1.getOutcomes().get(0).getCertificateOutcomes())
        Assertions.assertFalse(enrol2.getOutcomes().isEmpty())
    }
}
