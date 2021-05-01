/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.cayenne.*
import ish.unit.*
import org.apache.cayenne.ObjectContext

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@CompileStatic
class TestHelper {

    private static TestHelper helper
    ObjectContext shared = null

    private static CourseClass testClass = null
    private static Student testStudent = null

    static TestHelper helper() {
        if (helper == null) {
            helper = new TestHelper()
        }
        return helper
    }

    ObjectContext getNewContext() {
        return new MockObjectContext()
    }

    ObjectContext getSharedContext() {
        if (this.shared == null) {
            this.shared = new MockObjectContext()
        }
        return this.shared
    }

    PaymentIn createPayment(final Money amount) {
        return createPayment(amount, null)
    }

    
    PaymentIn createPayment(final Money amount, ish.common.types.PaymentStatus ps) {
        ObjectContext oc = TestHelper.helper().getNewContext()

        final PaymentIn pIn = new MockPaymentIn(1, oc)
        PaymentMethod method = mock(PaymentMethod.class)
        when(method.getType()).thenReturn(PaymentType.CREDIT_CARD)
        when(method.getName()).thenReturn(PaymentType.CREDIT_CARD.getDisplayName())
        // ---- translate fields
        // common fields
        // pIn.writeProperty("id", 1);
        pIn.setCreatedOn(new Date())
        pIn.setModifiedOn(new Date())

        // specific
        SetPaymentMethod.valueOf(method, pIn).set()
        pIn.setCreditCardExpiry("09/09")
        pIn.setCreditCardName("TestName")
        pIn.setCreditCardNumber("4444333322221111")
        pIn.setCreditCardType(ish.common.types.CreditCardType.VISA)
        pIn.setStatus(ps)

        pIn.setPayer(createStudent(123, oc).getContact())

        pIn.setAmount(amount)
        // System.out.println("created payment "+ToStringBuilder.reflectionToString(pIn));
        return pIn
    }

    Enrolment createEnrolment(final int id) {
        return createEnrolment(id, null)
    }

    Enrolment createEnrolment(final int id, ish.common.types.EnrolmentStatus es) {
        final ObjectContext oc = TestHelper.helper().getNewContext()
        final Enrolment enrolment = new MockEnrolment(1, oc)

        // ---- translate fields
        // common fields
        enrolment.writeProperty("id", new Integer(1))
        enrolment.setCreatedOn(new Date())
        enrolment.setModifiedOn(new Date())

        // specific
        enrolment.setStudent(createStudent(id, oc))
        enrolment.setCourseClass(getTestCourseClass(oc))
        enrolment.setStatus(es)

        // System.out.println("created enrolment "+ToStringBuilder.reflectionToString(enrolment));
        return enrolment
    }

    Student createStudent(final int id, final ObjectContext oc) {
        if (testStudent == null) {
            testStudent = MockStudent.newInstance(oc, 1, "Test", "Test")
        }
        return testStudent
    }

    CourseClass getTestCourseClass(final ObjectContext oc) {
        if (testClass == null) {
            testClass = new MockCourseClass(1, oc)
            testClass.setCourse(new MockCourse(1, oc))
            testClass.getCourse().setName("test")
            testClass.getCourse().setCode("ta")
            testClass.setCode("001")
            testClass.setCreatedOn(new Date())
            testClass.setModifiedOn(new Date())
        }
        return testClass
    }

}