package ish.validation

import ish.oncourse.server.cayenne.Student
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when


class StudentCompletedYearValidatorTest {

    @Test
    void test() {
        String propKey = "propertyKey"
        Student student = mock(Student.class)
        when(student.getYearSchoolCompleted()).thenReturn(1939)

        ValidationResult res = StudentYearCompletedValidator.valueOf(student, propKey).validate()
        Assertions.assertEquals(1, res.getFailures().size())
        Assertions.assertEquals(student, res.getFailures().get(0).getSource())
        Assertions.assertNotNull(res.getIshFailureForKey(propKey))

        when(student.getYearSchoolCompleted()).thenReturn(1940)
        res = StudentYearCompletedValidator.valueOf(student, propKey).validate()
        Assertions.assertEquals(0, res.getFailures().size())
        Assertions.assertNull(res.getIshFailureForKey(propKey))

        when(student.getYearSchoolCompleted()).thenReturn(Calendar.getInstance().get(Calendar.YEAR) + 1)
        res = StudentYearCompletedValidator.valueOf(student, propKey).validate()
        Assertions.assertEquals(1, res.getFailures().size())
        Assertions.assertEquals(student, res.getFailures().get(0).getSource())
        Assertions.assertNotNull(res.getIshFailureForKey(propKey))
    }
}
