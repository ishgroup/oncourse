package ish.oncourse.get

import groovy.transform.CompileStatic
import ish.common.types.DeliveryMode
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Outcome
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@CompileStatic
class GetDeliveryModeTest {

    @Test
    void testGetDeliveryMode() {
        Outcome outcome1 = mock(Outcome.class)
        when(outcome1.getDeliveryMode()).thenReturn(DeliveryMode.NOT_SET)
        Enrolment enrolment1 = mock(Enrolment.class)
        when(outcome1.getEnrolment()).thenReturn(enrolment1)
        CourseClass courseClass1 = mock(CourseClass.class)
        when(enrolment1.getCourseClass()).thenReturn(courseClass1)
        when(courseClass1.getDeliveryMode()).thenReturn(DeliveryMode.CLASSROOM)

        GetDeliveryMode getDeliveryMode1 = GetDeliveryMode.valueOf(outcome1)
        Assertions.assertEquals(DeliveryMode.NOT_SET, getDeliveryMode1.get())


        Outcome outcome2 = mock(Outcome.class)
        when(outcome2.getDeliveryMode()).thenReturn(null)
        Enrolment enrolment2 = mock(Enrolment.class)
        when(outcome2.getEnrolment()).thenReturn(enrolment2)
        CourseClass courseClass2 = mock(CourseClass.class)
        when(enrolment2.getCourseClass()).thenReturn(courseClass2)
        when(courseClass2.getDeliveryMode()).thenReturn(DeliveryMode.CLASSROOM)

        GetDeliveryMode getDeliveryMode2 = GetDeliveryMode.valueOf(outcome2)
        Assertions.assertEquals(DeliveryMode.CLASSROOM, getDeliveryMode2.get())


        Outcome outcome3 = mock(Outcome.class)
        when(outcome3.getDeliveryMode()).thenReturn(null)
        when(outcome3.getEnrolment()).thenReturn(null)

        GetDeliveryMode getDeliveryMode3 = GetDeliveryMode.valueOf(outcome3)
        Assertions.assertNull(getDeliveryMode3.get())
    }
}
