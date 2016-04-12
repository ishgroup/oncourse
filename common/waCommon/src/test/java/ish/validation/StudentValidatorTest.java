package ish.validation;

import ish.oncourse.cayenne.ConcessionTypeInterface;
import ish.oncourse.cayenne.StudentConcessionInterface;
import ish.oncourse.cayenne.StudentInterface;
import org.apache.cayenne.validation.ValidationResult;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class StudentValidatorTest {

    @Test
    public void testCorrectStudentYearSchoolCompleted() throws Exception {
        StudentInterface student = Mockito.mock(StudentInterface.class);
        when(student.getYearSchoolCompleted()).thenReturn(2006);

        ValidationResult validationResult = validateStudent(student);

        assertEquals(0, validationResult.getFailures().size());
    }

    @Test
    public void testIncorrectStudentYearSchoolCompleted1() throws Exception {
        StudentInterface student = Mockito.mock(StudentInterface.class);
        when(student.getYearSchoolCompleted()).thenReturn(Calendar.getInstance().get(Calendar.YEAR) + 1);

        ValidationResult validationResult = validateStudent(student);

        assertEquals(1, validationResult.getFailures().size());
    }

    @Test
    public void testIncorrectStudentYearSchoolCompleted2() throws Exception {
        StudentInterface student = Mockito.mock(StudentInterface.class);
        when(student.getYearSchoolCompleted()).thenReturn(Calendar.getInstance().get(Calendar.YEAR) -101);

        ValidationResult validationResult = validateStudent(student);

        assertEquals(1, validationResult.getFailures().size());
    }

    @Test
    public void testValidateStudentConcessions() throws Exception {
        ConcessionTypeInterface concessionType = Mockito.mock(ConcessionTypeInterface.class);
        when(concessionType.getHasConcessionNumber()).thenReturn(true);

        StudentConcessionInterface studentConcession = Mockito.mock(StudentConcessionInterface.class);
        when(studentConcession.getConcessionType()).thenReturn(concessionType);
        when(studentConcession.getConcessionNumber()).thenReturn("111");

        StudentInterface student = Mockito.mock(StudentInterface.class);
        when(student.getConcessions()).thenReturn(Arrays.asList(studentConcession));
        when(student.getYearSchoolCompleted()).thenReturn(Calendar.getInstance().get(Calendar.YEAR));

        ValidationResult validationResult = validateStudent(student);

        assertEquals(0, validationResult.getFailures().size());
    }

    @Test
    public void testEmptyConcessionNumber() throws Exception {
        ConcessionTypeInterface concessionType = Mockito.mock(ConcessionTypeInterface.class);
        when(concessionType.getHasConcessionNumber()).thenReturn(true);

        StudentConcessionInterface studentConcession = Mockito.mock(StudentConcessionInterface.class);
        when(studentConcession.getConcessionType()).thenReturn(concessionType);
        when(studentConcession.getConcessionNumber()).thenReturn("");

        StudentInterface student = Mockito.mock(StudentInterface.class);
        when(student.getConcessions()).thenReturn(Arrays.asList(studentConcession));
        when(student.getYearSchoolCompleted()).thenReturn(Calendar.getInstance().get(Calendar.YEAR));

        ValidationResult validationResult = validateStudent(student);

        assertEquals(1, validationResult.getFailures().size());
        assertEquals("You need to enter a concession number since you have chosen a concession type.",
                validationResult.getFailures().get(0).getDescription());
    }

    private ValidationResult validateStudent(StudentInterface student) {
        ValidationResult validationResult = new ValidationResult();
        StudentValidator studentValidator = StudentValidator.valueOf(student, validationResult);
        studentValidator.validate();
        return validationResult;
    }
}
