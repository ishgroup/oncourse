/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util

import ish.TestWithDatabase
import ish.common.types.*
import ish.math.Money
import ish.messaging.ISessionModule
import ish.oncourse.cayenne.CourseClassUtil
import ish.oncourse.generator.DataGenerator
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.ObjectContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import static org.mockito.Matchers.any
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

class CourseClassUtilTest extends TestWithDatabase {

    private static int codeSequence = 0

    @Test
    void testSuccessAndQueuedEnrolments() {
        Student student = createStudent(cayenneContext)

        createEnrolment(cayenneContext, EnrolmentStatus.SUCCESS, student)
        createEnrolment(cayenneContext, EnrolmentStatus.SUCCESS, student)
        createEnrolment(cayenneContext, EnrolmentStatus.FAILED, student)
        createEnrolment(cayenneContext, EnrolmentStatus.REFUNDED, student)
        createEnrolment(cayenneContext, EnrolmentStatus.CANCELLED, student)
        createEnrolment(cayenneContext, EnrolmentStatus.IN_TRANSACTION, student)
        createEnrolment(cayenneContext, EnrolmentStatus.NEW, student)
        createEnrolment(cayenneContext, EnrolmentStatus.QUEUED, student)

        cayenneContext.commitChanges()

        Assertions.assertEquals(8, student.getEnrolments().size())
        Assertions.assertEquals(5, CourseClassUtil.getSuccessAndQueuedEnrolments(student.getEnrolments()).size())
        Assertions.assertEquals(2, CourseClassUtil.getRefundedAndCancelledEnrolments(student.getEnrolments()).size())
    }

    private Enrolment createEnrolment(ObjectContext context, EnrolmentStatus status, Student student) {

        Course course = context.newObject(Course.class)
        course.setCode("AABBDD" + codeSequence++)
        course.setName("courseName")
        course.setFieldConfigurationSchema(DataGenerator.valueOf(context).getFieldConfigurationScheme())
        course.setFeeHelpClass(Boolean.FALSE)

        Account account = context.newObject(Account.class)
        account.setAccountCode("accountCode")
        account.setDescription("description")
        account.setType(AccountType.INCOME)
        account.setIsEnabled(true)
        //commit accounts first than link to taxes (avoid exception with circular dependency on tables)
        context.commitChanges()

        Tax tax = context.newObject(Tax.class)
        tax.setIsGSTTaxType(Boolean.FALSE)
        tax.setTaxCode("taxCode")
        tax.setPayableToAccount(account)
        tax.setReceivableFromAccount(account)

        CourseClass cc = context.newObject(CourseClass.class)
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
        cc.setAttendanceType(CourseClassAttendanceType.NO_INFORMATION)

        Enrolment enrl = context.newObject(Enrolment.class)
        enrl.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrl.setStudent(student)
        enrl.setCourseClass(cc)
        enrl.setStatus(status)

        return enrl
    }

    @Test
    void testAddModuleToAllSessions() throws Exception {
        ISessionModule sessionModule = Mockito.mock(SessionModule.class)

        ObjectContext context = Mockito.mock(ObjectContext.class)
        when(context.newObject(SessionModule.class)).thenReturn((SessionModule) sessionModule)

        Session session = Mockito.mock(Session.class)
        List sessions = new ArrayList()
        sessions.add(session)

        CourseClass courseClass = Mockito.mock(CourseClass.class)
        when(courseClass.getSessions()).thenReturn(sessions)
        when(courseClass.getContext()).thenReturn(context)

        Module module = Mockito.mock(Module.class)

        CourseClassUtil.addModuleToAllSessions(courseClass, module, SessionModule.class)

        verify(sessionModule, Mockito.times(1)).setSession(any())
        verify(sessionModule, Mockito.times(1)).setModule(any())
    }

    private Student createStudent(ObjectContext context) {

        Contact contact = context.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Student student = context.newObject(Student.class)
        contact.setStudent(student)

        return student
    }

}
