package ish.oncourse.get

import ish.common.types.DeliveryMode
import ish.messaging.ICourseClass
import ish.messaging.IEnrolment
import ish.messaging.IOutcome
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Created by anarut on 8/10/16.
 */
class GetDeliveryModeTest {

    @Test
    void testGetDeliveryMode() {
        IOutcome outcome1 = mock(IOutcome.class)
        when(outcome1.getDeliveryMode()).thenReturn(DeliveryMode.NOT_SET)
        IEnrolment enrolment1 = mock(IEnrolment.class)
        when(outcome1.getEnrolment()).thenReturn(enrolment1)
        ICourseClass courseClass1 = mock(ICourseClass.class)
        when(enrolment1.getCourseClass()).thenReturn(courseClass1)
        when(courseClass1.getDeliveryMode()).thenReturn(DeliveryMode.CLASSROOM)

        GetDeliveryMode getDeliveryMode1 = GetDeliveryMode.valueOf(outcome1)
        assertEquals(DeliveryMode.NOT_SET, getDeliveryMode1.get())


        IOutcome outcome2 = mock(IOutcome.class)
        when(outcome2.getDeliveryMode()).thenReturn(null)
        IEnrolment enrolment2 = mock(IEnrolment.class)
        when(outcome2.getEnrolment()).thenReturn(enrolment2)
        ICourseClass courseClass2 = mock(ICourseClass.class)
        when(enrolment2.getCourseClass()).thenReturn(courseClass2)
        when(courseClass2.getDeliveryMode()).thenReturn(DeliveryMode.CLASSROOM)

        GetDeliveryMode getDeliveryMode2 = GetDeliveryMode.valueOf(outcome2)
        assertEquals(DeliveryMode.CLASSROOM, getDeliveryMode2.get())


        IOutcome outcome3 = mock(IOutcome.class)
        when(outcome3.getDeliveryMode()).thenReturn(null)
        when(outcome3.getEnrolment()).thenReturn(null)

        GetDeliveryMode getDeliveryMode3 = GetDeliveryMode.valueOf(outcome3)
        assertNull(getDeliveryMode3.get())
    }
}
