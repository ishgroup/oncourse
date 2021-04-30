package ish.validation

import ish.messaging.IStudent
import org.junit.jupiter.api.Test

import static org.junit.Assert.*
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class StudentCompletedYearValidatorTest {

    @Test
    void test() {
        String propKey = "propertyKey"
        IStudent student = mock(IStudent.class)
        when(student.getYearSchoolCompleted()).thenReturn(1939)

        ValidationResult res = StudentYearCompletedValidator.valueOf(student, propKey).validate()
        assertEquals(1, res.getFailures().size())
        assertEquals(student, res.getFailures().get(0).getSource())
        assertNotNull(res.getIshFailureForKey(propKey))

        when(student.getYearSchoolCompleted()).thenReturn(1940)
        res = StudentYearCompletedValidator.valueOf(student, propKey).validate()
        assertEquals(0, res.getFailures().size())
        assertNull(res.getIshFailureForKey(propKey))

        when(student.getYearSchoolCompleted()).thenReturn(Calendar.getInstance().get(Calendar.YEAR) + 1)
        res = StudentYearCompletedValidator.valueOf(student, propKey).validate()
        assertEquals(1, res.getFailures().size())
        assertEquals(student, res.getFailures().get(0).getSource())
        assertNotNull(res.getIshFailureForKey(propKey))
    }
}
