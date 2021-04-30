package ish.validation;

import ish.oncourse.cayenne.StudentInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.util.Calendar;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class StudentValidatorTest {

    @Test
    public void testCorrectStudentYearSchoolCompleted1() throws Exception {
        StudentInterface student = Mockito.mock(StudentInterface.class);
        when(student.getYearSchoolCompleted()).thenReturn(2006);

        Map<String, StudentErrorCode> errorCodeMap = validateStudent(student);

        Assertions.assertEquals(0, errorCodeMap.size());
    }

    @Test
    public void testIncorrectStudentYearSchoolCompleted2() throws Exception {
        StudentInterface student = Mockito.mock(StudentInterface.class);
        when(student.getYearSchoolCompleted()).thenReturn(Calendar.getInstance().get(Calendar.YEAR) + 1);

        Map<String, StudentErrorCode> errorCodeMap = validateStudent(student);

        Assertions.assertEquals(1, errorCodeMap.size());
    }

    @Test
    public void testIncorrectStudentYearSchoolCompleted3() throws Exception {
        StudentInterface student = Mockito.mock(StudentInterface.class);
        when(student.getYearSchoolCompleted()).thenReturn(Calendar.getInstance().get(Calendar.YEAR) -101);

        Map<String, StudentErrorCode> errorCodeMap = validateStudent(student);

        Assertions.assertEquals(1, errorCodeMap.size());
    }

    private Map<String, StudentErrorCode> validateStudent(StudentInterface student) {

        StudentValidator studentValidator = StudentValidator.valueOf(student);
        Map<String, StudentErrorCode> errorCodeMap = studentValidator.validate();
        return errorCodeMap;
    }
}
