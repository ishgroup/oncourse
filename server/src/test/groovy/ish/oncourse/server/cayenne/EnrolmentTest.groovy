/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.*
import ish.math.Money
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

@CompileStatic
class EnrolmentTest extends CayenneIshTestCase {

    private ICayenneService cayenneService

    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        this.cayenneService = injector.getInstance(ICayenneService.class)

        InputStream st = EnrolmentTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/cayenne/enrolment-outcomeTest.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))

        executeDatabaseOperation(rDataSet)
    }

    // disabling this test until we implement a proper enrolment status safety
    // @Test
    
    void testEnrolmentStatusChanges() {
        DataContext newContext = cayenneService.getNewContext()

        Course course = newContext.newObject(Course.class)
        course.setCode("courseCode")
        course.setName("courseName")

        Tax tax = newContext.newObject(Tax.class)
        tax.setIsGSTTaxType(Boolean.FALSE)
        tax.setTaxCode("taxCode")

        Account account = newContext.newObject(Account.class)
        account.setAccountCode("accountCode")
        account.setDescription("description")
        account.setType(AccountType.INCOME)
        account.setIsEnabled(true)

        tax.setReceivableFromAccount(account)
        tax.setPayableToAccount(account)

        CourseClass cc = newContext.newObject(CourseClass.class)
        cc.setSessionsCount(0)
        cc.setMinimumPlaces(4)
        cc.setMaximumPlaces(5)
        cc.setCode("testCourse")
        cc.setCourse(course)
        cc.setTax(tax)
        cc.setIncomeAccount(account)
        cc.setIsActive(true)
        cc.setFeeExGst(Money.ZERO)
        cc.setTaxAdjustment(Money.ZERO)
        cc.setDeliveryMode(DeliveryMode.CLASSROOM)
        cc.setIsClassFeeApplicationOnly(true)
        cc.setSuppressAvetmissExport(false)

        newContext.commitChanges()

        Contact contact = newContext.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Student student = newContext.newObject(Student.class)
        contact.setStudent(student)

        EnrolmentStatus[] initialStatuses = [null, EnrolmentStatus.NEW, EnrolmentStatus.QUEUED, EnrolmentStatus.IN_TRANSACTION]
        try {
            for (EnrolmentStatus initialStatus : initialStatuses)
                for (EnrolmentStatus s : EnrolmentStatus.STATUSES_FINAL) {
                    Enrolment enrolment = newContext.newObject(Enrolment.class)
                    enrolment.setStatus(initialStatus)
                    enrolment.setStudent(student)
                    enrolment.setCourseClass(cc)
                    enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)

                    enrolment.setStatus(s)
                    newContext.deleteObjects(enrolment)
                }
        } catch (IllegalStateException ise) {
            fail("should be able to set enrolment status: " + ise.getMessage())
        }

        for (EnrolmentStatus initialStatus : EnrolmentStatus.STATUSES_FINAL)
            for (EnrolmentStatus s : EnrolmentStatus.values()) {
                boolean exceptionCaught = false
                Enrolment enrolment = newContext.newObject(Enrolment.class)
                enrolment.setStatus(initialStatus)
                enrolment.setStudent(student)
                enrolment.setCourseClass(cc)
                enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)
                try {
                    enrolment.setStatus(s)
                } catch (IllegalStateException ise) {
                    // all good
                    exceptionCaught = true
                } finally {
                    newContext.deleteObjects(enrolment)
                }
                if (!exceptionCaught) {
                    if (!(EnrolmentStatus.SUCCESS.equals(initialStatus) && (EnrolmentStatus.CANCELLED.equals(s) || EnrolmentStatus.REFUNDED.equals(s)))) {
                        fail("should not be able to change a enrolment status when finalised " + initialStatus + " -> " + s)
                    }
                }
            }

    }

    
    @Test
    void testOutcomeNonVetNoModules() throws Exception {

        DataContext newContext = cayenneService.getNewContext()

        Student student = newContext.select(SelectQuery.query(Student.class, ExpressionFactory.matchExp(Student.ID_PROPERTY, 1L))).get(0)
        CourseClass cc = newContext.select(SelectQuery.query(CourseClass.class, ExpressionFactory.matchExp(CourseClass.ID_PROPERTY, 1L)))
                .get(0)

        Enrolment enrolment = newContext.newObject(Enrolment.class)
        enrolment.setStudent(student)
        enrolment.setCourseClass(cc)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)
        assertEquals(0, enrolment.getOutcomes().size(), "Check outcomes ")

        // test setup ends here

        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        assertEquals(1, enrolment.getOutcomes().size(), "Check outcomes ")
        enrolment.setStatus(EnrolmentStatus.QUEUED)
        newContext.commitChanges()
        assertEquals(1, enrolment.getOutcomes().size(), "Check outcomes ")

        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        assertEquals(1, enrolment.getOutcomes().size(), "Check outcomes ")

        assertEquals(OutcomeStatus.STATUS_NOT_SET, enrolment.getOutcomes().get(0).getStatus(), "Check outcome state")
        assertNull(enrolment.getOutcomes().get(0).getModule(), "Check outcome type")
        Long idOutcomes = enrolment.getOutcomes().get(0).getId()

        enrolment.setStatus(EnrolmentStatus.CANCELLED)// this is a workaround for enrollment change status check
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        // do not delete outcomes on server side when user perform cancel/refund
        assertEquals(1, enrolment.getOutcomes().size(), "Check outcomes ")
        assertNotNull(SelectById.query(Outcome.class, idOutcomes).selectOne(cayenneService.getNewContext()))

        newContext.deleteObjects(enrolment.getOutcomes())
        newContext.deleteObjects(enrolment)
        newContext.commitChanges()

        enrolment = newContext.newObject(Enrolment.class)
        enrolment.setStudent(student)
        enrolment.setCourseClass(cc)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        assertEquals(1, enrolment.getOutcomes().size(), "Check outcomes ")
        idOutcomes = enrolment.getOutcomes().get(0).getId()

        newContext.deleteObjects(enrolment)
        newContext.commitChanges()
        assertNull(SelectById.query(Outcome.class, idOutcomes).selectOne(cayenneService.getNewContext()))

        newContext.deleteObjects(student)
        newContext.commitChanges()

    }

    
    @Test
    void testOutcomeNotVETWithModules() throws Exception {

        DataContext newContext = cayenneService.getNewContext()

        Student student = newContext.select(SelectQuery.query(Student.class, ExpressionFactory.matchExp(Student.ID_PROPERTY, 1L))).get(0)
        CourseClass cc = newContext.select(SelectQuery.query(CourseClass.class, ExpressionFactory.matchExp(CourseClass.ID_PROPERTY, 2L)))
                .get(0)

        Enrolment enrolment = newContext.newObject(Enrolment.class)

        enrolment.setStudent(student)
        enrolment.setCourseClass(cc)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)

        // test setup ends here
        assertEquals(0, enrolment.getOutcomes().size(), "Check outcomes ")
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        assertEquals(3, enrolment.getOutcomes().size(), "Check outcomes ")

        assertEquals(OutcomeStatus.STATUS_NOT_SET, enrolment.getOutcomes().get(0).getStatus(), "Check outcome state")
        assertEquals(OutcomeStatus.STATUS_NOT_SET, enrolment.getOutcomes().get(1).getStatus(), "Check outcome state")
        assertEquals(OutcomeStatus.STATUS_NOT_SET, enrolment.getOutcomes().get(2).getStatus(), "Check outcome state")
        assertNotNull(enrolment.getOutcomes().get(0).getModule(), "Check outcome type")
        assertNotNull(enrolment.getOutcomes().get(1).getModule(), "Check outcome type")
        assertNotNull(enrolment.getOutcomes().get(2).getModule(), "Check outcome type")

        enrolment.setStatus(EnrolmentStatus.QUEUED)
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        assertEquals(3, enrolment.getOutcomes().size(), "Check outcomes ")

        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        newContext.commitChanges()
        assertEquals(3, enrolment.getOutcomes().size(), "Check outcomes ")

        Long idOutcome1 = enrolment.getOutcomes().get(0).getId()
        Long idOutcome2 = enrolment.getOutcomes().get(1).getId()
        Long idOutcome3 = enrolment.getOutcomes().get(2).getId()

        newContext.deleteObjects(enrolment)
        newContext.commitChanges()

        assertNull(SelectById.query(Outcome.class, idOutcome1).selectOne(cayenneService.getNewContext()))
        assertNull(SelectById.query(Outcome.class, idOutcome2).selectOne(cayenneService.getNewContext()))
        assertNull(SelectById.query(Outcome.class, idOutcome3).selectOne(cayenneService.getNewContext()))

        enrolment = newContext.newObject(Enrolment.class)

        enrolment.setStudent(student)
        enrolment.setCourseClass(cc)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrolment.setStatus(EnrolmentStatus.QUEUED)
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        assertEquals(3, enrolment.getOutcomes().size(), "Check outcomes ")

        idOutcome1 = enrolment.getOutcomes().get(0).getId()
        idOutcome2 = enrolment.getOutcomes().get(1).getId()
        idOutcome3 = enrolment.getOutcomes().get(2).getId()

        enrolment.setStatus(EnrolmentStatus.FAILED)
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        assertEquals(0, enrolment.getOutcomes().size(), "Check outcomes ")

        assertNull(SelectById.query(Outcome.class, idOutcome1).selectOne(cayenneService.getNewContext()))
        assertNull(SelectById.query(Outcome.class, idOutcome2).selectOne(cayenneService.getNewContext()))
        assertNull(SelectById.query(Outcome.class, idOutcome3).selectOne(cayenneService.getNewContext()))

        newContext.deleteObjects(enrolment)
        newContext.commitChanges()
        assertNull(SelectById.query(Outcome.class, idOutcome1).selectOne(cayenneService.getNewContext()))
        assertNull(SelectById.query(Outcome.class, idOutcome2).selectOne(cayenneService.getNewContext()))
        assertNull(SelectById.query(Outcome.class, idOutcome3).selectOne(cayenneService.getNewContext()))

        newContext.deleteObjects(student)
        newContext.commitChanges()
    }

    
    @Test
    void testOutcomeVETNoModules() throws Exception {

        DataContext newContext = cayenneService.getNewContext()

        Student student = newContext.select(SelectQuery.query(Student.class, ExpressionFactory.matchExp(Student.ID_PROPERTY, 1L))).get(0)
        CourseClass cc = newContext.select(SelectQuery.query(CourseClass.class, ExpressionFactory.matchExp(CourseClass.ID_PROPERTY, 3L)))
                .get(0)

        Enrolment enrolment = newContext.newObject(Enrolment.class)
        enrolment.setStudent(student)
        enrolment.setCourseClass(cc)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)

        // test setup finishes here
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        assertEquals(1, enrolment.getOutcomes().size(), "Check outcomes ")

        enrolment.setStatus(EnrolmentStatus.QUEUED)
        newContext.commitChanges()
        assertEquals(1, enrolment.getOutcomes().size(), "Check outcomes ")

        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        newContext.commitChanges()
        assertEquals(1, enrolment.getOutcomes().size(), "Check outcomes ")

        assertEquals(OutcomeStatus.STATUS_NOT_SET, enrolment.getOutcomes().get(0).getStatus(), "Check outcome state")
        assertNull(enrolment.getOutcomes().get(0).getModule(), "Check outcome type")
        Long idOutcomes = enrolment.getOutcomes().get(0).getId()

        enrolment.setStatus(EnrolmentStatus.CANCELLED)// this is a workaround for enrollment change status check
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        // do not delete outcomes on server side when user perform cancel/refund
        assertEquals(1, enrolment.getOutcomes().size(), "Check outcomes ")
        assertNotNull(SelectById.query(Outcome.class, idOutcomes).selectOne(cayenneService.getNewContext()))

        newContext.deleteObjects(enrolment.getOutcomes())
        newContext.deleteObjects(enrolment)
        newContext.commitChanges()

        enrolment = newContext.newObject(Enrolment.class)
        enrolment.setStudent(student)
        enrolment.setCourseClass(cc)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        assertEquals(1, enrolment.getOutcomes().size(), "Check outcomes ")
        idOutcomes = enrolment.getOutcomes().get(0).getId()

        newContext.deleteObjects(enrolment)
        newContext.commitChanges()
        assertNull(SelectById.query(Outcome.class, idOutcomes).selectOne(cayenneService.getNewContext()))

        newContext.deleteObjects(student)
        newContext.commitChanges()

    }

    
    @Test
    void testOutcomeVETWithModules() throws Exception {

        DataContext newContext = cayenneService.getNewContext()

        Student student = newContext.select(SelectQuery.query(Student.class, ExpressionFactory.matchExp(Student.ID_PROPERTY, 1L))).get(0)
        CourseClass cc = newContext.select(SelectQuery.query(CourseClass.class, ExpressionFactory.matchExp(CourseClass.ID_PROPERTY, 4L)))
                .get(0)

        Enrolment enrolment = newContext.newObject(Enrolment.class)
        enrolment.setStudent(student)
        enrolment.setCourseClass(cc)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)

        // test setup finishes here
        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        assertEquals(0, enrolment.getOutcomes().size(), "Check outcomes ")
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        assertEquals(3, enrolment.getOutcomes().size(), "Check outcomes ")

        assertEquals(OutcomeStatus.STATUS_NOT_SET, enrolment.getOutcomes().get(0).getStatus(), "Check outcome state")
        assertEquals(OutcomeStatus.STATUS_NOT_SET, enrolment.getOutcomes().get(1).getStatus(), "Check outcome state")
        assertEquals(OutcomeStatus.STATUS_NOT_SET, enrolment.getOutcomes().get(2).getStatus(), "Check outcome state")
        assertNotNull(enrolment.getOutcomes().get(0).getModule(), "Check outcome type")
        assertNotNull(enrolment.getOutcomes().get(1).getModule(), "Check outcome type")
        assertNotNull(enrolment.getOutcomes().get(2).getModule(), "Check outcome type")

        newContext.commitChanges()

        assertEquals(3, enrolment.getOutcomes().size(), "Check outcomes ")

        Long idOutcome1 = enrolment.getOutcomes().get(0).getId()
        Long idOutcome2 = enrolment.getOutcomes().get(1).getId()
        Long idOutcome3 = enrolment.getOutcomes().get(2).getId()

        // try deleting one outcome
        DataContext newContext2 = cayenneService.getNewContext()
        Outcome o = SelectById.query(Outcome.class, idOutcome1).selectOne(newContext2)
        newContext2.deleteObjects(o)
        newContext2.commitChanges()

        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        assertEquals(2, enrolment.getOutcomes().size(), "Check outcomes ")
        assertNull(SelectById.query(Outcome.class, idOutcome1).selectOne(cayenneService.getNewContext()))

        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        newContext.commitChanges()
        assertEquals(2, enrolment.getOutcomes().size(), "Check outcomes ")
        assertNull(SelectById.query(Outcome.class, idOutcome1).selectOne(cayenneService.getNewContext()))

        // now delete whole enrolment
        newContext.deleteObjects(enrolment)
        newContext.commitChanges()

        assertNull(SelectById.query(Outcome.class, idOutcome1).selectOne(cayenneService.getNewContext()))
        assertNull(SelectById.query(Outcome.class, idOutcome2).selectOne(cayenneService.getNewContext()))
        assertNull(SelectById.query(Outcome.class, idOutcome3).selectOne(cayenneService.getNewContext()))

        enrolment = newContext.newObject(Enrolment.class)
        enrolment.setStudent(student)
        enrolment.setCourseClass(cc)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        assertEquals(3, enrolment.getOutcomes().size(), "Check outcomes ")

        idOutcome1 = enrolment.getOutcomes().get(0).getId()
        idOutcome2 = enrolment.getOutcomes().get(1).getId()
        idOutcome3 = enrolment.getOutcomes().get(2).getId()

        enrolment.setStatus(EnrolmentStatus.CANCELLED)// this is a workaround for enrollment change status check
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        // do not delete outcomes on server side when user perform cancel or refund manually (through oncourse UI)
        // it was done on client side if user selected such option
        assertEquals(3, enrolment.getOutcomes().size(), "Check outcomes ")

        assertNotNull(SelectById.query(Outcome.class, idOutcome1).selectOne(cayenneService.getNewContext()))
        assertNotNull(SelectById.query(Outcome.class, idOutcome2).selectOne(cayenneService.getNewContext()))
        assertNotNull(SelectById.query(Outcome.class, idOutcome3).selectOne(cayenneService.getNewContext()))

        newContext.deleteObjects(enrolment.getOutcomes())
        newContext.deleteObjects(enrolment)
        newContext.commitChanges()

        newContext.deleteObjects(student)
        newContext.commitChanges()

    }

    
    @Test
    void testOutcomeVETTraineeship() throws Exception {

        DataContext newContext = cayenneService.getNewContext()

        Student student = newContext.select(SelectQuery.query(Student.class, ExpressionFactory.matchExp(Student.ID_PROPERTY, 2L))).get(0)
        CourseClass cc = newContext.select(SelectQuery.query(CourseClass.class, ExpressionFactory.matchExp(CourseClass.ID_PROPERTY, 5L)))
                .get(0)

        Enrolment enrolment = newContext.newObject(Enrolment.class)
        enrolment.setStudent(student)
        enrolment.setCourseClass(cc)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)

        // test setup ends here

        assertEquals(0, enrolment.getOutcomes().size(), "Check outcomes ")
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        assertEquals(3, enrolment.getOutcomes().size(), "Check outcomes ")

        assertEquals(OutcomeStatus.STATUS_NOT_SET, enrolment.getOutcomes().get(0).getStatus(), "Check outcome state")
        assertEquals(OutcomeStatus.STATUS_NOT_SET, enrolment.getOutcomes().get(1).getStatus(), "Check outcome state")
        assertEquals(OutcomeStatus.STATUS_NOT_SET, enrolment.getOutcomes().get(2).getStatus(), "Check outcome state")
        assertNotNull(enrolment.getOutcomes().get(0).getModule(), "Check outcome type")
        assertNotNull(enrolment.getOutcomes().get(1).getModule(), "Check outcome type")
        assertNotNull(enrolment.getOutcomes().get(2).getModule(), "Check outcome type")

        enrolment.setStatus(EnrolmentStatus.QUEUED)
        newContext.commitChanges()
        assertEquals(3, enrolment.getOutcomes().size(), "Check outcomes ")

        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        newContext.commitChanges()
        assertEquals(3, enrolment.getOutcomes().size(), "Check outcomes ")

        Long idOutcome1 = enrolment.getOutcomes().get(0).getId()
        Long idOutcome2 = enrolment.getOutcomes().get(1).getId()
        Long idOutcome3 = enrolment.getOutcomes().get(2).getId()

        newContext.deleteObjects(enrolment)
        newContext.commitChanges()

        assertNull(SelectById.query(Outcome.class, idOutcome1).selectOne(cayenneService.getNewContext()))
        assertNull(SelectById.query(Outcome.class, idOutcome2).selectOne(cayenneService.getNewContext()))
        assertNull(SelectById.query(Outcome.class, idOutcome3).selectOne(cayenneService.getNewContext()))

        enrolment = newContext.newObject(Enrolment.class)
        enrolment.setStudent(student)
        enrolment.setCourseClass(cc)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)

        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        assertEquals(3, enrolment.getOutcomes().size(), "Check outcomes ")

        idOutcome1 = enrolment.getOutcomes().get(0).getId()
        idOutcome2 = enrolment.getOutcomes().get(1).getId()
        idOutcome3 = enrolment.getOutcomes().get(2).getId()

        enrolment.setStatus(EnrolmentStatus.CANCELLED)// this is a workaround for enrollment change status check
        newContext.commitChanges()
        enrolment = SelectById.query(Enrolment.class, enrolment.getObjectId())
                .selectOne(newContext)
        // do not delete outcomes on server side when user perform cancel or refund manually (through oncourse UI)
        // it was done on client side if user selected such option
        assertEquals(3, enrolment.getOutcomes().size(), "Check outcomes ")

        assertNotNull(SelectById.query(Outcome.class, idOutcome1).selectOne(cayenneService.getNewContext()))
        assertNotNull(SelectById.query(Outcome.class, idOutcome2).selectOne(cayenneService.getNewContext()))
        assertNotNull(SelectById.query(Outcome.class, idOutcome3).selectOne(cayenneService.getNewContext()))

        newContext.deleteObjects(enrolment.getOutcomes())
        newContext.deleteObjects(enrolment)
        newContext.commitChanges()

        newContext.deleteObjects(student)
        newContext.commitChanges()
    }

    
    @Test
    void testEnrolmentPostAddTrigger() throws Exception {

        DataContext newContext = cayenneService.getNewContext()

        Student student = newContext.select(SelectQuery.query(Student.class, ExpressionFactory.matchExp(Student.ID_PROPERTY, 3L))).get(0)
        CourseClass cc = newContext.select(SelectQuery.query(CourseClass.class, ExpressionFactory.matchExp(CourseClass.ID_PROPERTY, 1L)))
                .get(0)

        Enrolment enrolment = newContext.newObject(Enrolment.class)
        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        enrolment.setStudent(student)
        enrolment.setCourseClass(cc)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)

        try {
            newContext.commitChanges()
            enrolment = ObjectSelect.query(Enrolment.class).selectFirst(newContext)
            assertEquals(false, enrolment.getEligibilityExemptionIndicator(), "Check EligibilityExemptionIndicator ")
            assertEquals(false, enrolment.getVetIsFullTime(), "Check VetIsFullTime ")
            assertEquals(false, enrolment.getVetFeeIndicator(), "Check VetFeeIndicator ")
            assertEquals(StudyReason.STUDY_REASON_NOT_STATED, enrolment.getStudyReason(), "Check StudyReason ")
        } finally {
            newContext.deleteObjects(enrolment)
            newContext.deleteObjects(student)
            newContext.commitChanges()
        }

    }

    
    @Test
    void testAttendance() throws Exception {

        DataContext newContext = cayenneService.getNewContext()

        Student student = newContext.select(SelectQuery.query(Student.class, Student.ID.eq(5L))).get(0)
        CourseClass cc = newContext.select(SelectQuery.query(CourseClass.class, CourseClass.ID.eq(5L)))
                .get(0)

        Enrolment enrolment = newContext.newObject(Enrolment.class)
        enrolment.setStudent(student)
        enrolment.setCourseClass(cc)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)

        // test setup ends here

        newContext.commitChanges()
        student = SelectById.query(Student.class, student.getObjectId())
                .selectOne(newContext)
        assertEquals(2, student.getAttendances().size(), "Check attendance ")
        enrolment.setModifiedOn(new Date())
        newContext.commitChanges()
        assertEquals(2, student.getAttendances().size(), "Check attendance ")

        enrolment.setStatus(EnrolmentStatus.CANCELLED)
        newContext.commitChanges()
        student = SelectById.query(Student.class, student.getObjectId())
                .selectOne(newContext)
        assertEquals(0, student.getAttendances().size(), "Check attendance ")

        enrolment.setModifiedOn(new Date())
        newContext.commitChanges()
        assertEquals(0, student.getAttendances().size(), "Check attendance ")

        Enrolment enrolment2 = newContext.newObject(Enrolment.class)
        enrolment2.setStatus(EnrolmentStatus.SUCCESS)
        enrolment2.setStudent(student)
        enrolment2.setCourseClass(cc)
        enrolment2.setSource(PaymentSource.SOURCE_ONCOURSE)

        Session s2 = newContext.select(SelectQuery.query(Session.class, Session.ID.eq(200L))).get(0)

        Attendance a = newContext.newObject(Attendance.class)
        a.setStudent(student)
        a.setSession(s2)
        a.setAttendanceType(AttendanceType.UNMARKED)

        newContext.commitChanges()
        student = SelectById.query(Student.class, student.getObjectId())
                .selectOne(newContext)
        assertEquals(2, student.getAttendances().size(), "Check attendance ")

        newContext.deleteObjects(s2)
        newContext.commitChanges()
        assertEquals(1, student.getAttendances().size(), "Check attendance ")

        enrolment2.setModifiedOn(new Date())
        newContext.commitChanges()
        student = SelectById.query(Student.class, student.getObjectId())
                .selectOne(newContext)
        assertEquals(1, student.getAttendances().size(), "Check attendance ")

        Session s3 = newContext.newObject(Session.class)
        s3.setCourseClass(cc)
        s3.setStartDatetime(new Date(new Date().getTime() + 1000 * 60 * 60 * 36))
        s3.setEndDatetime(new Date(new Date().getTime() + 1000 * 60 * 60 * 38))

        newContext.commitChanges()
        student = SelectById.query(Student.class, student.getObjectId())
                .selectOne(newContext)
        // TODO: attendance should be 3 now...
        assertEquals(1, student.getAttendances().size(), "Check attendance ")

        newContext.deleteObjects(enrolment.getOutcomes())
        newContext.deleteObjects(enrolment2.getOutcomes())
        newContext.deleteObjects(enrolment)
        newContext.deleteObjects(enrolment2)
        newContext.deleteObjects(student)
        newContext.commitChanges()

    }

    
    @Test
    void testStatusConstraints() {
        /**
         * List of allowed status changes: <br>
         * <ul>
         * <li>null -> anything</li>
         * <li>NEW -> anything but null</li>
         * <li>QUEUED -> anything but null/NEW</li>
         * <li>IN_TRANSACTION -> anything but null/NEW/QUEUED</li>
         * <li>SUCCESS -> only CANCELLED/REFUNDED allowed</li>
         * <li>FAILED/FAILED_CARD_DECLINED/FAILED_NO_PLACES -> no further status change allowed</li>
         * <li>CANCELLED/REFUNDED -> no further status change allowed</li>
         * </ul>
         */

        ObjectContext context = cayenneService.getNewNonReplicatingContext()

        // allowed changes

        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.NEW, null))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.NEW, EnrolmentStatus.QUEUED))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.NEW, EnrolmentStatus.IN_TRANSACTION))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.NEW, EnrolmentStatus.SUCCESS))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.NEW, EnrolmentStatus.FAILED))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.NEW, EnrolmentStatus.FAILED_CARD_DECLINED))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.NEW, EnrolmentStatus.FAILED_NO_PLACES))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.NEW, EnrolmentStatus.CANCELLED))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.NEW, EnrolmentStatus.REFUNDED))

        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.QUEUED, null))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.QUEUED, EnrolmentStatus.NEW))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.QUEUED, EnrolmentStatus.IN_TRANSACTION))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.QUEUED, EnrolmentStatus.SUCCESS))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.QUEUED, EnrolmentStatus.FAILED))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.QUEUED, EnrolmentStatus.FAILED_CARD_DECLINED))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.QUEUED, EnrolmentStatus.FAILED_NO_PLACES))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.QUEUED, EnrolmentStatus.CANCELLED))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.QUEUED, EnrolmentStatus.REFUNDED))

        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.IN_TRANSACTION, null))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.IN_TRANSACTION, EnrolmentStatus.NEW))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.IN_TRANSACTION, EnrolmentStatus.QUEUED))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.IN_TRANSACTION, EnrolmentStatus.SUCCESS))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.IN_TRANSACTION, EnrolmentStatus.FAILED))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.IN_TRANSACTION, EnrolmentStatus.FAILED_CARD_DECLINED))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.IN_TRANSACTION, EnrolmentStatus.FAILED_NO_PLACES))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.IN_TRANSACTION, EnrolmentStatus.CANCELLED))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.IN_TRANSACTION, EnrolmentStatus.REFUNDED))

        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.SUCCESS, null))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.SUCCESS, EnrolmentStatus.NEW))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.SUCCESS, EnrolmentStatus.IN_TRANSACTION))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.SUCCESS, EnrolmentStatus.QUEUED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.SUCCESS, EnrolmentStatus.FAILED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.SUCCESS, EnrolmentStatus.FAILED_CARD_DECLINED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.SUCCESS, EnrolmentStatus.FAILED_NO_PLACES))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.SUCCESS, EnrolmentStatus.CANCELLED))
        assertTrue(checkStatusChangeAvailability(context, EnrolmentStatus.SUCCESS, EnrolmentStatus.REFUNDED))

        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED, null))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED, EnrolmentStatus.NEW))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED, EnrolmentStatus.IN_TRANSACTION))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED, EnrolmentStatus.QUEUED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED, EnrolmentStatus.SUCCESS))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED, EnrolmentStatus.FAILED_CARD_DECLINED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED, EnrolmentStatus.FAILED_NO_PLACES))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED, EnrolmentStatus.CANCELLED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED, EnrolmentStatus.REFUNDED))

        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_CARD_DECLINED, null))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_CARD_DECLINED, EnrolmentStatus.NEW))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_CARD_DECLINED, EnrolmentStatus.IN_TRANSACTION))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_CARD_DECLINED, EnrolmentStatus.QUEUED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_CARD_DECLINED, EnrolmentStatus.SUCCESS))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_CARD_DECLINED, EnrolmentStatus.FAILED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_CARD_DECLINED, EnrolmentStatus.FAILED_NO_PLACES))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_CARD_DECLINED, EnrolmentStatus.CANCELLED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_CARD_DECLINED, EnrolmentStatus.REFUNDED))

        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_NO_PLACES, null))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_NO_PLACES, EnrolmentStatus.NEW))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_NO_PLACES, EnrolmentStatus.IN_TRANSACTION))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_NO_PLACES, EnrolmentStatus.QUEUED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_NO_PLACES, EnrolmentStatus.SUCCESS))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_NO_PLACES, EnrolmentStatus.FAILED_CARD_DECLINED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_NO_PLACES, EnrolmentStatus.FAILED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_NO_PLACES, EnrolmentStatus.CANCELLED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.FAILED_NO_PLACES, EnrolmentStatus.REFUNDED))

        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.CANCELLED, null))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.CANCELLED, EnrolmentStatus.NEW))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.CANCELLED, EnrolmentStatus.IN_TRANSACTION))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.CANCELLED, EnrolmentStatus.QUEUED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.CANCELLED, EnrolmentStatus.SUCCESS))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.CANCELLED, EnrolmentStatus.FAILED_CARD_DECLINED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.CANCELLED, EnrolmentStatus.FAILED_NO_PLACES))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.CANCELLED, EnrolmentStatus.FAILED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.CANCELLED, EnrolmentStatus.REFUNDED))

        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.REFUNDED, null))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.REFUNDED, EnrolmentStatus.NEW))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.REFUNDED, EnrolmentStatus.IN_TRANSACTION))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.REFUNDED, EnrolmentStatus.QUEUED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.REFUNDED, EnrolmentStatus.SUCCESS))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.REFUNDED, EnrolmentStatus.FAILED_CARD_DECLINED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.REFUNDED, EnrolmentStatus.FAILED_NO_PLACES))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.REFUNDED, EnrolmentStatus.CANCELLED))
        assertFalse(checkStatusChangeAvailability(context, EnrolmentStatus.REFUNDED, EnrolmentStatus.FAILED))

    }

    
    private boolean checkStatusChangeAvailability(ObjectContext context, EnrolmentStatus from, EnrolmentStatus to) {
        try {
            Enrolment enrolment = context.newObject(Enrolment.class)
            enrolment.setStatus(from)
            enrolment.setStatus(to)

            return true
        } catch (IllegalArgumentException e) {
            return false
        }
    }
}
