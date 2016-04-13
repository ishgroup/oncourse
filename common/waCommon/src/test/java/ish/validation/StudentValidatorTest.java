package ish.validation;

import ish.oncourse.cayenne.StudentInterface;
import org.apache.cayenne.validation.ValidationResult;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class StudentValidatorTest {

    @Test
    public void testCorrectStudentYearSchoolCompleted1() throws Exception {
        StudentInterface student = Mockito.mock(StudentInterface.class);
        when(student.getYearSchoolCompleted()).thenReturn(2006);

        ValidationResult validationResult = validateStudent(student);

        assertEquals(0, validationResult.getFailures().size());
    }

    @Test
    public void testIncorrectStudentYearSchoolCompleted2() throws Exception {
        StudentInterface student = Mockito.mock(StudentInterface.class);
        when(student.getYearSchoolCompleted()).thenReturn(Calendar.getInstance().get(Calendar.YEAR) + 1);

        ValidationResult validationResult = validateStudent(student);

        assertEquals(1, validationResult.getFailures().size());
    }

    @Test
    public void testIncorrectStudentYearSchoolCompleted3() throws Exception {
        StudentInterface student = Mockito.mock(StudentInterface.class);
        when(student.getYearSchoolCompleted()).thenReturn(Calendar.getInstance().get(Calendar.YEAR) -101);

        ValidationResult validationResult = validateStudent(student);

        assertEquals(1, validationResult.getFailures().size());
    }

    private ValidationResult validateStudent(StudentInterface student) {
        ValidationResult validationResult = new ValidationResult();
        StudentValidator studentValidator = StudentValidator.valueOf(student, validationResult);
        studentValidator.validate();
        return validationResult;
    }
}
